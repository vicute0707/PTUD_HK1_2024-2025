package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyConnection {
    // Database credentials
    private static final String HOST = "localhost";
    private static final String PORT = "3307";  
    private static final String DATABASE = "store_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";  

    // JDBC URL
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
            "?useUnicode=true&characterEncoding=UTF-8";

    private Connection connection;

    // Constructor
    public MyConnection() {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found.", e);
        }
    }

    // Connect to database
    public Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                
            }
            System.out.println("Ketes noi thanh cong");
            return connection;
           
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }
    }

    // Close connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing connection", e);
        }
    }

    // Execute SELECT query with prepared statement
    public ResultSet executeQuery(String query, Object... params) {
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            // Set parameters if any
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    // Execute INSERT, UPDATE, DELETE queries
    public int executeUpdate(String query, Object... params) {
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            // Set parameters if any
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing update", e);
        }
    }

    // Begin transaction
    public void beginTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Error starting transaction", e);
        }
    }

    // Commit transaction
    public void commitTransaction() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Error committing transaction", e);
        }
    }

    // Rollback transaction
    public void rollbackTransaction() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Error rolling back transaction", e);
        }
    }
}