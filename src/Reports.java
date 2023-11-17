package src;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Reports {
    public static void generateReportCitations(Connection connection) throws SQLException {
        // This query gives us the number of citations, the total number of vehicles to
        // which citations were given and the total fee.

        try (Statement stmt = connection.createStatement()) {
            String query = "SELECT COUNT(citation_number) AS number_citations, "
                    + "COUNT(DISTINCT car_license_number) AS number_vehicles, SUM(fee) AS total_fees "
                    + "FROM Shows NATURAL JOIN Citation NATURAL JOIN GivenTo;";

            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=======================RESULTS=======================");

            if (!rs.next()) {
                System.out.println("No records found");
            } else {
                System.out.println("Number of Citations, Number of Vehicles, Total Fees");
                do {
                    int numberCitations = rs.getInt("number_citations");
                    int numberVehicles = rs.getInt("number_vehicles");
                    float totalFees = rs.getFloat("total_fees");
                    System.out.println(numberCitations + ", " + numberVehicles + ", " + totalFees);
                } while (rs.next());
            }
            System.out.println("=======================END OF RESULTS=======================");
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void totalCitationsCountByTimeRange(Connection connection, Scanner sc) throws SQLException {
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given time range
        try (Statement stmt = connection.createStatement()) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr;
            Date startDate = null;

            // take user input for start date
            System.out.print("Enter Start Date (yyyy-MM-dd): ");
            startDateStr = sc.nextLine().trim();

            try {
                startDate = new Date(dateFormat.parse(startDateStr).getTime());
                System.out.println("Start Date: " + startDateStr);
            } catch (ParseException e) {
                System.out.println("Invalid input for Start Date. Please use the format yyyy-MM-dd.");
            }

            System.out.println("You entered the Start Date: " + startDate);

            String endDateStr;
            Date endDate = null;

            // take user input for end date
            System.out.print("Enter End Date(yyyy-MM-dd): ");
            endDateStr = sc.nextLine().trim();
            try {
                endDate = new Date(dateFormat.parse(endDateStr).getTime());
                System.out.println("End Date: " + endDateStr);
            } catch (ParseException e) {
                System.out.println("Invalid input for End Date. Please use the format yyyy-MM-dd.");
            }

            String query = "SELECT lot_name, COUNT(citation_number) AS number_citations,"
                    + " COUNT(DISTINCT car_license_number) AS number_vehicles, SUM(fee) AS total_fees"
                    + " FROM Shows NATURAL JOIN Citation NATURAL JOIN GivenTo"
                    + " WHERE citation_date BETWEEN '" + startDate + "' AND '" + endDate + "' "
                    + " GROUP BY lot_name;";

            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=======================RESULTS=======================");

            if (!rs.next()) {
                System.out.println("No records found");
            } else {
                System.out.println("Lot Name, Number of Citations, Number of Vehicles, Total Fees");

                do {
                    String lotName = rs.getString("lot_name");
                    int numberCitations = rs.getInt("number_citations");
                    int numberVehicles = rs.getInt("number_vehicles");
                    float totalFees = rs.getFloat("total_fees");
                    System.out.println(lotName + ", " + numberCitations + ", "
                            + numberVehicles + ", " + totalFees);
                } while (rs.next());
            }
            System.out.println("=======================END OF RESULTS=======================");
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void totalCitationsCountByMonth(Connection connection, Scanner sc) throws SQLException {
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given year
        // sc sc = new sc(System.in);
        try (Statement stmt = connection.createStatement()) {

            int month = 0;

            // take user input for month
            System.out.print("Enter Month as an integer: ");
            try {
                month = sc.nextInt();
                System.out.println("Month: " + month);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }

            String query = "SELECT lot_name, COUNT(citation_number) AS number_citations, "
                    + "COUNT(DISTINCT car_license_number) AS number_vehicles, SUM(fee) AS total_fees "
                    + "FROM Shows NATURAL JOIN Citation NATURAL JOIN GivenTo "
                    + "WHERE MONTH(citation_date) = " + month
                    + " GROUP BY lot_name;";

            ResultSet rs = stmt.executeQuery(query);

            System.out.println("=======================RESULTS=======================");

            if (!rs.next()) {
                System.out.println("No records found");
            } else {
                System.out.println("Lot Name, Number of Citations, Number of Vehicles, Total Fees");
                do {
                    String lotName = rs.getString("lot_name");
                    int numberCitations = rs.getInt("number_citations");
                    int numberVehicles = rs.getInt("number_vehicles");
                    float totalFees = rs.getFloat("total_fees");
                    System.out.println(lotName + ", " + numberCitations + ", "
                            + numberVehicles + ", " + totalFees);
                } while (rs.next());
            }
            System.out.println("=======================END OF RESULTS=======================");
            System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void totalCitationsCountByYear(Connection connection, Scanner sc) throws SQLException {
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given year
        try (Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false); // start transaction
            int year = 0;

            // take user input for year
            System.out.print("Enter Year: ");
            try {
                year = sc.nextInt();
                System.out.println("Year: " + year);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }

            String query = "SELECT lot_name, COUNT(citation_number) AS number_citations,"
                    + " COUNT(DISTINCT car_license_number) AS number_vehicles, SUM(fee) AS total_fees"
                    + " FROM Shows NATURAL JOIN Citation NATURAL JOIN GivenTo"
                    + " WHERE YEAR(citation_date) = " + year
                    + " GROUP BY lot_name;";

            try (PreparedStatement totalCitationCountByYearStatement = connection.prepareStatement(query)) {
                try (ResultSet rs = totalCitationCountByYearStatement.executeQuery()) {
                    System.out.println("=======================RESULTS=======================");
                    if (!rs.next()) {
                        System.out.println("No records found");
                    } else {
                        System.out.println("Lot Name, Number of Citations, Number of Vehicles, Total Fees");
                        do {
                            String lotName = rs.getString("lot_name");
                            int numberCitations = rs.getInt("number_citations");
                            int numberVehicles = rs.getInt("number_vehicles");
                            float totalFees = rs.getFloat("total_fees");
                            System.out.println(lotName + ", " + numberCitations + ", "
                                    + numberVehicles + ", " + totalFees);
                        } while (rs.next());
                    }
                    System.out.println("=======================END OF RESULTS=======================");
                }
            }
            connection.commit(); // end transaction
            System.out.println();
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            System.out.println("Error occurred while counting total citations in the year: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void listOfZones(Connection connection) throws SQLException {
        // this function lists all zones
        try (Statement stmt = connection.createStatement()) {

            String query = "SELECT * FROM Zone ORDER BY lot_name;";

            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=======================RESULTS=======================");

            if (!rs.next()) {
                System.out.println("No records found");
            } else {
                System.out.println("Zone Id, Lot Name");
                do {
                    String zoneId = rs.getString("zone_id");
                    String lotName = rs.getString("lot_name");
                    System.out.println(zoneId + ", " + lotName);
                } while (rs.next());
            }
            System.out.println("=======================END OF RESULTS=======================");
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void carsInViolation(Connection connection) throws SQLException {
        // Return the number of cars that are currently in violation
        try (Statement stmt = connection.createStatement()) {

            String query = "SELECT COUNT(DISTINCT car_license_number)AS number_cars_violation"
                    + " FROM GivenTo NATURAL JOIN Citation WHERE payment_status = 0;";

            ResultSet rs = stmt.executeQuery(query);

            System.out.println("=======================RESULTS=======================");

            if (!rs.next()) {
                System.out.println("No records found");
            } else {
                System.out.println("Number of Cars in Violation");
                do {
                    int numberCarsViolation = rs.getInt("number_cars_violation");
                    System.out.println(numberCarsViolation);
                } while (rs.next());
            }
            System.out.println("=======================END OF RESULTS=======================");
            System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void employeesHavePermits(Connection connection, Scanner sc) throws SQLException {
        // Return the number of employees having permits for a given parking zone
        try {
            String zone_id = "";
            String lot_name = "";
            connection.setAutoCommit(false); // start transaction

            try {
                // take zone id as an input
                System.out.print("Enter Zone Id: ");
                zone_id = sc.nextLine().trim();
                System.out.print("Enter parking lot name: ");
                lot_name = sc.nextLine().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String query = "SELECT COUNT(DISTINCT phone) as Number_Employees"
                    + " FROM IsAssigned NATURAL JOIN Permit NATURAL JOIN Driver NATURAL JOIN HasZone"
                    + " WHERE zone_id = ? and status='E' and lot_name = ?;";

            try (PreparedStatement employeeHavePermitsStatement = connection.prepareStatement(query)) {
                employeeHavePermitsStatement.setString(1, zone_id);
                employeeHavePermitsStatement.setString(2, lot_name);
                try (ResultSet rs = employeeHavePermitsStatement.executeQuery()) {
                    System.out.println("=======================RESULTS=======================");
                    if (!rs.next()) {
                        System.out.println("No records found");
                    } else {
                        System.out.println("Number of Employees");
                        do {
                            int numberEmployees = rs.getInt("number_employees");
                            System.out.println(numberEmployees);
                        } while (rs.next());
                    }
                    System.out.println("=======================END OF RESULTS=======================");
                }
            }
            connection.commit(); // end transaction
            System.out.println();
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            System.out.println("Error occurred while counting employee permit: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void returnPermitInfo(Connection connection, Scanner sc) throws SQLException {
        // Return permit information given an ID or phone number
        try (Statement stmt = connection.createStatement()) {

            int option = -1;
            int parameter = 0;

            // select using ID or phone number
            try {
                do {
                    System.out.println("1.Permit ID");
                    System.out.println("2.Phone Number");
                    System.out.print("Choose 1 or 2 to return permit information: ");
                    option = sc.nextInt();
                    sc.nextLine();

                    switch (option) {
                        case 1:
                            parameter = 1;
                            break;
                        case 2:
                            parameter = 2;
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                            continue;
                    }
                } while (parameter != 1 && parameter != 2);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // Consume the invalid input and discard it
            }

            String query = "";
            String permitIdInput = "";
            String phone = "";

            if (parameter == 1) {

                try {
                    System.out.print("Enter Permit ID: ");
                    permitIdInput = sc.nextLine().trim();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. ");
                    sc.nextLine(); // Consume the invalid input and discard it
                }

                query = "SELECT * FROM Permit JOIN IsAssigned ON Permit.permit_id = IsAssigned.permit_id JOIN HasZone ON Permit.permit_id = HasZone.permit_id WHERE Permit.permit_id = '"
                        + permitIdInput + "';";
                System.out.println(query);
            }

            else {
                try {
                    System.out.print("Enter phone number (do not include spaces or dashes): ");
                    phone = sc.nextLine().trim();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.nextLine(); // Consume the invalid input and discard it
                }
                query = "SELECT * FROM Permit JOIN IsAssigned ON Permit.permit_id = IsAssigned.permit_id " 
                + "JOIN HasZone ON Permit.permit_id = HasZone.permit_id WHERE Permit.permit_id in (SELECT permit_id FROM IsAssigned WHERE phone = '" + phone + "');";
            }

            ResultSet rs = stmt.executeQuery(query);
            // permit_id | space_type | start_date | expiration_date | expiration_time |
            // permit_type
            System.out.println("=======================RESULTS=======================");

            if (!rs.next()) {
                System.out.println("No records found");
            } else {
                System.out.println(
                        "Permit Id Space Type, Start Date, Expiration Date, Expiration Time, Permit Type, Car License Number, Zone Id, Lot Name");
                do {
                    int permitId = rs.getInt("permit_id");
                    String spaceType = rs.getString("space_type");
                    Date startDate = rs.getDate("start_date");
                    Date expirationDate = rs.getDate("expiration_date");
                    Time expirationTime = rs.getTime("expiration_time");
                    String permitType = rs.getString("permit_type");
                    String carLicenseNumber = rs.getString("car_license_number");
                    String zoneId = rs.getString("zone_id");
                    String lotName = rs.getString("lot_name");
                    System.out.println(permitId + ", " + spaceType + ", "
                            + startDate + ", " + expirationDate + ", " + expirationTime + ", "
                            + permitType + ", " + carLicenseNumber + ", " + zoneId + ", " + lotName);
                } while (rs.next());
            }
            System.out.println("=======================END OF RESULTS=======================");
            System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void generateSpaceAvailable(Connection connection, Scanner sc) throws SQLException {
        // Return an available space number given a space type in a given parking lot

        try (Statement stmt = connection.createStatement()) {

            int option = -1;
            String space_type = "";

            do {
                try {
                    System.out.println("1.Regular");
                    System.out.println("2.Electric");
                    System.out.println("3.Compact Car");
                    System.out.println("4.Handicap");
                    System.out.print("Enter Space Type (as an integer): ");
                    option = sc.nextInt();
                    sc.nextLine();

                    switch (option) {
                        case 1:
                            space_type = "Regular";
                            break;
                        case 2:
                            space_type = "Electric";
                            break;
                        case 3:
                            space_type = "Compact Car";
                            break;
                        case 4:
                            space_type = "Handicap";
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                            continue;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    sc.nextLine(); // Consume the invalid input and discard it
                }
            } while (option > 4);

            String lot_name = "";

            try {
                lot_name = sc.nextLine().trim();

            } catch (InputMismatchException e) {
                System.out.println("Invalid input");
                sc.nextLine(); // Consume the invalid input and discard it
            }

            String query = "SELECT space_number FROM Space"
                    + " WHERE space_type = '" + space_type
                    + "' AND lot_name = '" + lot_name
                    + "' AND availability_status = 1 LIMIT 1;";

            ResultSet rs = stmt.executeQuery(query);

            System.out.println("=======================RESULTS=======================");

            if (!rs.next()) {
                System.out.println("No records found");
            } else {
                System.out.println("Space number");
                do {
                    int spaceNumber = rs.getInt("space_number");
                    System.out.println(spaceNumber);
                } while (rs.next());
            }
            System.out.println("=======================END OF RESULTS=======================");
            System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
