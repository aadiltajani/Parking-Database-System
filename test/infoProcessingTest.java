package test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import src.infoProcessing;

public class infoProcessingTest {
    private static Connection connection = null;
    private static Statement statement = null;
    private static String user = null;
    private static String password = null;
    private infoProcessing IP = new infoProcessing();


    @BeforeAll
    public static void initiateConnection(){
        try {
            getUser();
            connectToDatabase("jdbc:mariadb://classdb2.csc.ncsu.edu:3306/" + user, user, password);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down...");
                close();
            }));
            try{
                statement.executeUpdate("DELETE FROM Driver WHERE phone = \'9108887955\'");
            } catch(Exception e){
                // ignore
            }
            
        }catch (Exception e) {
            System.out.println("Error Occurred" + e);
            close();
        }
    }

    @AfterAll
    public static void closeConnection(){
        close();
    }
    
    @Test
    public void testEnterDriverInfo(){
        IP.enterDriverInfoHelper(statement, "9108887955", "Washington,George", "S", "gwashU");
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Driver WHERE phone =\'9108887955\';");
            result.next();
            assertEquals("gwashU", result.getString("univ_id"));
            assertEquals("Washington,George", result.getString("name"));
        } catch(Exception e) {
            fail();
        }
    }



    static void getUser() {
		Properties properties = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("./db_keys");
            properties.load(input);
            user = properties.getProperty("username");
            password = properties.getProperty("password");
            System.out.println("User Found");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    static void connectToDatabase(String jdbcURL, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");
        connection = DriverManager.getConnection(jdbcURL, user, password);
        statement = connection.createStatement();
        System.out.println("Connected to Database");
    }

    static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Database Connection Terminated");
    }
}
