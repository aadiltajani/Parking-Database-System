import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DemoDataLoad {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Properties properties = new Properties();
        FileInputStream input = null;
        String user = null;
        String password = null;

        try {
            input = new FileInputStream("db_keys");
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
            createTables(stmt);
            // Insert data from the JSON
            // insertData(stmt);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stmt.close();
        conn.close();
    }

    private static void createTables(Statement stmt) throws Exception {
        String createDriverTableSQL = "CREATE TABLE Driver (\n" +
            "phone VARCHAR(10),\n" +
            "name VARCHAR(128) NOT NULL,\n" +
            "status VARCHAR(1) NOT NULL,\n" +
            "univ_id VARCHAR(9),\n" +
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

        // Create HasLot table
        String createHasLotTableSQL = "CREATE TABLE HasLot (" +
            "permit_id INT," +
            "lot_name VARCHAR(128)," +
            "PRIMARY KEY (permit_id, lot_name)," +
            "FOREIGN KEY (permit_id) REFERENCES Permit(permit_id) ON UPDATE CASCADE," +
            "FOREIGN KEY (lot_name) REFERENCES ParkingLot(lot_name) ON UPDATE CASCADE" +
            ")";
        stmt.execute(createHasLotTableSQL);

        // Create HasZone table
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

        // Create GivenTo table
        String createGivenToTableSQL = "CREATE TABLE GivenTo (" +
            "citation_number INT," +
            "car_license_number VARCHAR(10)," +
            "PRIMARY KEY (citation_number, car_license_number)," +
            "FOREIGN KEY (citation_number) REFERENCES Citation(citation_number) ON UPDATE CASCADE," +
            "FOREIGN KEY (car_license_number) REFERENCES Vehicle(car_license_number) ON UPDATE CASCADE" +
            ")";
        stmt.execute(createGivenToTableSQL);

    }
}
