package database;

import model.MealOption;
import model.StatusEnum;
import model.user.Roles;
import model.user.SimpleUser;
import model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static final String insertUser = "INSERT INTO USERS (username, password, email, role_list) VALUES (?, ?, ?, ?);";
    static final String selectUserByUserName = "select * from USERS where username = ?;";
    static final String selectUserById= "select id, username, role_list from USERS where id = ?;";
    static final String selectSimpleUser = "select id, username, role_list from USERS";
    static final String createUserConfirmationToken = "INSERT INTO USER_CONFIRMATION_TOKENS(fk_user, token) VALUES (?, ?);";
    static final String getUserToConfirmationFromToken = "SELECT fk_user FROM USER_CONFIRMATION_TOKENS where token = ?";

    static final String SetUserRoleList = "UPDATE USERS set role_list = ? where id = ?";
    static final String enableUser = "UPDATE USERS set enabled = 1 where id = ?";
    static final String deleteTokenByUserId = "DELETE FROM USER_CONFIRMATION_TOKENS where fk_user = ?";
    /**
     * CREATE TABLE USER_CONFIRMATION_TOKENS(
     *     id INT NOT NULL AUTO_INCREMENT,
     *     fk_user INT NOT NULL,
     *     token VARCHAR(36) NOT NULL,
     *     registration_time DATETIME NOT NULL,
     *     CONSTRAINT UC_user_id_fk UNIQUE (fk_user),
     *     PRIMARY KEY (id),
     *     FOREIGN KEY (fk_user) REFERENCES USERS(id)
     * )
     *  id INT NOT NULL AUTO_INCREMENT,
     *     username VARCHAR(20) NOT NULL,
     *     password VARCHAR(60) NOT NULL,
     *     email VARCHAR(50) NOT NULL,
     *     role_list VARCHAR(20),
     *     enabled boolean DEFAULT 0,
     *
     */

    public User getUserByUsername(String username){

        return jdbcTemplate.queryForObject(
            selectUserByUserName,
            new Object[]{username},
            (rs, rownum) ->
                new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getBoolean("enabled"),
                    DatabaseUtil.splitCommaDelimatedStringFromDatabase(
                        rs.getString("role_list")
                    ).stream().map(Roles::valueOf).collect(Collectors.toList())
                )
        );
    }

    public List<SimpleUser> getUserList() {

        List<SimpleUser> users = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(selectSimpleUser);


        for (Map<String, Object> row : rows)  {
            SimpleUser user = new SimpleUser(
                    (int) row.get("id"),
                    (String) row.get("username"),
                    DatabaseUtil.splitCommaDelimatedStringFromDatabase(
                            (String) row.get("role_list")
                    ).stream().map(Roles::valueOf).collect(Collectors.toList())

            );

            users.add(user);
        }
        users.sort(Comparator.comparing(SimpleUser::getUsername));

        return users;

    }

    public int createUser(User user) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
            connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);

                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, DatabaseUtil.createCommaDelimatedStringForDatabase(user.getRoles()));
                return ps;
            },
            keyHolder
        );

        return keyHolder.getKey().intValue();
    }


    public String createConfirmationToken(int userId) {
        String token = UUID.randomUUID().toString();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection
                            .prepareStatement(createUserConfirmationToken);

                    ps.setInt(1, userId);
                    ps.setString(2, token);
                    return ps;
                }
        );

        return token;
    }

    public Integer getUserByConfirmationToken(String token) {
        return jdbcTemplate.queryForObject(getUserToConfirmationFromToken, Integer.class, token);
    }

    public void enableUserAndDeleteConfirmationToken(int userId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(enableUser);

            ps.setInt(1, userId);
            return ps;
        });

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(deleteTokenByUserId);

            ps.setInt(1, userId);
            return ps;
        });
    }
    public SimpleUser getUserById(int id){

        return jdbcTemplate.queryForObject(
                selectUserById,
                (rs, rownum) ->
                        new SimpleUser(
                                rs.getLong("id"),
                                rs.getString("username"),
                                DatabaseUtil.splitCommaDelimatedStringFromDatabase(
                                        rs.getString("role_list")
                                ).stream().map(Roles::valueOf).collect(Collectors.toList())
                        ),

                id);
    }

    public void SetRoleListForUser(int id, List<Roles> roles) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(SetUserRoleList);

            ps.setString(1, DatabaseUtil.createCommaDelimatedStringForDatabase(roles));
            ps.setInt(2, id);
            return ps;
        });
    }
}
