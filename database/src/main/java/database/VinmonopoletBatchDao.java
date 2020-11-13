package database;

import model.vinmonopolet.AlcoholForSale;
import model.vinmonopolet.SortedMaxLengthList;
import model.vinmonopolet.VinmonopoletBatchJob;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Repository
public class VinmonopoletBatchDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static String insertIntoBatchTable = "INSERT INTO VINMONOPOLET_BATCH ( status, dateOfRun, lastDateRunWasCheckedAsStillValid, sizeOfOverallTopList, sizeOfCategoryTopList) VALUES (?,?,?,?,?)";

    private final static String insertIntoTopListRecords =
        "INSERT INTO VINMONOPOLET_BATCH_TOP_LIST_RECORDS " +
            "(fk_vinmonopoletBatchID, listCategory, vinmonopoletProductId, productName, productCategory, salePrice, saleVolume, alcoholPercentage, salePricePerLiter, salePricePerAlcoholLiter, changeInRanking) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    private final static String updatePreviousBatch =
        "UPDATE VINMONOPOLET_BATCH SET lastDateRunWasCheckedAsStillValid = ? WHERE batchId = ?";

    private final static String getMostRecentBatch =
        "SELECT * FROM VINMONOPOLET_BATCH b " +
            "LEFT JOIN VINMONOPOLET_BATCH_TOP_LIST_RECORDS btlr " +
            "ON b.batchID = btlr.fk_vinmonopoletBatchID " +
            "WHERE b.status = 'COMPLETE' " +
            "AND b.lastDateRunWasCheckedAsStillValid = (select MAX(lastDateRunWasCheckedAsStillValid) from VINMONOPOLET_BATCH);";


    private final static String getBatchBetweenDates =
        "SELECT * FROM VINMONOPOLET_BATCH b " +
            "LEFT JOIN VINMONOPOLET_BATCH_TOP_LIST_RECORDS btlr " +
            "ON b.batchID = btlr.fk_vinmonopoletBatchID " +
            "WHERE ? BETWEEN b.dateOfRun AND b.lastDateRunWasCheckedAsStillValid " +
            "AND b.status = 'COMPLETE'";

    private final static String getEarliestBatchInDatabase =
        "SELECT MIN(b.dateOfRun) from VINMONOPOLET_BATCH b" +
            " where b.status = 'COMPLETE'";

    private final static String getLatestBatchInDatabase =
        "SELECT MAX(b.lastDateRunWasCheckedAsStillValid) from VINMONOPOLET_BATCH b" +
            " where b.status = 'COMPLETE'";

    private final static String getAllCategoriesEver =
        "SELECT DISTINCT btlr.productCategory FROM VINMONOPOLET_BATCH_TOP_LIST_RECORDS btlr";

    private final static String getAllCategoriesFromDate =
        "Select Distinct btlr.productCategory " +
            "FROM VINMONOPOLET_BATCH b " +
            "LEFT JOIN VINMONOPOLET_BATCH_TOP_LIST_RECORDS btlr " +
                "ON b.batchID = btlr.fk_vinmonopoletBatchID " +
                "WHERE ? BETWEEN b.dateOfRun AND b.lastDateRunWasCheckedAsStillValid " +
                "AND b.status = 'COMPLETE'";

    private final static String OVERALL_LIST_CATEGORY_NAME = "OVERALL";

    public void saveVinmonopoletBatchJob(VinmonopoletBatchJob vinmonopoletBatchJob) {

        int batchId= saveIntoBatchTable(vinmonopoletBatchJob);

        //Adding overallList here for ease of Saving when reading it will go into the appropriate place
        vinmonopoletBatchJob.getCategoryToAlcoholForSalePricePerAlcoholLiter().put(OVERALL_LIST_CATEGORY_NAME, vinmonopoletBatchJob.getOverallAlcoholForSalePricePerAlcoholLiter());
        saveTopLists(batchId, vinmonopoletBatchJob.getCategoryToAlcoholForSalePricePerAlcoholLiter());
    }

    public void updatePreviousBatchesValidDateToDate(LocalDate lastDateRunWasCheckedAsStillValid, long batchId) {
        jdbcTemplate.update(updatePreviousBatch, Timestamp.from(toUTCDateOfSameDate(lastDateRunWasCheckedAsStillValid)), batchId);
    }

    public VinmonopoletBatchJob fetchLastSuccessfulJob() {

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(getMostRecentBatch);
        
        return mapRowsToVinmonopoletBatch(rows);
    }

    public VinmonopoletBatchJob fetchBatchJobFromDate(java.util.Date date) {

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(getBatchBetweenDates, Date.from(date.toInstant()));

        return mapRowsToVinmonopoletBatch(rows);
    }

    public LocalDate fetchFirstRunDate() {
        Date date = jdbcTemplate.queryForObject(getEarliestBatchInDatabase, Date.class);

        return date.toLocalDate();
    }

    public LocalDate fetchLastRunDate() {
        Date date = jdbcTemplate.queryForObject(getLatestBatchInDatabase, Date.class);

        return date.toLocalDate();
    }

    public List<String> fetchAllCategoriesInDatabase() {
        return jdbcTemplate.queryForList(getAllCategoriesEver, String.class);
    }

    public List<String> fetchAllCatgoriesFromDate(java.util.Date date) {
        return jdbcTemplate.queryForList(getAllCategoriesFromDate, String.class, Date.from(date.toInstant()));
    }

    private VinmonopoletBatchJob mapRowsToVinmonopoletBatch(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return null;
        }

        VinmonopoletBatchJob vinmonopoletBatchJob = null;

        SortedMaxLengthList<AlcoholForSale> topList = null;
        List<AlcoholForSale> alcoholForSaleList = new ArrayList<>();
        for (Map<String, Object> row : rows) {

            if (vinmonopoletBatchJob == null) {
                vinmonopoletBatchJob = new VinmonopoletBatchJob(
                    (Integer) row.get("batchId"),
                    ((Date) row.get("dateOfRun")).toLocalDate(),
                    ((Date) row.get("lastDateRunWasCheckedAsStillValid")).toLocalDate(),
                    (String) row.get("status"),
                    (Integer) row.get("sizeOfOverallTopList"),
                    (Integer) row.get("sizeOfCategoryTopList")
                );

                topList = new SortedMaxLengthList<>(vinmonopoletBatchJob.getSizeOfOverallTopList(), vinmonopoletBatchJob.getAlcoholForSaleComparator());
            }

            AlcoholForSale alcoholForSale = new AlcoholForSale(
                (String) row.get("productName"),
                (String) row.get("productCategory"),
                (String) row.get("vinmonopoletProductId"),
                null,
                (Double) row.get("salePrice"),
                (Double) row.get("saleVolume"),
                (Double) row.get("alcoholPercentage")
            );

            alcoholForSale.setChangeInRanking((String) row.get("changeInRanking"));

            if (row.get("listCategory").equals(OVERALL_LIST_CATEGORY_NAME)) {
                topList.add(alcoholForSale);
            } else {
                alcoholForSaleList.add(alcoholForSale);
            }

        }

        vinmonopoletBatchJob.addAlcoholsToTopLists(alcoholForSaleList);
        vinmonopoletBatchJob.setOverallAlcoholForSalePricePerAlcoholLiter(topList);
        //to lazy to add method that doesnt set top list while adding to regular list. so overwriting it above

        return vinmonopoletBatchJob;
    }

    private int saveIntoBatchTable(VinmonopoletBatchJob vinmonopoletBatchJob) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertIntoBatchTable, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, vinmonopoletBatchJob.getStatus());
            ps.setTimestamp(2, Timestamp.from(toUTCDateOfSameDate(vinmonopoletBatchJob.getLastDateRunWasCheckedAsStillValid())));
            ps.setTimestamp(3, Timestamp.from(toUTCDateOfSameDate(vinmonopoletBatchJob.getLastDateRunWasCheckedAsStillValid())));
            ps.setInt(4, vinmonopoletBatchJob.getSizeOfOverallTopList());
            ps.setInt(5, vinmonopoletBatchJob.getSizeOfCategoryTopList());

            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    //mysql is annoying and saves dates in utc. since norway is +1 or +2
    // from utc or whatever this saves the day before always
    // to override im going to add 2 hours to it i guess
    //this and using time stamp when saving it seemed to solve it.
    // unless it creates a problem it will stay like this.
    private Instant toUTCDateOfSameDate(LocalDate localDate) {
        return
            ZonedDateTime.of(
                    localDate.getYear(),
                    localDate.getMonthValue(),
                    localDate.getDayOfMonth(),
                    0,0,0,0, ZoneId.of("UTC")
                )
                .toInstant();
    }

    private void saveTopLists(int batchId, Map<String, SortedMaxLengthList<AlcoholForSale>> topListsToSave) {

        for (Map.Entry<String, SortedMaxLengthList<AlcoholForSale>> entry : topListsToSave.entrySet()) {

            jdbcTemplate.batchUpdate(insertIntoTopListRecords, new BatchPreparedStatementSetter() {

                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    AlcoholForSale toSave = (AlcoholForSale) entry.getValue().get(i);
                    ps.setInt(1, batchId);
                    ps.setString(2, entry.getKey());
                    ps.setString(3, toSave.getVinmonopoletProductId());
                    ps.setString(4, toSave.getName());
                    ps.setString(5, toSave.getCategory());
                    ps.setDouble(6, toSave.getSalePrice());
                    ps.setDouble(7, toSave.getSaleVolume());
                    ps.setDouble(8, toSave.getAlcoholPercentage());
                    ps.setDouble(9, toSave.getSalePricePerLiter());
                    ps.setDouble(10, toSave.getSalePricePerAlcoholLiter());
                    ps.setString(11, toSave.getChangeInRanking());
                }

                //(fk_vinmonopoletBatchID, listCategory, vinmonopoletProductId, productName, productCategory, salePrice, saleVolume, alcoholPercentage, salePricePerLiter, salePricePerAlcoholUnit)

                public int getBatchSize() {
                    return entry.getValue().size();
                }
            });
        }

    }
}
