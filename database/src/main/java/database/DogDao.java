
package database;

import model.DogStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DogDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_STATS = "select * from DOG_STATS";

    private static final String INSERT = "INSERT INTO DOG_STATS (breed_name, wins, losses) VALUES (?,?,?) " +
            "ON DUPLICATE KEY UPDATE " +
            "wins = wins + VALUES(wins)," +
            "losses = losses + VALUES(losses);";

    public List<DogStat> getAllDogStats(){
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_STATS);
        List<DogStat> stats = new ArrayList<DogStat>();
        for (Map<String, Object> row : rows) {
            DogStat dogStat = new DogStat();
            dogStat.setBreedName((String)row.get("breed_name"));
            dogStat.setWins((Integer) row.get("wins"));
            dogStat.setLosses((Integer) row.get("losses"));

            stats.add(dogStat);
        }
        return stats;

    }

    public void insertDogStats(final List<DogStat> dogStats) {

        jdbcTemplate.batchUpdate(INSERT, new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DogStat dogStat = dogStats.get(i);
                ps.setString(1, dogStat.getBreedName());
                ps.setInt(2, dogStat.getWins());
                ps.setInt(3, dogStat.getLosses() );
            }

            public int getBatchSize() {
                return dogStats.size();
            }
        });

    }

}
