package newpix.configs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/newpix";
    private static final String USER = "root";
    private static final String PASSWORD = "password"; // ATENÇÃO: Altere para a sua senha do MySQL

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}