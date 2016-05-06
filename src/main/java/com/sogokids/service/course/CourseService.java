package com.sogokids.service.course;

import com.sogokids.exception.SogoErrorException;
import com.sogokids.exception.SogoException;
import com.sogokids.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.util.List;

public class CourseService extends AbstractService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);

    public Course get(int courseId) {
        String sql = "SELECT Id, SubjectId, Cover, Title, Age, Insurance, Joined, Detail, Tips FROM SG_Course WHERE Id=? AND Status<>0";
        Course course = queryObject(sql, new Object[] { courseId }, Course.class, Course.NOT_EXIST_COURSE);

        if (course.exists()) {
            course.setImgs(queryCourseImgs(courseId));
        }

        return course;
    }

    private List<String> queryCourseImgs(int courseId) {
        String sql = "SELECT Url FROM SG_CourseImg WHERE CourseId=? AND Status=1";
        return queryStringList(sql, new Object[] { courseId });
    }

    public List<Course> queryBySubject(int subjectId) {
        String sql = "SELECT Id, SubjectId, Cover, Title, Age, Joined FROM SG_Course WHERE SubjectId=? AND Status=1";
        return queryObjectList(sql, new Object[] { subjectId }, Course.class);
    }

    public List<CourseSku> querySkus(int courseId) {
        String sql = "SELECT A.Id, A.Courseid, A.Desc, A.CityId, A.RegionId, A.Address, A.StartTime, A.EndTime, A.Deadline, A.UnlockedStock AS Stock, B.Name AS Region " +
                "FROM SG_CourseSku A " +
                "INNER JOIN SG_Region B ON A.RegionId=B.Id " +
                "WHERE A.CourseId=? AND A.Deadline>NOW() AND A.Status=1 AND B.Status=1";
        return queryObjectList(sql, new Object[] { courseId }, CourseSku.class);
    }

    public CourseSku getSku(int courseSkuId) {
        String sql = "SELECT A.Id, A.Courseid, A.Desc, A.CityId, A.RegionId, A.Address, A.StartTime, A.EndTime, A.Deadline, A.UnlockedStock AS Stock, B.Name AS Region " +
                "FROM SG_CourseSku A " +
                "INNER JOIN SG_Region B ON A.RegionId=B.Id " +
                "WHERE A.Id=? AND A.Status<>0 AND B.Status=1";
        return queryObject(sql, new Object[] { courseSkuId }, CourseSku.class, CourseSku.NOT_EXIST_COURSE_SKU);
    }

    public long queryBookableCount(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_UserPackage A INNER JOIN SG_Subject B ON A.SubjectId=B.Id WHERE A.UserId=? AND A.BookableCount>0 AND A.Status=1 AND B.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    public List<UserPackage> queryBookable(long userId, int start, int count) {
        String sql = "SELECT A.Id, A.UserId, A.OrderId, A.PriceId, A.SubjectId, A.CourseCount, A.BookableCount, A.AddTime, B.Cover, B.Title " +
                "FROM SG_UserPackage A " +
                "INNER JOIN SG_Subject B ON A.SubjectId=B.Id " +
                "WHERE A.UserId=? AND A.BookableCount>0 AND A.Status=1 AND B.Status<>0 " +
                "ORDER BY AddTime " +
                "LIMIT ?,?";
        return queryObjectList(sql, new Object[] { userId, start, count }, UserPackage.class);
    }

    public UserPackage getUserPackage(long packageId) {
        String sql = "SELECT A.Id, A.UserId, A.OrderId, A.PriceId, A.SubjectId, A.CourseCount, A.BookableCount, A.AddTime, B.Cover, B.Title " +
                "FROM SG_UserPackage A " +
                "INNER JOIN SG_Subject B ON A.SubjectId=B.Id " +
                "WHERE A.Id=? AND A.Status=1 AND B.Status<>0";
        return queryObject(sql, new Object[] { packageId }, UserPackage.class, UserPackage.NOT_EXIST_USER_PACKAGE);
    }

    public long queryNotFinishedCount(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_UserBooked A " +
                "INNER JOIN SG_Course B ON A.CourseId=B.Id " +
                "INNER JOIN SG_CourseSku C ON A.CourseSkuId=C.Id " +
                "WHERE A.UserId=? AND A.Status=1 AND B.Status<>0 AND C.EndTime>NOW() AND C.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    public List<UserBooked> queryNotFinished(long userId, int start, int count) {
        String sql = "SELECT A.Id, A.UserId, A.ChildId, A.PackageId, A.CourseId, A.CourseSkuId, B.Cover, B.Title, C.StartTime, C.EndTime, C.Address FROM SG_UserBooked A " +
                "INNER JOIN SG_Course B ON A.CourseId=B.Id " +
                "INNER JOIN SG_CourseSku C ON A.CourseSkuId=C.Id " +
                "WHERE A.UserId=? AND A.Status=1 AND B.Status<>0 AND C.EndTime>NOW() AND C.Status<>0 " +
                "ORDER BY C.StartTime " +
                "LIMIT ?,?";
        return queryObjectList(sql, new Object[] { userId, start, count }, UserBooked.class);
    }

    public long queryFinishedCount(long userId) {
        String sql = "SELECT COUNT(1) FROM SG_UserBooked A " +
                "INNER JOIN SG_Course B ON A.CourseId=B.Id " +
                "INNER JOIN SG_CourseSku C ON A.CourseSkuId=C.Id " +
                "WHERE A.UserId=? AND A.Status=1 AND B.Status<>0 AND C.EndTime<=NOW() AND C.Status<>0";
        return queryLong(sql, new Object[] { userId });
    }

    public List<UserBooked> queryFinished(long userId, int start, int count) {
        String sql = "SELECT A.Id, A.UserId, A.ChildId, A.PackageId, A.CourseId, A.CourseSkuId, B.Cover, B.Title, C.StartTime, C.EndTime, C.Address FROM SG_UserBooked A " +
                "INNER JOIN SG_Course B ON A.CourseId=B.Id " +
                "INNER JOIN SG_CourseSku C ON A.CourseSkuId=C.Id " +
                "WHERE A.UserId=? AND A.Status=1 AND B.Status<>0 AND C.EndTime<=NOW() AND C.Status<>0 " +
                "ORDER BY C.StartTime " +
                "LIMIT ?,?";
        return queryObjectList(sql, new Object[] { userId, start, count }, UserBooked.class);
    }

    public UserBooked getUserBooked(long bookedId) {
        String sql = "SELECT A.Id, A.UserId, A.ChildId, A.PackageId, A.CourseId, A.CourseSkuId, B.Cover, B.Title, C.StartTime, C.EndTime, C.Address FROM SG_UserBooked A " +
                "INNER JOIN SG_Course B ON A.CourseId=B.Id " +
                "INNER JOIN SG_CourseSku C ON A.CourseSkuId=C.Id " +
                "WHERE A.Id=? AND A.Status=1 AND B.Status<>0 AND C.Status<>0";
        return queryObject(sql, new Object[] { bookedId }, UserBooked.class, UserBooked.NOT_EXIST_USER_BOOKED);
    }

    public boolean booking(final long userId, final long childId, final long packageId, final int courseId, final int courseSkuId) {
        try {
            execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    if (!lockSku(courseSkuId)) throw new SogoErrorException("选课失败，库存不足");
                    if (!decreaseBookable(packageId)) throw new SogoErrorException("选课失败");
                    if (!logBooked(userId, childId, packageId, courseId, courseSkuId)) throw new SogoErrorException("选课失败");
                    increaseJoined(courseId);

                    return null;
                }
            });
        } catch (SogoException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("booking error: {}/{}/{}/{}/{}", new Object[] { userId, childId, packageId, courseId, courseSkuId });
            return false;
        }

        return true;
    }

    private boolean lockSku(int courseSkuId) {
        String sql = "UPDATE SG_CourseSku SET UnlockedStock=UnlockedStock-1, LockedStock=LockedStock+1 WHERE Id=? AND Status=1 AND UnlockedStock>=1";
        return update(sql, new Object[] { courseSkuId });
    }

    private boolean decreaseBookable(long packageId) {
        String sql = "UPDATE SG_UserPackage SET BookableCount=BookableCount-1 WHERE Id=? AND Status=1 AND BookableCount>=1";
        return update(sql, new Object[] { packageId });
    }

    private boolean logBooked(long userId, long childId, long packageId, int courseId, int courseSkuId) {
        String sql = "INSERT INTO SG_UserBooked(UserId, ChildId, PackageId, CourseId, CourseSkuId, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
        return update(sql, new Object[] { userId, childId, packageId, courseId, courseSkuId });
    }

    private boolean increaseJoined(int courseId) {
        String sql = "UPDATE SG_Course SET Joined=Joined+1 WHERE Id=? AND Status=1";
        return update(sql, new Object[] { courseId });
    }

    public boolean cancel(final long userId, final long bookedId, final long packageId, final int courseId, final int courseSkuId) {
        try {
            execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    if (!unlockSku(courseSkuId)) throw new SogoErrorException("取消失败");
                    if (!increaseBookable(packageId)) throw new SogoErrorException("取消失败");
                    if (!deleteBooked(bookedId)) throw new SogoErrorException("取消失败");
                    decreaseJoined(courseId);

                    return null;
                }
            });
        } catch (SogoException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("cancel error: {}/{}/{}/{}/{}", new Object[] { userId, bookedId, packageId, courseId, courseSkuId });
            return false;
        }

        return true;
    }

    private boolean unlockSku(int courseSkuId) {
        String sql = "UPDATE SG_CourseSku SET UnlockedStock=UnlockedStock+1, LockedStock=LockedStock-1 WHERE Id=? AND Status=1 AND LockedStock>=1";
        return update(sql, new Object[] { courseSkuId });
    }

    private boolean increaseBookable(long packageId) {
        String sql = "UPDATE SG_UserPackage SET BookableCount=BookableCount+1 WHERE Id=? AND Status=1 AND BookableCount<CourseCount";
        return update(sql, new Object[] { packageId });
    }

    private boolean deleteBooked(long bookedId) {
        String sql = "UPDATE SG_UserBooked SET Status=0 WHERE Id=? AND Status=1";
        return update(sql, new Object[] { bookedId });
    }

    private boolean decreaseJoined(int courseId) {
        String sql = "UPDATE SG_Course SET Joined=Joined-1 WHERE Id=? AND Joined>0 AND Status=1";
        return update(sql, new Object[] { courseId });
    }
}
