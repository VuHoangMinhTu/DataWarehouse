package Configs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConection {
   Connection connection;
    String jdbcURL = "jdbc:mysql://localhost:3306/";
    String databaseName="";
    String dbUser = "root";
    String dbPassword = "";

    public void connect(String databaseName) {
        try {
            Connection connection = null;
            this.databaseName = databaseName;
            connection = DriverManager.getConnection(jdbcURL+this.databaseName, dbUser, dbPassword);
            this.connection = connection;
//            System.out.println("kết nối tới database "+databaseName+" thành công");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()){
                connect(databaseName);
            }
            return this.connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

    }

}
