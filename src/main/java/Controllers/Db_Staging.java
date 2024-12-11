package Controllers;

import Configs.JDBCConection;
import Utils.SendEmail;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Db_Staging {
    private JDBCConection jdbcConection = new JDBCConection();
    private SendEmail sendEmail = new SendEmail();
    public  Db_Staging(){
        jdbcConection.connect("staging");
    }
    public boolean loadDataFromCSVToTempStaging(String sourceFileLocation,String fileName) {
        String sql = "LOAD DATA INFILE '" + sourceFileLocation+"/"+fileName.replace("\\", "/") + "' " +
                "INTO TABLE temp_kyma_cameras " +
                "FIELDS TERMINATED BY ',' " +
                "ENCLOSED BY '\"' " +
                "LINES TERMINATED BY '\\r\\n' " +
                "IGNORE 1 LINES " +
                "(id, name, link, actual_price, sale_price, brand, images, text_description, " +
                "video_url_description, outstanding_features, image_spec, lighting_spec, " +
                "video_spec, focus_spec, screen_spec, viewfinder_spec, storage_connect_spec, " +
                "flash_spec, physic_spec, lens_spec, date)";

        try (Connection connection = jdbcConection.getConnection();
             Statement statement = connection.createStatement()) {

            // Thực thi câu lệnh LOAD DATA
            int rows = statement.executeUpdate(sql);
            System.out.println("Successfully loaded " + rows + " rows into temp_kyma_cameras");
            return true;
        } catch (Exception e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing LOAD DATA INFILE: " + e);
            throw new RuntimeException("Error executing LOAD DATA INFILE", e);

        }
    }
    public void loadDataFromTempStagingToMainStaging(){
        String procedureCall = "{CALL insert_data_to_main_Staging()}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
            callableStatement.execute();
            System.out.println("Load data from temp_staging to main_staging successfully!");
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing procedur insert_data_to_main_Staging: " + e);
            throw new RuntimeException("Error executing procedur insert_data_to_main_Staging", e);
        }
    }
    public void truncateTableOfStaging(String tableName) {
        String sql = "TRUNCATE TABLE " + tableName;
        try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
             Statement statement = connection.createStatement()) {
            // Thực thi truy vấn
            statement.execute(sql);
            System.out.println("Delete all old data in table "+tableName+" successfully !");;
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error truncating table: " + e);
            throw new RuntimeException("Error truncating table", e);
        }
    }




}
