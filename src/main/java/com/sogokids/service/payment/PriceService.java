package com.sogokids.service.payment;

import com.sogokids.service.AbstractService;

import java.util.List;

public class PriceService extends AbstractService {
    public List<Price> queryBySubject(int subjectId) {
        String sql = "SELECT A.Id, A.RefType, A.RefId, A.Price, A.Desc " +
                "FROM SG_Price A " +
                "INNER JOIN SG_SubjectSku B ON A.RefId=B.SubjectId " +
                "WHERE A.RefType=? AND B.SubjectId=? AND A.Status=1 AND B.Status=1";
        return queryObjectList(sql, new Object[] { Price.Type.SUBJECT_PACKAGE, subjectId }, Price.class);
    }
}
