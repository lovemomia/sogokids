package com.sogokids.service.course;

import com.sogokids.common.exception.SogoErrorException;
import com.sogokids.service.AbstractService;
import com.sogokids.service.payment.OrderService;
import com.sogokids.service.payment.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.util.List;

public class SubjectService extends AbstractService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectService.class);

    private OrderService orderService;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public Subject get(int subjectId) {
        String sql = "SELECT Id, CityId, Cover, Title, Intro, Rules FROM SG_Subject WHERE Id=? AND Status<>0";
        return queryObject(sql, new Object[] { subjectId }, Subject.class, Subject.NOT_EXIST_SUBJECT);
    }

    public long placeOrder(final long userId, final int type, final String name, final String mobile, final List<Price> prices) {
        try {
            return (Long) execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    long orderId = orderService.add(userId, type, name, mobile);
                    if (orderId <= 0) throw new SogoErrorException("下单失败");
                    for (Price price : prices) {
                        addUserPackage(userId, orderId, price);
                    }

                    return orderId;
                }
            });
        } catch (Exception e) {
            LOGGER.error("fail to place order for user: {}", userId, e);
        }

        return 0;
    }

    private void addUserPackage(long userId, long orderId, Price price) {
        for (int i = 0; i < price.getCount(); i++) {
            SubjectSku subjectSku = getSubjectSku(price.getRefId());
            if (!subjectSku.exist()) throw new SogoErrorException("无效的课程包");

            String sql = "INSERT INTO SG_UserPackage(UserId, OrderId, PriceId, SubjectId, SubjectSkuId, CourseCount, BookableCount, Status, AddTime) VALUES (?, ?, ?, ?, ?, ?, ?, 2, NOW())";
            if (!update(sql, new Object[] { userId,orderId, price.getId(), subjectSku.getSubjectId(), subjectSku.getId(), subjectSku.getCourseCount(), subjectSku.getCourseCount() }))
                throw new SogoErrorException("下单失败，无法创建课程包");
        }
    }

    private SubjectSku getSubjectSku(int subjectSkuId) {
        String sql = "SELECT Id, SubjectId, CourseCount FROM SG_SubjectSku WHERE Id=? AND Status<>0";
        return queryObject(sql, new Object[] { subjectSkuId }, SubjectSku.class, SubjectSku.NOT_EXIST_SUBJECT_SKU);
    }
}
