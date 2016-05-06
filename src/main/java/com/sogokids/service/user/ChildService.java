package com.sogokids.service.user;

import com.sogokids.service.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildService extends AbstractService {
    public long add(final Child child) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_Child (UserId, Avatar, Name, Sex, Birthday, AddTime) VALUES (?, ?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, child.getUserId());
                ps.setString(2, child.getAvatar());
                ps.setString(3, child.getName());
                ps.setString(4, child.getSex());
                ps.setDate(5, new java.sql.Date(child.getBirthday().getTime()));

                return ps;
            }
        });

        return keyHolder.getKey().longValue();
    }

    public boolean update(Child child) {
        String sql = "UPDATE SG_Child SET Avatar=?, Name=?, Sex=?, Birthday=? WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { child.getAvatar(), child.getName(), child.getSex(), child.getBirthday(), child.getUserId(), child.getId() });
    }

    public boolean delete(long userId, long childId) {
        String sql = "UPDATE SG_Child SET Status=0 WHERE UserId=? AND Id=?";
        return update(sql, new Object[] { userId, childId });
    }

    public Map<Long, List<Child>> queryByUsers(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new HashMap<Long, List<Child>>();

        Map<Long, List<Child>> childrenMap = new HashMap<Long, List<Child>>();
        for (long userId : userIds) {
            childrenMap.put(userId, new ArrayList<Child>());
        }

        String sql = String.format("SELECT Id FROM SG_Child WHERE UserId IN (%s) AND Status=1", StringUtils.join(userIds, ","));
        List<Child> children = list(queryLongList(sql));

        for (Child child : children) {
            childrenMap.get(child.getUserId()).add(child);
        }

        return childrenMap;
    }

    private List<Child> list(Collection<Long> childrenIds) {
        String sql = "SELECT Id, UserId, Avatar, Name, Sex, Birthday FROM SG_Child WHERE Id IN (%s) AND Status=1";
        return listByIds(sql, childrenIds, Long.class, Child.class);
    }
}
