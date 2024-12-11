package Controllers;

import Configs.JDBCConection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class Db_DataMart {
	private JDBCConection jdbcConection = new JDBCConection();
    public Db_DataMart(){
        jdbcConection.connect("dm");
    }
    // load data tu dw sang dm theo price
    public void loadDataFromDWtoDataMartChangePrice() {
    	String procedureCall = "{CALL loadDataFromDWtoDataMartChangePrice()}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
            // Thuc thi procedure
            callableStatement.execute();
            System.out.println("Procedure loadDataFromDWtoDataMartChangePrice executed successfully!");

        } catch (SQLException e) {
            throw new RuntimeException("Error executing stored procedure", e);
        }
    }
    // load data tu dw sang dm
    public void LoadDmFilterCameras() {
    	String procedureCall = "{CALL LoadDmFilterCameras()}";
        try (Connection connection = jdbcConection.getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
            // Thuc thi procedure
            callableStatement.execute();
            System.out.println("Procedure loadDataFromDWtoDataMartChangePrice executed successfully!");

        } catch (SQLException e) {
            throw new RuntimeException("Error executing stored procedure", e);
        }
    }
    public static void main(String[] args) {
        Db_DataMart test = new Db_DataMart();
        test.loadDataFromDWtoDataMartChangePrice();
    }
}
