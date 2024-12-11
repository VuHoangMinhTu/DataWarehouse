package Controllers;

import Configs.JDBCConection;
import Utils.SendEmail;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Db_Control {
    private JDBCConection jdbcConection = new JDBCConection();
    public static final String sourceFileLocation = "E:\\CrawlMayAnhTemp";
    private SendEmail sendEmail = new SendEmail();


    public Db_Control(){
        jdbcConection.connect("control");
    }
//    Luồng đầu tiên 1
    public void checkFileCSVInLog(){
        File directory = new File(sourceFileLocation);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (checkFile(fileName)) {
                String sourceFileLocationTemp = sourceFileLocation.replace("\\", "\\\\");
                // File chưa tồn tại,
                int idConfig = getIdConfig(sourceFileLocationTemp);
                System.out.println(idConfig);
                addFileCSVToLog(idConfig,fileName);
                System.out.println("Ready to take from data file csv"+fileName+"  to temp_staging");
            }else {
                System.out.println("File "+fileName+" is exits in logs");
                sendEmail.sendMail("tranlocmom@gmail.com", "File "+fileName+" is exits in logs");
            }

        }


    }

//    Luồng 2
    public void setStatusForFileCSVReadyToTempStaging(){
        List<String> fileNames = getFileNameCSVReadyToTempStaging();
        int idConfig = getIdConfig(sourceFileLocation);
        for (String fileName : fileNames) {
            setStatusLogAndProcessWhenLoadDataCSVToTempStagingSU(idConfig,fileName);
        }

    }
//    Luồng 2.1
    private void setStatusLogAndProcessWhenLoadDataCSVToTempStagingSU(int idConfig, String fileName) {
        String procedureCall = "{CALL set_status_extract_temp(?, ?)}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Truyền tham số cho procedure
            callableStatement.setInt(1, idConfig); // Tham số đầu tiên la config_id
            callableStatement.setString(2, fileName); // Tham số thu hai la fileName

            // Thực thi procedure
            callableStatement.execute();
            System.out.println("Procedure set_status_extract_temp executed successfully!");
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing procedure: " + e);
            throw new RuntimeException("Error executing procedure", e);
        }

    }
    //    Luồng thứ 2.2
    public List<String> getFileNameCSVReadyToTempStaging(){
        List<String> fileNames = new ArrayList<>();
        String sourceFileLocationTemp = sourceFileLocation.replace("\\", "\\\\");
        int configId = getIdConfig(sourceFileLocationTemp);
        String sql = "Select l.file_name from logs l join processes p on p.log_id = l.id where p.status = 'EXTRACT_READY' and l.config_id = ?";
        try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Truyền tham số cho truy vấn
            preparedStatement.setInt(1, configId);  // Tham số đầu tiên là config_id

            // Thực thi truy vấn
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Nếu có kết quả, file đã tồn tại
                while (resultSet.next()) {
                    String fileName = resultSet.getString("file_name");
                    fileNames.add(fileName);

                }

            }
            if(fileNames.size()>0){
                return fileNames;
            } else {

                return fileNames;
            }
            // Nếu không cá kết quả, file chưa tồn tại
        }
        catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: " + e);
            throw new RuntimeException("Error checking file in log table", e);
        }
    }
    // Luồng thứ 3
    public boolean checkStatusExtractToTempStaging(){
        String sql = "Select l.id from logs l join processes p on p.log_id = l.id where p.status = 'EXTRACT_TO_TEMP_STAGING'";
        try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Thực thi truy vấn
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Nếu có kết quả, file đã tồn tại
                while (resultSet.next()) {
                   return true;

                }
            }
            return false;
            // Nếu không cá kết quả, file chưa tồn tại
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: " + e);
            throw new RuntimeException("Error checking file in log table", e);
        }
    }
//    Luồng thứ 4
    public void setStatusTempStagingToMainStaging(){
        String sourceFileLocationTemp = sourceFileLocation.replace("\\", "\\\\");
        int configId = getIdConfig(sourceFileLocationTemp);
     List<String> fileNames = getFileNameHasStatusExtractToTempStaging();
     if(fileNames.size() == 0 || fileNames == null){
         System.out.println("None of file to load");
         sendEmail.sendMail("tranlocmom@gmail.com", "None of file to load");
         return;
     }
     for (String fileName : fileNames) {
         setStatusToMainStaging(configId,fileName);
     }
    }
