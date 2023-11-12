package src;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class DemoDataLoad {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Properties properties = new Properties();
        FileInputStream input = null;
        String user = null;
        String password = null;

        try {
            input = new FileInputStream("../db_keys");
            properties.load(input);

            user = properties.getProperty("username");
            password = properties.getProperty("password");

            System.out.println("User: " + user);
            System.out.println("Password: " + password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/"+ user;
        Class.forName("org.mariadb.jdbc.Driver");

		Connection conn = DriverManager.getConnection(jdbcURL, user, password);
		Statement stmt = conn.createStatement();
        try {

            List<String> tableNames = List.of(
                "IsAssigned",
                "Shows",
                "Appeals",
                "HasLot",
                "HasZone",
                "GivenTo",
                "Space",
                "Zone",
                "ParkingLot",
                "Vehicle",
                "Citation",
                "Permit",
                "Driver"
            );

        
            for (String tableName : tableNames) {
                if (tableExists(conn, tableName)) {
                    System.out.println("Table " + tableName + " exists. Deleting...");
                    deleteTable(conn, tableName);
                    System.out.println("Table " + tableName + " deleted successfully.");
                } else {
                    System.out.println("Table " + tableName + " does not exist.");
                }
            }

            createTables(stmt);
            insertData(stmt);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stmt.close();
        conn.close();
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SHOW TABLES LIKE '" + tableName + "'");
            return stmt.getResultSet().next();
        }
    }

    private static void deleteTable(Connection connection, String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE " + tableName);
        }
    }

    private static void createTables(Statement stmt) throws Exception {
        String createDriverTableSQL = "CREATE TABLE Driver (\n" +
            "phone VARCHAR(10),\n" +
            "name VARCHAR(128) NOT NULL,\n" +
            "status VARCHAR(1) NOT NULL,\n" +
            "univ_id VARCHAR(128),\n" +
            "UNIQUE(univ_id),\n" +
            "PRIMARY KEY(phone)\n" +
            ");";
        stmt.execute(createDriverTableSQL);

        String createParkingLotTableSQL = "CREATE TABLE ParkingLot (\n" +
            "lot_name VARCHAR(128),\n" +
            "address VARCHAR(128) NOT NULL,\n" +
            "PRIMARY KEY(lot_name)\n" +
            ");";
        stmt.execute(createParkingLotTableSQL);

        String createZoneTableSQL = "CREATE TABLE Zone (\n" +
            "zone_id VARCHAR(2),\n" +
            "lot_name VARCHAR(100),\n" +
            "PRIMARY KEY(zone_id, lot_name),\n" +
            "FOREIGN KEY(lot_name) REFERENCES ParkingLot(lot_name) ON UPDATE CASCADE\n" +
            ");";
        stmt.execute(createZoneTableSQL);

        String createSpaceTableSQL = "CREATE TABLE Space (\n" +
            "space_number INT,\n" +
            "lot_name VARCHAR(100),\n" +
            "zone_id VARCHAR(2),\n" +
            "space_type VARCHAR(11) NOT NULL,\n" +
            "availability_status BOOLEAN NOT NULL,\n" +
            "PRIMARY KEY(space_number, lot_name, zone_id),\n" +
            "FOREIGN KEY(zone_id, lot_name) REFERENCES Zone(zone_id, lot_name)\n" +
            ");";
        stmt.execute(createSpaceTableSQL);

        String createVehicleTableSQL = "CREATE TABLE Vehicle (\n" +
            "car_license_number VARCHAR(10),\n" +
            "model VARCHAR(100) NOT NULL,\n" +
            "year INT NOT NULL,\n" +
            "color VARCHAR(100) NOT NULL,\n" +
            "manufacturer VARCHAR(100) NOT NULL,\n" +
            "PRIMARY KEY(car_license_number)\n" +
            ");";
        stmt.execute(createVehicleTableSQL);

        String createCitationTableSQL = "CREATE TABLE Citation (\n" +
            "citation_number INT,\n" +
            "citation_date DATE NOT NULL,\n" +
            "citation_time TIME NOT NULL,\n" +
            "category VARCHAR(128) NOT NULL,\n" +
            "fee FLOAT(9,2) NOT NULL,\n" +
            "payment_status BOOLEAN NOT NULL,\n" +
            "PRIMARY KEY(citation_number)\n" +
            ");";
        stmt.execute(createCitationTableSQL);

        String createPermitTableSQL = "CREATE TABLE Permit (\n" +
            "permit_id INT,\n" +
            "space_type VARCHAR(11) NOT NULL,\n" +
            "start_date DATE NOT NULL,\n" +
            "expiration_date DATE NOT NULL,\n" +
            "expiration_time TIME NOT NULL,\n" +
            "permit_type VARCHAR(13) NOT NULL,\n" +
            "PRIMARY KEY(permit_id)\n" +
            ");";
        stmt.execute(createPermitTableSQL);

        String createIsAssignedTableSQL = "CREATE TABLE IsAssigned (\n" +
            "phone VARCHAR(10),\n" +
            "permit_id INT,\n" +
            "car_license_number VARCHAR(10),\n" +
            "PRIMARY KEY(phone, permit_id, car_license_number),\n" +
            "FOREIGN KEY(phone) REFERENCES Driver(phone) ON UPDATE CASCADE,\n" +
            "FOREIGN KEY(permit_id) REFERENCES Permit(permit_id) ON UPDATE CASCADE,\n" +
            "FOREIGN KEY(car_license_number) REFERENCES Vehicle(car_license_number) ON UPDATE CASCADE\n" +
            ");";
        stmt.execute(createIsAssignedTableSQL);

        String createShowsTableSQL = "CREATE TABLE Shows (" +
            "citation_number INT," +
            "lot_name VARCHAR(128)," +
            "PRIMARY KEY (citation_number, lot_name)," +
            "FOREIGN KEY (citation_number) REFERENCES Citation(citation_number) ON UPDATE CASCADE," +
            "FOREIGN KEY (lot_name) REFERENCES ParkingLot(lot_name) ON UPDATE CASCADE" +
            ")";
        stmt.execute(createShowsTableSQL);

        String createAppealsTableSQL = "CREATE TABLE Appeals (" +
            "phone VARCHAR(10)," +
            "citation_number INT," +
            "appeal_status VARCHAR(9) NOT NULL," +
            "PRIMARY KEY (phone, citation_number)," +
            "FOREIGN KEY (citation_number) REFERENCES Citation(citation_number) ON UPDATE CASCADE," +
            "FOREIGN KEY (phone) REFERENCES Driver(phone) ON UPDATE CASCADE" +
            ")";
        stmt.execute(createAppealsTableSQL);

        String createHasLotTableSQL = "CREATE TABLE HasLot (" +
            "permit_id INT," +
            "lot_name VARCHAR(128)," +
            "PRIMARY KEY (permit_id, lot_name)," +
            "FOREIGN KEY (permit_id) REFERENCES Permit(permit_id) ON UPDATE CASCADE," +
            "FOREIGN KEY (lot_name) REFERENCES ParkingLot(lot_name) ON UPDATE CASCADE" +
            ")";
        stmt.execute(createHasLotTableSQL);

        String createHasZoneTableSQL = "CREATE TABLE HasZone (" +
            "permit_id INT," +
            "zone_id VARCHAR(2)," +
            "lot_name VARCHAR(128)," +
            "PRIMARY KEY (permit_id, zone_id, lot_name)," +
            "FOREIGN KEY (permit_id) REFERENCES Permit(permit_id) ON UPDATE CASCADE," +
            "FOREIGN KEY (zone_id) REFERENCES Zone(zone_id) ON UPDATE CASCADE," +
            "FOREIGN KEY (lot_name) REFERENCES ParkingLot(lot_name) ON UPDATE CASCADE" +
            ")";
        stmt.execute(createHasZoneTableSQL);

        String createGivenToTableSQL = "CREATE TABLE GivenTo (" +
            "citation_number INT," +
            "car_license_number VARCHAR(10)," +
            "PRIMARY KEY (citation_number, car_license_number)," +
            "FOREIGN KEY (citation_number) REFERENCES Citation(citation_number) ON UPDATE CASCADE" +
            // "FOREIGN KEY (car_license_number) REFERENCES Vehicle(car_license_number) ON UPDATE CASCADE" +
            ")";
        stmt.execute(createGivenToTableSQL);

    }

    private static void insertData(Statement stmt) throws Exception {
        String[] insertStatements = {
            // INSERT statements for the Driver table
            "INSERT INTO Driver (phone, name, status, univ_id) VALUES ('7729119111', 'Sam BankmanFried', 'V', NULL);",
            "INSERT INTO Driver (phone, name, status, univ_id) VALUES ('266399121', 'John Clay', 'E', 'jclay');",
            "INSERT INTO Driver (phone, name, status, univ_id) VALUES ('366399121', 'Julia Hicks', 'E', 'jhicks');",
            "INSERT INTO Driver (phone, name, status, univ_id) VALUES ('466399121', 'Ivan Garcia', 'E', 'igarcia');",
            "INSERT INTO Driver (phone, name, status, univ_id) VALUES ('122765234', 'Sachin Tendulkar', 'S', 'stendulkar');",
            "INSERT INTO Driver (phone, name, status, univ_id) VALUES ('9194789124', 'Charles Xavier', 'V', NULL);",
            
            // INSERT statements for the ParkingLot table
            "INSERT INTO ParkingLot (lot_name, address) VALUES ('Poulton Deck', '1021 Main Campus Dr Raleigh, NC, 27606');",
            "INSERT INTO ParkingLot (lot_name, address) VALUES ('Partners Way Deck', '851 Partners Way Raleigh, NC, 27606');",
            "INSERT INTO ParkingLot (lot_name, address) VALUES ('Dan Allen Parking Deck', '110 Dan Allen Dr Raleigh, NC, 27607');",
            
            // INSERT statements for the Zone table
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('A', 'Poulton Deck');",
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('AS', 'Poulton Deck');",
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('V', 'Poulton Deck');",
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('A', 'Partners Way Deck');",
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('AS', 'Partners Way Deck');",
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('V', 'Partners Way Deck');",
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('A', 'Dan Allen Parking Deck');",
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('AS', 'Dan Allen Parking Deck');",
            "INSERT INTO Zone (zone_id, lot_name) VALUES ('V', 'Dan Allen Parking Deck');",
            
            // INSERT statements for the Space table
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (1, 'Dan Allen Parking Deck', 'V', 'Regular', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (2, 'Dan Allen Parking Deck', 'V', 'Handicap', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (3, 'Dan Allen Parking Deck', 'V', 'Electric', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (1, 'Partners Way Deck', 'AS', 'Compact Car', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (2, 'Partners Way Deck', 'AS', 'Regular', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (1, 'Poulton Deck', 'A', 'Regular', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (2, 'Poulton Deck', 'A', 'Regular', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (3, 'Poulton Deck', 'A', 'Regular', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (4, 'Poulton Deck', 'A', 'Electric', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (5, 'Poulton Deck', 'A', 'Handicap', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (1, 'Poulton Deck', 'AS', 'Regular', 1);",
            "INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES (2, 'Poulton Deck', 'AS', 'Regular', 1);",
            
            // INSERT statements for the Vehicle table
            "INSERT INTO Vehicle (car_license_number, model, year, color, manufacturer) VALUES ('SBF', 'GT-R-Nismo', 2024, 'Pearl White TriCoat', 'Nissan');",
            "INSERT INTO Vehicle (car_license_number, model, year, color, manufacturer) VALUES ('Clay1', 'Model S', 2023, 'Ultra Red', 'Tesla');",
            "INSERT INTO Vehicle (car_license_number, model, year, color, manufacturer) VALUES ('Hicks1', 'M2 Coupe', 2024, 'Zandvoort Blue', 'BMW');",
            "INSERT INTO Vehicle (car_license_number, model, year, color, manufacturer) VALUES ('Garcia1', 'Continental GT Speed', 2024, 'Blue Fusion', 'Bentley');",
            "INSERT INTO Vehicle (car_license_number, model, year, color, manufacturer) VALUES ('CRICKET', 'Civic SI', 2024, 'Sonic Gray Pearl', 'Honda');",
            "INSERT INTO Vehicle (car_license_number, model, year, color, manufacturer) VALUES ('PROFX', 'Taycan Sport Turismo', 2024, 'Frozenblue Metallic', 'Porsche');",
            
            // INSERT statements for the Permit table
            "INSERT INTO Permit (permit_id, space_type, start_date, expiration_date, expiration_time, permit_type) VALUES (1, 'Regular', '2023-01-01', '2024-01-01', '06:00:00', 'Commuter');",
            "INSERT INTO Permit (permit_id, space_type, start_date, expiration_date, expiration_time, permit_type) VALUES (2, 'Electric', '2010-01-01', '2030-01-01', '06:00:00', 'Residential');",
            "INSERT INTO Permit (permit_id, space_type, start_date, expiration_date, expiration_time, permit_type) VALUES (3, 'Regular', '2023-01-01', '2024-01-01', '06:00:00', 'Commuter');",
            "INSERT INTO Permit (permit_id, space_type, start_date, expiration_date, expiration_time, permit_type) VALUES (4, 'Regular', '2023-01-01', '2024-01-01', '06:00:00', 'Commuter');",
            "INSERT INTO Permit (permit_id, space_type, start_date, expiration_date, expiration_time, permit_type) VALUES (5, 'Compact Car', '2022-01-01', '2023-09-30', '06:00:00', 'Residential');",
            "INSERT INTO Permit (permit_id, space_type, start_date, expiration_date, expiration_time, permit_type) VALUES (6, 'Handicap', '2023-01-01', '2023-11-15', '06:00:00', 'Special Event');",
        
            // INSERT statements for the Citation table
            "INSERT INTO Citation (citation_number, citation_date, citation_time, category, fee, payment_status) VALUES (1, '2021-10-11', '08:00:00', 'No Permit', 40.00, 1);",
            "INSERT INTO Citation (citation_number, citation_date, citation_time, category, fee, payment_status) VALUES (2, '2023-10-01', '08:00:00', 'Expired Permit', 30.00, 0);",
        
            // INSERT statements for the IsAssigned table
            "INSERT INTO IsAssigned (phone, permit_id, car_license_number) VALUES ('7729119111', 1, 'SBF');",
            "INSERT INTO IsAssigned (phone, permit_id, car_license_number) VALUES ('266399121', 2, 'Clay1');",
            "INSERT INTO IsAssigned (phone, permit_id, car_license_number) VALUES ('366399121', 3, 'Hicks1');",
            "INSERT INTO IsAssigned (phone, permit_id, car_license_number) VALUES ('466399121', 4, 'Garcia1');",
            "INSERT INTO IsAssigned (phone, permit_id, car_license_number) VALUES ('122765234', 5, 'CRICKET');",
            "INSERT INTO IsAssigned (phone, permit_id, car_license_number) VALUES ('9194789124', 6, 'PROFX');",
        
            // INSERT statements for the Shows table
            "INSERT INTO Shows (citation_number, lot_name) VALUES (1, 'Dan Allen Parking Deck');",
            "INSERT INTO Shows (citation_number, lot_name) VALUES (2, 'Poulton Deck');",
        
            // INSERT statements for the HasZone table
            "INSERT INTO HasZone (permit_id, lot_name, zone_id) VALUES (1, 'Dan Allen Parking Deck', 'V');",
            "INSERT INTO HasZone (permit_id, lot_name, zone_id) VALUES (2, 'Poulton Deck', 'A');",
            "INSERT INTO HasZone (permit_id, lot_name, zone_id) VALUES (3, 'Poulton Deck', 'A');",
            "INSERT INTO HasZone (permit_id, lot_name, zone_id) VALUES (4, 'Poulton Deck', 'A');",
            "INSERT INTO HasZone (permit_id, lot_name, zone_id) VALUES (5, 'Partners Way Deck', 'AS');",
            "INSERT INTO HasZone (permit_id, lot_name, zone_id) VALUES (6, 'Dan Allen Parking Deck', 'V');",
        
            // INSERT statements for the GivenTo table
            "INSERT INTO GivenTo (citation_number, car_license_number) VALUES (1, 'VAN-9910');",
            "INSERT INTO GivenTo (citation_number, car_license_number) VALUES (2, 'CRICKET');"
        };
            
        for (String sql : insertStatements) {
            stmt.executeUpdate(sql);
        }

        System.out.println("Data inserted successfully.");
    
    }
}