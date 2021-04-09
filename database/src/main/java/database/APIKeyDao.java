package database;

import model.user.TwoFaToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.UUID;

@Repository
public class APIKeyDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CHECK_API_KEY_EXISTS_SQL = "SELECT EXISTS(SELECT * FROM API_KEYS where api_key_string = ?);";
    private final String CHECK_API_KEY_EXISTS_FOR_USER_SQL = "SELECT EXISTS(SELECT * FROM API_KEYS where fk_user_id = ?);";
    private final String INSERT_API_KEY = "INSERT INTO API_KEYS ( api_key_string, fk_user_id) VALUES (?, ?);";
    private final String UPDATE_API_KEY = "UPDATE API_KEYS SET api_key_string = ? WHERE fk_user_id = ?;";
    private final String DELETE_API_KEY = "DELETE FROM API_KEYS WHERE api_key_string = ? AND fk_user_id = ?;"; //adding api key here incase i add support for having multiple apikeys per user

    private final String FETCH_API_KEY_FOR_USER = "SELECT api_key_string FROM API_KEYS where fk_user_id = ?;";
    private final String FETCH_USER_FOR_API_KEY = "SELECT u.id FROM USERS u LEFT JOIN API_KEYS ak ON ak.fk_user_id = u.id where ak.api_key_string = ?;";

    private final String INSERT_OR_UPDATE_USAGE = "INSERT INTO API_KEY_USAGE (fk_user_id, usage_count, last_usage) VALUES(?, 1, now()) " +
            "ON DUPLICATE KEY UPDATE usage_count = usage_count + 1, last_usage = now();";

    private final String INSERT_NEW_TOKEN = "INSERT INTO TWO_FACTOR_AUTHENTICATION_TOKENS (fk_user_id, token, expiry_time, external_user_id) VALUES(:userId, :token, :expiryTime, :externalUserId) " +
            "ON DUPLICATE KEY UPDATE expiry_time = :expiryTime, token = :token;";

    private final String CHECK_TOKEN_EXISTS_AND_NOT_EXPIRED = "SELECT EXISTS(SELECT * FROM TWO_FACTOR_AUTHENTICATION_TOKENS WHERE expiry_time > now() AND fk_user_id = ? AND external_user_id = ? AND token = ?);";
    private final String DELETE_TOKEN = "DELETE FROM TWO_FACTOR_AUTHENTICATION_TOKENS WHERE fk_user_id = ? AND external_user_id = ? AND token = ?;";

    private final String FETCH_ALL_OPEN_TOKENS = "SELECT * FROM TWO_FACTOR_AUTHENTICATION_TOKENS WHERE fk_user_id = ?";

    //if a user already has a key this will overwrite that key if not it will create a new record
    public UUID generateAndSaveUniqueApiKey(long userId) {
        boolean isUnique = false;
        UUID apiKey = null;
        //generate uuids until find one that is not in the database. in reality this will be basically everytime
        while (!isUnique) {
            apiKey = UUID.randomUUID();
            isUnique = !doesApiKeyExist(apiKey);
        }

        if (doesApiKeyExistForUser(userId)) {
            jdbcTemplate.update(UPDATE_API_KEY, apiKey.toString(), userId);
        } else {
            jdbcTemplate.update(INSERT_API_KEY, apiKey.toString(), userId);
        }

        return apiKey;

    }

    public UUID getApiKeyForUser(long userId) {
        try {
            return jdbcTemplate.queryForObject(FETCH_API_KEY_FOR_USER, UUID.class, userId);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }



    public boolean doesApiKeyExist(UUID uuid) {
        return jdbcTemplate.queryForObject(CHECK_API_KEY_EXISTS_SQL, Boolean.class, uuid.toString());
    }

    private boolean doesApiKeyExistForUser(long userId) {
        return jdbcTemplate.queryForObject(CHECK_API_KEY_EXISTS_FOR_USER_SQL, Boolean.class, userId);
    }

    public void deleteApiKeyForUser(UUID apiKey, long id) {
        jdbcTemplate.update(DELETE_API_KEY, apiKey.toString(), id);
    }


    public long getUserIdFromApiKey(UUID apiKey) {
        return jdbcTemplate.queryForObject(FETCH_USER_FOR_API_KEY, Long.class, apiKey.toString());
    }

    public void increaseUsage(long userId) {
        jdbcTemplate.update(INSERT_OR_UPDATE_USAGE, userId);
    }

    public void insertToken(TwoFaToken twoFaToken) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", twoFaToken.getUserId());
        parameters.addValue("token", twoFaToken.getToken());
        parameters.addValue("expiryTime", twoFaToken.getExpiryTime());
        parameters.addValue("externalUserId", twoFaToken.getExternalUserId());

        namedParameterJdbcTemplate.update(INSERT_NEW_TOKEN, parameters);
    }

    public Boolean authenticateToken(long userId, String externalUserId, String token) {
        Boolean authenticated = jdbcTemplate.queryForObject(CHECK_TOKEN_EXISTS_AND_NOT_EXPIRED, Boolean.class, userId, externalUserId, token);

        if (authenticated != null && authenticated ) {
            jdbcTemplate.update(DELETE_TOKEN, userId, externalUserId, token);
        }
        return authenticated;
    }

    public List<TwoFaToken> getAllTokensForUser(long userId) {
        return jdbcTemplate.query(
                FETCH_ALL_OPEN_TOKENS,
                (resultSet, i) -> createTwoFactor(resultSet),
                userId
        );
    }

    private TwoFaToken createTwoFactor(ResultSet resultSet) throws SQLException {
        return new TwoFaToken(
                resultSet.getLong("fk_user_id"),
                resultSet.getString("token"),
                resultSet.getTimestamp("expiry_time").toLocalDateTime(),
                resultSet.getString("external_user_id")
        );
    }


}
