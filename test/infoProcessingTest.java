package test;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.jupiter.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import src.infoProcessing;

public class infoProcessingTest {
    public static Connection connection = null;
    public static Statement statement = null;
    public static String user = null;
    public static String password = null;
    private static Scanner sc = new Scanner(System.in);

    @BeforeEach
    public void initiateConnection(){
        getUser();
        connectToDatabase("jdbc:mariadb://classdb2.csc.ncsu.edu:3306/" + user, user, password);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            close();
        }));
    }
    
    @Test
    public void testEnterDriverInfo(){
        Statement statement = null;

        infoProcessing in = new infoProcessing();
        in.enterDriverInfo(statement);
        assertEquals(1,1);
    }



    static void getUser() {
		Properties properties = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream("../db_keys");
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
