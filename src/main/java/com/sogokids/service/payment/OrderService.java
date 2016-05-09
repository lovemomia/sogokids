package com.sogokids.service.payment;

import com.sogokids.service.AbstractService;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderService extends AbstractService {
    public long add(final long userId, final int type, final String name, final String mobile) {
        return insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_Order(UserId, Type, Name, Mobile, AddTime) VALUES (?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, userId);
                ps.setInt(2, type);
                ps.setString(3, name);
                ps.setString(4, mobile);

                return ps;

            }
        }).getKey().longValue();
    }
}
