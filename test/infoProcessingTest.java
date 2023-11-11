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
                statement.executeUpdate("DELETE FROM Permit WHERE permit_id = 8");
                statement.executeUpdate("INSERT INTO Driver (phone, name, status, univ_id) VALUES ('9194999124', 'Patrick,Mahomes', 'V', NULL);");
                statement.executeUpdate("DELETE FROM ParkingLot WHERE lot_name = 'Varsity';"); 
                statement.executeUpdate("DELETE FROM Zone WHERE zone_id = 'SS' AND lot_name = \'Partners Way Deck\';");  
                statement.executeUpdate("DELETE FROM Zone WHERE zone_id = 'NS' AND lot_name = \'Partners Way Deck\';"); 
                statement.executeUpdate("DELETE FROM Zone WHERE zone_id = 'SS' AND lot_name = \'Partners Way Deck\';");   
  
 
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

    @Test
    public void testUpdateDriverInfo(){
        String update = "name = \'Jackson,Andrew\' , status = \'E\'";
        IP.updateDriverInfoHelper(statement, "9194999124", update);
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Driver WHERE phone =\'9194999124\';");
            result.next();
            assertEquals("Jackson,Andrew", result.getString("name"));
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteDriverInfo(){
        try{
            statement.executeUpdate("INSERT INTO Driver (phone, name, status, univ_id) VALUES ('1192199124', 'Katy,Perry', 'S', 'kperry');");
        } catch(Exception e){
            // ignore
        }

        int rowsAffected = IP.deleteDriverInfoHelper(statement, null, "1192199124");
        
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Driver WHERE phone = \'1192199124\';");
            assertFalse(result.next());
        } catch(Exception e) {
            fail();
        }
        assertEquals(1, rowsAffected);
    }

    @Test
    public void testEnterParkingLotInfo(){
        IP.enterParkingLotInfoHelper(statement, "Varsity", "111, Oval Dr.");
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM ParkingLot WHERE lot_name =\'Varsity\';");
            result.next();
            assertEquals("Varsity", result.getString("lot_name"));
            assertEquals("111, Oval Dr.", result.getString("address"));
        } catch(Exception e) {
            fail();
        }
    }

     @Test
    public void testUpdateParkingLotInfo(){
       

        IP.updateParkingLotInfoHelper(statement, "Varsity", "New Address");
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM ParkingLot WHERE lot_name =\'Varsity\';");
            result.next();
            assertEquals("New Address", result.getString("address"));
        } catch(Exception e) {
            fail();
        }
    }

     @Test
    public void testDeleteParkingLotInfo(){
        try{
            statement.executeUpdate("INSERT INTO ParkingLot (lot_name, address) VALUES ('NoLot','Nowhere');");
        } catch(Exception e){
            // ignore
        }

        int rowsAffected = IP.deleteParkingLotInfoHelper(statement, "NoLot");
        
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Driver WHERE phone = \'1192199124\';");
            assertFalse(result.next());
        } catch(Exception e) {
            fail();
        }
        assertEquals(1, rowsAffected);
    }

    @Test
    public void testEnterZoneInfo(){
        IP.enterZoneInfoHelper(statement, "SS", "Partners Way Deck");
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Zone WHERE lot_name =\'Partners Way Deck\' AND zone_id = \'SS\';");
            result.next();
            assertEquals("Partners Way Deck", result.getString("lot_name"));
            assertEquals("SS", result.getString("zone_id"));
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateZoneInfo(){
        IP.updateZoneInfoHelper(statement, "SS", "Partners Way Deck", "NS", null);
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Zone WHERE lot_name =\'Dan Allen Parking Deck\' AND zone_id = \'NS\';");
            result.next();
            assertEquals("Dan Allen Parking Deck", result.getString("lot_name"));
            assertEquals("NS", result.getString("zone_id"));
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteZoneInfo(){
        try{
            statement.executeUpdate("INSERT INTO Zone (lot_name, zone_id) VALUES ('Dan Allen Parking Deck','AI');");
        } catch(Exception e){
            // ignore
        }

        int rowsAffected = IP.deleteZoneInfoHelper(statement,"AI", "Dan Allen Parking Deck");
        
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Zone WHERE zone_id = \'AI\' AND lot_name = \'Dan Allen Parking Deck\';");
            assertFalse(result.next());
        } catch(Exception e) {
            fail();
        }
        assertEquals(1, rowsAffected);
    }

    @Test
    public void testEnterSpaceInfo(){
        IP.enterSpaceInfoHelper(statement, 111, "A", "Poulton Deck",  "Regular");
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Space WHERE lot_name =\'Poulton Deck\' AND zone_id = 'A' AND space_number = 111;");
            result.next();
            assertEquals("Poulton Deck", result.getString("lot_name"));
            assertEquals("111", result.getString("space_number"));
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateSpaceInfo(){
        IP.updateSpaceInfoHelper(statement, "SS", "Partners Way Deck", 111, "Regular", 0);
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Space WHERE lot_name =\'Poulton Deck\' AND zone_id = \'A\';");
            result.next();
            assertEquals("Partners Way Deck", result.getString("lot_name"));
            assertEquals("Regular", result.getString("space_type"));
        } catch(Exception e) {
            fail();
        }
    }

    
    @Test
    public void testDeleteSpaceInfo(){
        try{
            statement.executeUpdate("INSERT INTO Space (lot_name, zone_id) VALUES ('Dan Allen Parking Deck','AI');");
        } catch(Exception e){
            // ignore
        }

        int rowsAffected = IP.deleteSpaceInfoHelper(statement,111, "A", "Poultron Deck");
        
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Space WHERE space_number = 111 AND zone_id = \'A\' AND lot_name = \'Poulton Deck\';");
            assertFalse(result.next());
        } catch(Exception e) {
            fail();
        }
        assertEquals(1, rowsAffected);
    }


    // test enter permit info 
    @Test
    public void testEnterPermitInfo(){
        IP.enterPermitInfoHelper(statement, 8, "Regular", "Commuter", "2023-01-01", "2024-01-01", "06:00:00");
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Permit WHERE permit_id = 8;");
            result.next();
            assertEquals("8", result.getString("permit_id"));
            assertEquals("Regular", result.getString("space_type"));
            assertEquals("Commuter", result.getString("permit_type"));
            assertEquals("2023-01-01", result.getString("start_date"));
            assertEquals("2024-01-01", result.getString("expiration_date"));
            assertEquals("06:00:00", result.getString("expiration_time"));


        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdatePermitInfo(){
        String updates = "space_type = \'Handicap\', permit_type = \'Residential\', start_date = \'2023-01-02\', " +
        "expiration_date = \'2025-01-01\', expiration_time = \'07:00:00\'";
        IP.updatePermitInfoHelper(statement, 8, updates);
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Permit WHERE permit_id = 8;");
            result.next();
            assertEquals("8", result.getString("permit_id"));
            assertEquals("Handicap", result.getString("space_type"));
            assertEquals("Residential", result.getString("permit_type"));
            assertEquals("2023-01-02", result.getString("start_date"));
            assertEquals("2025-01-01", result.getString("expiration_date"));
            assertEquals("07:00:00", result.getString("expiration_time"));


        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testDeletePermitInfo(){
        // try{
        //     statement.executeUpdate("INSERT INTO Space (lot_name, zone_id) VALUES ('Dan Allen Parking Deck','AI');");
        // } catch(Exception e){
        //     // ignore
        // }

        int rowsAffected = IP.deletePermitInfoHelper(statement, 8);
        
        try{
            ResultSet result = statement.executeQuery("SELECT * FROM Permit WHERE permit_id = 8;");
            assertFalse(result.next());
        } catch(Exception e) {
            fail();
        }
        assertEquals(1, rowsAffected);
    }

    @Test
    public void testAssignTypeToSpace() {
        fail();
    }

    @Test
    public void testRequestCitationAppeal() {
        fail();
    }

    @Test
    public void testUpdateCitationPayment() {
        fail();
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
