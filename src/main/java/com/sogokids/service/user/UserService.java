package com.sogokids.service.user;

import com.google.common.collect.Sets;
import com.sogokids.config.Configuration;
import com.sogokids.exception.SogoErrorException;
import com.sogokids.exception.SogoLoginException;
import com.sogokids.service.AbstractService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserService extends AbstractService {
    private ChildService childService;

    public void setChildService(ChildService childService) {
        this.childService = childService;
    }

    public boolean exists(String field, String value) {
        String sql = "SELECT COUNT(1) FROM SG_User WHERE " + field + "=?";
        return queryInt(sql, new Object[] { value }) > 0;
    }

    public long register(final String nickName, final String mobile, final String password) {
        KeyHolder keyHolder = insert(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "INSERT INTO SG_User(NickName, Mobile, Password, Token, AddTime) VALUES (?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, nickName);
                ps.setString(2, mobile);
                ps.setString(3, encryptPassword(mobile, password));
                ps.setString(4, generateToken(mobile));

                return ps;
            }
        });

        return keyHolder.getKey().longValue();
    }

    private String encryptPassword(String mobile, String password) {
        return new String(new Base64().encode(DigestUtils.md5(StringUtils.join(new String[] { mobile, password, Configuration.getString("SecretKey.Password") }, "|"))));
    }

    private String generateToken(String mobile) {
        return DigestUtils.md5Hex(StringUtils.join(new String[] { mobile, new Date().toString(), Configuration.getString("SecretKey.UToken") }, "|"));
    }

    public User login(String mobile, String password) {
        User user = getByMobile(mobile);
        if (!user.exists()) throw new SogoErrorException("用户不存在，请先注册");

        String sql = "SELECT COUNT(1) FROM SG_User WHERE Mobile=? AND Password=?";
        if (queryInt(sql, new Object[] { mobile, encryptPassword(mobile, password) }) != 1) throw new SogoErrorException("登录失败，密码不正确");

        return user;
    }

    public User updatePassword(String mobile, String password) {
        String sql = "UPDATE SG_User SET Password=? WHERE Mobile=?";
        if (!update(sql, new Object[] { encryptPassword(mobile, password), mobile })) throw new SogoErrorException("更改密码失败");
        return getByMobile(mobile);
    }

    public User get(long userId) {
        List<User> users = list(Sets.newHashSet(userId));
        return users.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    public User getByMobile(String mobile) {
        String sql = "SELECT Id FROM SG_User WHERE Mobile=? AND Status=1";
        List<User> users = list(queryLongList(sql, new Object[] { mobile }));
        return users.isEmpty() ? User.NOT_EXIST_USER : users.get(0);
    }

    public User getByToken(String token) {
        String sql = "SELECT Id FROM SG_User WHERE Token=? AND Status=1";
        List<User> users = list(queryLongList(sql, new Object[] { token }));
        if (users.isEmpty()) throw new SogoLoginException();
        return users.get(0);
    }

    public List<User> list(Collection<Long> userIds) {
        if (userIds.isEmpty()) return new ArrayList<User>();

        String sql = "SELECT Id, NickName, Avatar, Sex, Address, Token, Mobile FROM SG_User WHERE Id IN (%s) AND Status=1";
        List<User> users = listByIds(sql, userIds, Long.class, User.class);

        Map<Long, List<Child>> childrenMap = childService.queryByUsers(userIds);
        for (User user : users) {
            user.setChildren(childrenMap.get(user.getId()));
        }

        return users;
    }

    public boolean updateNickName(long userId, String nickName) {
        String sql = "UPDATE SG_User SET NickName=? WHERE Id=?";
        return update(sql, new Object[] { nickName, userId });
    }

    public boolean updateAvatar(long userId, String avatar) {
        String sql = "UPDATE SG_User SET Avatar=? WHERE Id=?";
        return update(sql, new Object[] { avatar, userId });
    }

    public boolean updateSex(long userId, String sex) {
        String sql = "UPDATE SG_User SET Sex=? WHERE Id=?";
        return update(sql, new Object[] { sex, userId });
    }

    public boolean updateAddress(long userId, String address) {
        String sql = "UPDATE SG_User SET Address=? WHERE Id=?";
        return update(sql, new Object[] { address, userId });
    }
}
