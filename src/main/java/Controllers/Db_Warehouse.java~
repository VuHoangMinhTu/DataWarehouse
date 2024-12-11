package Controllers;

import Configs.JDBCConection;
import Utils.SendEmail;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Db_Warehouse {
    private JDBCConection jdbcConection = new JDBCConection();

    private SendEmail sendEmail = new SendEmail();
    public Db_Warehouse() {
        jdbcConection.connect("dw");
    }

    public void loadDataFromMainStagingToTempDW() {
        String procedureCall = "{CALL MigrateDailyStagingToTempDW()}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
            // Thực thi procedure
            callableStatement.execute();
            System.out.println("Procedure MigrateDailyStagingToTempDW executed successfully!");

        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing stored procedure: " + e);
            throw new RuntimeException("Error executing stored procedure", e);
        }
    }

    public void loadDataFromTempDWToMainDW() {
        String procedureCall = "{CALL transform_data_from_tempDW_to_DW()}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
            // Thực thi procedure
            callableStatement.execute();
            System.out.println("Procedure transform_data_from_tempDW_to_DW executed successfully!");

        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing stored procedure: " + e);
            throw new RuntimeException("Error executing stored procedure", e);
        }
    }

    public void loadDataFromDWtoAggregateFilter() {
        String queryDrop = "DROP TABLE if EXISTS aggregate_filter_cameras;";

        String queryCreate = "CREATE TABLE aggregate_filter_cameras (\n" +
                "    sk BIGINT, \n" +
                "    id varchar(255), \n" +
                "    name TEXT, \n" +
                "    brand TEXT, \n" +
                "    actual_price double, \n" +
                "    sale_price double, \n" +
                "    images TEXT, \n" +
                "    video_url TEXT\n" +
                ");";

        String queryInsert = "INSERT INTO aggregate_filter_cameras (\n" +
                "    sk, \n" +
                "    id, \n" +
                "    name, \n" +
                "    brand,\n" +
                "    actual_price, \n" +
                "    sale_price, \n" +
                "    images, \n" +
                "    video_url\n" +
                ")\n" +
                "SELECT \n" +
                "    sk, \n" +
                "    id, \n" +
                "    name, \n" +
                "    brand,\n" +
                "    actual_price, \n" +
                "    sale_price, \n" +
                "    images, \n" +
                "    video_url_description\n" +
                "FROM kyma_camera_dw;";

        try (Connection connection = jdbcConection.getConnection();
             PreparedStatement dropStmt = connection.prepareStatement(queryDrop);
             PreparedStatement createStmt = connection.prepareStatement(queryCreate);
             PreparedStatement insertStmt = connection.prepareStatement(queryInsert)) {

            dropStmt.execute();
            createStmt.execute();
            insertStmt.execute();

            System.out.println("Success to load data to Aggregate table");
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing load data to Aggregate table: " + e);
            throw new RuntimeException("Error executing load data to Aggregate table", e);
        }
    }



    public static void main(String[] args) {
        Db_Warehouse test = new Db_Warehouse();
        test.loadDataFromDWtoAggregateFilter();
    }


}