//    Luồng 4.1
    private void setStatusToMainStaging(int idConfig, String fileName) {
        String procedureCall = "{CALL Transform_data_from_temp_staging_to_main_staging(?,?)}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Truyền tham số cho procedure
            callableStatement.setInt(1, idConfig); // Tham số đầu tiên la config_id
            callableStatement.setString(2, fileName); // Tham số thu hai la fileName

            // Thực thi procedure
            callableStatement.execute();
            System.out.println("Procedure Transform_data_from_temp_staging_to_main_staging for "+fileName+" executed successfully!");
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing procedure: " + e);
            throw new RuntimeException("Error executing procedure", e);
        }
    }

    //Luồng 4.2
    public List<String> getFileNameHasStatusExtractToTempStaging(){
        List<String> fileNames = new ArrayList<>();
        String sourceFileLocationTemp = sourceFileLocation.replace("\\", "\\\\");
        int configId = getIdConfig(sourceFileLocationTemp);
        String sql = "Select l.file_name from logs l join processes p on p.log_id = l.id where p.status = 'EXTRACT_TO_TEMP_STAGING' and l.config_id = ?";
        try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Truyền tham số cho truy vấn
            preparedStatement.setInt(1, configId);  // Tham số đầu tiên là config_id

            // Thực thi truy vấn
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Nếu có kết quả, file đã tồn tại
                while (resultSet.next()) {
                    String fileName = resultSet.getString("file_name");
                    fileNames.add(fileName);
                }
            }
            if(fileNames.size()>0){
                return fileNames;
            } else {
                return fileNames;
            }
            // Nếu không cá kết quả, file chưa tồn tại
        }
        catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: " + e);
            throw new RuntimeException("Error checking file in log table", e);
        }
    }
//    Luồng thứ 5
     public boolean checkStatusExtractToMainStaging(){
         String sql = "Select l.id from logs l join processes p on p.log_id = l.id where p.status = 'EXTRACT_TO_MAINSTAGING'";
         try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
              PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
             // Thực thi truy vấn
             try (ResultSet resultSet = preparedStatement.executeQuery()) {
                 // Nếu có kết quả, file tồn tại
                 while (resultSet.next()) {
                     return true;
                 }
             }
             return false;
             // Nếu không cá kết quả, file chưa tồn tại
         } catch (SQLException e) {
             sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: " + e);
             throw new RuntimeException("Error checking file in log table", e);
         }
     }
     // Luồng thứ 6
    public void setStatusToTempDW(){
        String sourceFileLocationTemp = sourceFileLocation.replace("\\", "\\\\");
        int configId = getIdConfig(sourceFileLocationTemp);
        List<String> fileNames = getFileNameHasStatusExtractToMainStaging();
        if(fileNames.size() == 0 || fileNames == null){
            System.out.println("None of file to load");
            sendEmail.sendMail("tranlocmom@gmail.com", "None of file to load");
        }
        for (String fileName : fileNames) {
            setStatusToTempDataWareHouse(configId,fileName);
        }
    }
    // Luồng 6.1
    private void setStatusToTempDataWareHouse(int idConfig, String fileName) {
        String procedureCall = "{CALL MigrateDailyStagingToTempDW(?,?)}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Truyền tham số cho procedure
            callableStatement.setInt(1, idConfig); // Tham số đầu tiên la config_id
            callableStatement.setString(2, fileName); // Tham số thu hai la fileName

            // Thực thi procedure
            callableStatement.execute();
            System.out.println("Procedure MigrateDailyStagingToTempDW for "+fileName+" executed successfully!");
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing procedure: " + e);
            throw new RuntimeException("Error executing procedure", e);
        }
    }


