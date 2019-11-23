package database;

import model.user.Roles;
import model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.stream.Collectors;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static final String insertUser = "INSERT INTO USERS (username, password, email, role_list) VALUES (?, ?, ?, ?);";
    static final String selectUserByUserName = "select * from USERS where username = ?;";

    /**
     *  id INT NOT NULL AUTO_INCREMENT,
     *     username VARCHAR(20) NOT NULL,
     *     password VARCHAR(60) NOT NULL,
     *     email VARCHAR(50) NOT NULL,
     *     role_list VARCHAR(20),
     *     enabled boolean DEFAULT 0,
     *
     */
    public User getUserById() {
        return new User();
    }

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
                    DatabaseUtil.splitCommaDelimatedStringFromDatabase(
                        rs.getString("role_list")
                    ).stream().map(x -> Roles.valueOf(x)).collect(Collectors.toList())
                )
        );
    }

    public void createUser(User user) {
        jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                    .prepareStatement(insertUser);

                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, DatabaseUtil.createCommaDelimatedStringForDatabase(user.getRoles()));
                return ps;
            }
        );
    }


}
