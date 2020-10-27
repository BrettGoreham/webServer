package database;

import model.MealOption;
import model.vinmonopolet.AlcoholForSale;
import model.vinmonopolet.SortedMaxLengthList;
import model.vinmonopolet.VinmonopoletBatchJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Profile("vinmonopoletBatch")
public class VinmonopoletBatchDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static String insertIntoBatchTable = "INSERT INTO VINMONOPOLET_BATCH ( status, dateOfRun, lastDateRunWasCheckedAsStillValid, sizeOfOverallTopList, sizeOfCategoryTopList) VALUES (?,?,?,?,?)";

    private final static String insertIntoTopListRecords =
        "INSERT INTO VINMONOPOLET_BATCH_TOP_LIST_RECORDS " +
            "(fk_vinmonopoletBatchID, listCategory, vinmonopoletProductId, productName, productCategory, salePrice, saleVolume, alcoholPercentage, salePricePerLiter, salePricePerAlcoholUnit) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    private final static String updatePreviousBatch =
        "UPDATE VINMONOPOLET_BATCH SET lastDateRunWasCheckedAsStillValid = ? WHERE batchId = ?";

    private final static String getMostRecentBatch =
        "SELECT * FROM VINMONOPOLET_BATCH b " +
            "LEFT JOIN VINMONOPOLET_BATCH_TOP_LIST_RECORDS btlr " +
            "ON b.batchID = btlr.fk_vinmonopoletBatchID " +
            "WHERE b.lastDateRunWasCheckedAsStillValid = (select MAX(lastDateRunWasCheckedAsStillValid) from VINMONOPOLET_BATCH);";

    private final static String OVERALL_LIST_CATEGORY_NAME = "OVERALL";


    public void saveVinmonopoletBatchJob(VinmonopoletBatchJob vinmonopoletBatchJob) {

        int batchId= saveIntoBatchTable(vinmonopoletBatchJob);

        //Adding overallList here for ease of Saving when reading it will go into the appropriate place
        vinmonopoletBatchJob.getCategoryToAlcoholForSalePricePerAlcoholUnit().put(OVERALL_LIST_CATEGORY_NAME, vinmonopoletBatchJob.getOverallAlcoholForSalePricePerAlcoholUnit());
        saveTopLists(batchId, vinmonopoletBatchJob.getCategoryToAlcoholForSalePricePerAlcoholUnit());
    }

    public void updatePreviousBatchesValidDateToDate(LocalDate lastDateRunWasCheckedAsStillValid, long batchId) {
        jdbcTemplate.update(updatePreviousBatch, lastDateRunWasCheckedAsStillValid, batchId);
    }

    public VinmonopoletBatchJob fetchLastSuccessfulJob() {

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(getMostRecentBatch);
        
        return mapRowsToVinmonopoletBatch(rows);
    }

    private VinmonopoletBatchJob mapRowsToVinmonopoletBatch(List<Map<String, Object>> rows) {

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

            if (row.get("listCategory").equals(OVERALL_LIST_CATEGORY_NAME)) {
                topList.add(alcoholForSale);
            } else {
                alcoholForSaleList.add(alcoholForSale);
            }

        }

        assert vinmonopoletBatchJob != null;
        vinmonopoletBatchJob.addAlcoholsToTopLists(alcoholForSaleList);
        vinmonopoletBatchJob.setOverallAlcoholForSalePricePerAlcoholUnit(topList);
        //to lazy to add method that doesnt set top list while adding to regular list. so overwriting it above

        return vinmonopoletBatchJob;
    }

    private int saveIntoBatchTable(VinmonopoletBatchJob vinmonopoletBatchJob) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertIntoBatchTable, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, vinmonopoletBatchJob.getStatus());
            ps.setDate(2, Date.valueOf(vinmonopoletBatchJob.getDateOfRun()));
            ps.setDate(3, Date.valueOf(vinmonopoletBatchJob.getLastDateRunWasCheckedAsStillValid()));
            ps.setInt(4, vinmonopoletBatchJob.getSizeOfOverallTopList());
            ps.setInt(5, vinmonopoletBatchJob.getSizeOfCategoryTopList());

            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
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
                    ps.setDouble(10, toSave.getSalePricePerAlcoholUnit());

                }

                //(fk_vinmonopoletBatchID, listCategory, vinmonopoletProductId, productName, productCategory, salePrice, saleVolume, alcoholPercentage, salePricePerLiter, salePricePerAlcoholUnit)

                public int getBatchSize() {
                    return entry.getValue().size();
                }
            });
        }

    }
}