// Luồng 6.2
public List<String> getFileNameHasStatusExtractToMainStaging(){
    List<String> fileNames = new ArrayList<>();
    String sourceFileLocationTemp = sourceFileLocation.replace("\\", "\\\\");
    int configId = getIdConfig(sourceFileLocationTemp);
    String sql = "Select l.file_name from logs l join processes p on p.log_id = l.id where p.status = 'EXTRACT_TO_MAINSTAGING' and l.config_id = ?";
    try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        // Truyền tham số cho truy vấn
        preparedStatement.setInt(1, configId);  // Tham số đầu tiên là config_id

        // Thực thi truy vấn
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            // Nếu có kết quả, file đã tồn tại
            while (resultSet.next()) {
                String fileName = resultSet.getString("file_name");
                fileNames.add(fileName);
            }
        }
        if(fileNames.size()>0){
            return fileNames;
        } else {
            return fileNames;
        }
        // Nếu không cá kết quả, file chưa tồn tại
    }
    catch (SQLException e) {
        sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: " + e);
        throw new RuntimeException("Error checking file in log table", e);
    }
}
// Luồng thu 7
public boolean checkStatusExtractToTempDW(){
    String sql = "Select l.id from logs l join processes p on p.log_id = l.id where p.status = 'EXTRACT_TO_TEMP_DW'";
    try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        // Thực thi truy vấn
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            // Nếu có kết quả, file đã tồn tại
            while (resultSet.next()) {
                return true;
            }
        }
        return false;
        // Nếu không cá kết quả, file chưa tồn tại
    } catch (SQLException e) {
        sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: "+ e);
        throw new RuntimeException("Error checking file in log table", e);
    }
}
// Luồng thứ 8
public void setStatusToMainDW(){
    String sourceFileLocationTemp = sourceFileLocation.replace("\\", "\\\\");
    int configId = getIdConfig(sourceFileLocationTemp);
    List<String> fileNames = getFileNameHasStatusExtractToTempDW();
    if(fileNames.size() == 0 || fileNames == null){
        System.out.println("None of file to load");
    }
    for (String fileName : fileNames) {
        setStatusToMainDataWareHouse(configId,fileName);
    }
}
    // Luồng 8.1
    private void setStatusToMainDataWareHouse(int idConfig, String fileName) {
        String procedureCall = "{CALL update_data_from_tempDW_to_DW(?,?)}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Truyền tham số cho procedure
            callableStatement.setInt(1, idConfig); // Tham số đầu tiên la config_id
            callableStatement.setString(2, fileName); // Tham số thu hai la fileName

            // Thực thi procedure
            callableStatement.execute();
            System.out.println("Procedure update_data_from_tempDW_to_DW for "+fileName+" executed successfully!");
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing procedure: "+ e);
            throw new RuntimeException("Error executing procedure", e);
        }
    }


    // Luồng 8.2
    public List<String> getFileNameHasStatusExtractToTempDW(){
        List<String> fileNames = new ArrayList<>();
        String sourceFileLocationTemp = sourceFileLocation.replace("\\", "\\\\");
        int configId = getIdConfig(sourceFileLocationTemp);
        String sql = "Select l.file_name from logs l join processes p on p.log_id = l.id where p.status = 'EXTRACT_TO_TEMP_DW' and l.config_id = ?";
        try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Truyền tham số cho truy vấn
            preparedStatement.setInt(1, configId);  // Tham số đầu tiên là config_id

            // Thực thi truy vấn
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Nếu có kết quả, file đã tồn tại
                while (resultSet.next()) {
                    String fileName = resultSet.getString("file_name");
                    fileNames.add(fileName);
                }
            }
            if(fileNames.size()>0){
                return fileNames;
            } else {
                return fileNames;
            }
            // Nếu không cá kết quả, file chưa tồn tại
        }
        catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: "+ e);
            throw new RuntimeException("Error checking file in log table", e);
        }
    }



    private boolean checkFile(String fileName) {
        String sql = "SELECT 1 FROM logs WHERE file_name = ?";
        try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Truyền tham số cho truy vấn
            preparedStatement.setString(1, fileName);

            // Thực thi truy vấn
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Nếu có kết quả, file đã tồn tại
                if (resultSet.next()) {
                    return false; // File đã tồn tại
                }
            }
            // Nếu không có kết quả, file chưa tồn tại
            return true;
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: "+ e);
            throw new RuntimeException("Error checking file in log table", e);
        }
    }
    private void addFileCSVToLog(int idConfig,String fileName) {
        String procedureCall = "{CALL Load_file_data_csv_to_temp_staging(?, ?)}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Truyền tham số cho procedure
            callableStatement.setInt(1, idConfig);  // Tham số đầu tiên là config_id
            callableStatement.setString(2, fileName);  // Tham số thứ hai là file_name

            // Thực thi procedure
            callableStatement.execute();
            System.out.println("Procedure executed successfully!");

        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error executing stored procedure: "+ e);
            throw new RuntimeException("Error executing stored procedure", e);
        }

    }
    public int getIdConfig(String sourceFileLocation) {
        String sql = "SELECT id FROM configs WHERE source_file_location = ?";
        try (Connection connection = jdbcConection.getConnection(); // Đảm bảo Connection được đóng
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Truyền tham số cho truy vấn
            preparedStatement.setString(1, sourceFileLocation);

            // Thực thi truy vấn
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Nếu có kết quả, file đã tồn tại
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
            // Nếu không có kết quả, file chưa tồn tại
            return -1;
        } catch (SQLException e) {
            sendEmail.sendMail("tranlocmom@gmail.com", "Error checking file in log table: "+ e);
            throw new RuntimeException("Error checking file in log table", e);
        }
    }





}
