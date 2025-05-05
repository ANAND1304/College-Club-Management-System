package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {  
    public static Connection getConnection() {  
        Connection conn = null;  
        try {  
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load the MySQL driver  
            String url = "jdbc:mysql://localhost:3306/college_club"; // Your database URL  
            String user = "root"; // Your database username  
            String password = "7870"; // Your database password  
            conn = DriverManager.getConnection(url, user, password);  
        } catch (ClassNotFoundException | SQLException e) {  
            System.err.println("Error connecting to database: " + e.getMessage());  
            e.printStackTrace();  
        }  
        return conn;  
    }  
}