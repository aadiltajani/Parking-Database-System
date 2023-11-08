import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    // Update your user info alone here
    Properties properties = new Properties();
    FileInputStream input = null;
    String user = null;
    String password = null;

    System.out.println("Success");

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

    String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/" + user;
    Class.forName("org.mariadb.jdbc.Driver");

    Connection conn = DriverManager.getConnection(jdbcURL, user, password);
    Statement stmt = conn.createStatement();

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        //load demo data every time we start Main.java
        DemoDataLoad demodataload = new DemoDataLoad();

        int option = sc.nextInt();

        do {
            System.out.println("Choose the operation from the main menu by inputting the respective number:");
            System.out.println("1. Information Processing");
            System.out.println("2. Maintaining permits and vehicle information for each driver");
            System.out.println("3. Generating and maintaining citations");
            System.out.println("4. Reports");
            System.out.println("100. Exit");

            String query = new String();
            switch (option) {
                case 1:
                    query = informationProcessingMenu();
                    break;
                case 2:
                    query = maintainingPermitsMenu();
                    break;
                case 3:
                    query = generatingCitationsMenu();
                    break;
                case 4:
                    query = reportsMenu();
                    break;
                case 100:
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            } while (query.isEmpty());


        try {

            // Loading the driver. This creates an instance of the driver
            // and calls the registerDriver method to make MySql(MariaDB) Thin available to
            // clients.
            Class.forName("org.mariadb.jdbc.Driver");

            Connection connection = null;
            Statement statement = null;
            ResultSet result = null;

            try {
                // Get a connection instance from the first driver in the
                // DriverManager list that recognizes the URL jdbcURL
                connection = DriverManager.getConnection(jdbcURL, user, password);

                // Create a statement instance that will be sending
                // your SQL statements to the DBMS
                statement = connection.createStatement();

                // Create the CATS table
                statement.executeUpdate(query);

                // Get records from the CATS table
                result = statement.executeQuery("SELECT CNAME, WEIGHT FROM CATS");

                // Now result contains the rows of cat names and weights from
                // the CATS table. To access the data, use the method
                // NEXT to access all rows in result, one row at a time
                while (result.next()) {
                    String name = result.getString("CNAME");
                    float weight = result.getFloat("WEIGHT");
                    System.out.println(name + "  " + weight);
                }
            } finally {
                close(result);
                close(statement);
                close(connection);
            }
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    } while (option != 100);
    }

    static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable whatever) {
            }
        }
    }

    static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Throwable whatever) {
            }
        }
    }

    static void close(ResultSet result) {
        if (result != null) {
            try {
                result.close();
            } catch (Throwable whatever) {
            }
        }
    }

    private static String informationProcessingMenu() {
        int option;
        String query = new String();
        do {
            System.out.println("\nInformation Processing Menu:");
            System.out.println("Choose the operation from the menu by inputting the respective number:");
            System.out.println("1.Enter driver info");
            System.out.println("2.Update driver info");
            System.out.println("3.Delete driver info");
            System.out.println("4.Enter parking lot info");
            System.out.println("5.Update parking lot info");
            System.out.println("6.Delete parking lot info");
            System.out.println("7.Enter parking lot info");
            System.out.println("8.Enter zone info");
            System.out.println("9.Update lot info");
            System.out.println("10.Delete lot info");
            System.out.println("11.Enter space info");
            System.out.println("12.Update space info");
            System.out.println("13.Delete space info");
            System.out.println("14.Enter permit info");
            System.out.println("15.Update permit info");
            System.out.println("16.Delete permit info");
            System.out.println("17.Assign zones to each parking lot");
            System.out.println("18.Assign a type to a given space.");
            System.out.println("19.Request citation appeal");
            System.out.println("20.Update citation payment");
            System.out.println("100. Return to main menu");
            option = sc.nextInt();

            switch (option) {
                case 1:
                    infoProcessing enterDriverInfo = new infoProcessing();
                    query = enterDriverInfo.enterDriverInfo();
                    // Implement code for entering driver information
                    break;
                case 2:
                    // Update driver info
                    infoProcessing updateDriverInfo = new infoProcessing();
                    query = updateDriverInfo.updateDriverInfo();
                    // Implement code for updating driver information
                    break;
                case 3:
                    // Delete driver info
                    // Implement code for deleting driver information
                    break;
                case 4:
                    // Enter parking lot info
                    // Implement code for entering parking lot information
                    break;
                case 5:
                    // Update parking lot info
                    // Implement code for updating parking lot information
                    break;
                case 6:
                    // Delete parking lot info
                    // Implement code for deleting parking lot information
                    break;
                case 7:
                    // Enter parking lot info
                    // Implement code for entering parking lot information
                    break;
                case 8:
                    // Enter zone info
                    // Implement code for entering zone information
                    break;
                case 9:
                    // Update lot info
                    // Implement code for updating zone information
                    break;
                case 10:
                    // Delete lot info
                    // Implement code for deleting zone information
                    break;
                case 11:
                    // Enter space info
                    // Implement code for entering space information
                    break;
                case 12:
                    // Update space info
                    // Implement code for updating space information
                    break;
                case 13:
                    // Delete space info
                    // Implement code for deleting space information
                    break;
                case 14:
                    // Enter permit info
                    // Implement code for entering permit information
                    break;
                case 15:
                    // Update permit info
                    // Implement code for updating permit information
                    break;
                case 16:
                    // Delete permit info
                    // Implement code for deleting permit information
                    break;
                case 17:
                    // Assign zones to each parking lot
                    // Implement code for assigning zones to parking lots
                    break;
                case 18:
                    // Assign a type to a given space
                    // Implement code for assigning space type
                    break;
                case 19:
                    // Request citation appeal
                    // Implement code for appealing a citation
                    break;
                case 20:
                    // Update citation payment
                    // Implement code for updating citation payment
                    break;
                case 100:
                    System.out.println("Exiting...");
                    sc.close();
                    break; // Return to main menu
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 100);
        return query;
    }

    private static String maintainingPermitsMenu() {
        int option;
        String query = new String();
        do {
            System.out.println("Choose the operation from the menu by inputting the respective number:");
            System.out.println("\nMaintaining Permits Menu:");
            System.out.println("1. Assign permits to drivers");
            System.out.println("2. Enter permit information");
            System.out.println("3. Update permit information");
            System.out.println("4. Add vehicle");
            System.out.println("5. Update vehicle ownership information");
            System.out.println("6. Remove vehicle");
            System.out.println("100. Return to main menu");
            option = sc.nextInt();

            switch (option) {
                case 1:
                    // Logic to assign permits to drivers
                    break;
                case 2:
                    // Logic to enter permit information
                    break;
                case 3:
                    // Logic to update permit information
                    break;
                case 4:
                    // Logic to add vehicle
                    break;
                case 5:
                    // Logic to update vehicle ownership information
                    break;
                case 6:
                    // Logic to remove vehicle
                    break;
                case 100:
                    System.out.println("Exiting...");
                    sc.close();
                    break; // Return to main menu
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 100);
        return query;
    }

    private static String generatingCitationsMenu() {
        int option;
        String query = new String();
        do {
            System.out.println("Choose the operation from the menu by inputting the respective number:");
            System.out.println("\nGenerating and Maintaining Citations Menu:");
            System.out.println(
                    "1.Detect parking violations by checking if a car has a valid permit in the lot,zone and space.");
            System.out.println("2.Generate a citation");
            // need to change 29 based on tbd functions
            System.out.println("3.Maintain a citation");
            System.out.println("4.Pay citation");
            System.out.println("5.Appeal citation");
            System.out.println("6. Return to main menu");
            option = sc.nextInt();

            switch (option) {
                // case 27 to 31:
                // submenu options
                // break;
                case 100:
                    System.out.println("Exiting...");
                    sc.close();
                    break; // Return to main menu
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 100);
        return query;
    }

    private static String reportsMenu() {
        int option;
        String query = new String();
        do {
            System.out.println("Choose the operation from the menu by inputting the respective number:");
            System.out.println("\nReports Menu:");
            System.out.println("1. Generate a report for citations");
            System.out.println(
                    "2. For each lot, generate a report for the total number of citations given in all zones in the lot for a given month");
            System.out.println(
                    "3. For each lot, generate a report for the total number of citations given in all zones in the lot for a given year");
            System.out.println("4. Return the list of zones for each lot as tuple pairs (lot, zone)");
            System.out.println("5. Return the number of cars that are currently in violation");
            System.out.println("6. Return the number of employees having permits for a given parking zone");
            System.out.println("7. Return permit information given an ID or phone number");
            System.out.println("8. Return an available space number given a space type in a given parking lot");
            System.out.println("100. Return to main menu");
            option = sc.nextInt();

            switch (option) {
                case 1:
                    // Generate a report for citations
                    // Implement code to generate the citation report
                    break;
                case 2:
                    // For each lot, generate a report for the total number of citations given in
                    // all zones in the lot for a given month
                    // Implement code for generating the monthly citation report for each lot
                    break;
                case 3:
                    // For each lot, generate a report for the total number of citations given in
                    // all zones in the lot for a given year
                    // Implement code for generating the yearly citation report for each lot
                    break;
                case 4:
                    // Return the list of zones for each lot as tuple pairs (lot, zone)
                    // Implement code to return the list of zones for each parking lot
                    break;
                case 5:
                    // Return the number of cars that are currently in violation
                    // Implement code to count the number of cars in violation
                    break;
                case 6:
                    // Return the number of employees having permits for a given parking zone
                    // Implement code to count the employees with permits in a specific zone
                    break;
                case 7:
                    // Return permit information given an ID or phone number
                    // Implement code to retrieve permit information by ID or phone number
                    break;
                case 8:
                    // Return an available space number given a space type in a given parking lot
                    // Implement code to find an available space of a specific type in a parking lot
                    break;
                case 100:
                    System.out.println("Exiting...");
                    sc.close();
                    break; // Return to main menu
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 100);
        return query;
    }
}
