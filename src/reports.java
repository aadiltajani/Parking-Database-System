package src;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

public class reports {
    public void generateReportCitations(Connection connection) throws SQLException {
        // This query gives us the number of citations, the total number of vehicles to
        // which citations were given and the total fee.
        Scanner scanner = new Scanner(System.in);
        try (Statement stmt = connection.createStatement()) {
            String query = "SELECT COUNT(citation_number) AS number_citations,"
                    + "COUNT(DISTINCT car_license_number) AS number_vehicles, SUM(fee) AS total_fees"
                    + "FROM Shows NATURAL JOIN Citation NATURAL JOIN GivenTo;";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int numberCitations = rs.getInt("number_citations");
                int numberVehicles = rs.getInt("SUP_ID");
                float totalFees = rs.getFloat("total_fees");
                System.out.println(numberCitations + ", " + numberVehicles + ", " + totalFees);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public void totalCitationsCountByTimeRange(Connection connection) throws SQLException {
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given time range (e.g., monthly or annually).
        Scanner scanner = new Scanner(System.in);
        try (Statement stmt = connection.createStatement()) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr;
            Date startDate = null;

            while (true) {
                System.out.print("Enter Start Date (yyyy-MM-dd): ");
                startDateStr = scanner.nextLine();

                try {
                    startDate = new Date(dateFormat.parse(startDateStr).getTime());
                    System.out.println("Start Date: " + startDateStr);
                    break; // Exit the loop when a valid date is entered
                } catch (ParseException e) {
                    System.out.println("Invalid input for Start Date. Please use the format yyyy-MM-dd.");
                }
            }

            System.out.println("You entered the Start Date: " + startDate);

            String endDateStr;
            Date endDate = null;

            while (true) {
                System.out.print("Enter End Date(yyyy-MM-dd): ");
                endDateStr = scanner.nextLine();
                try {
                    endDate = new Date(dateFormat.parse(endDateStr).getTime());
                    System.out.println("End Date: " + endDateStr);
                    break; // Exit the loop when a valid date is entered
                } catch (ParseException e) {
                    System.out.println("Invalid input for End Date. Please use the format yyyy-MM-dd.");
                }
            }

            String query = "SELECT lot_name, COUNT(citation_number) AS number_citations,"
                    + "COUNT(DISTINCT car_license_number) AS number_vehicles, SUM(fee) AS total_fees"
                    + "FROM Shows NATURAL JOIN Citation NATURAL JOIN GivenTo"
                    + "WHERE citation_date BETWEEN '" + startDate + "' AND '" + endDate + "' "
                    + "GROUP BY lot_name;";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lotName = rs.getString("lot_name");
                int numberCitations = rs.getInt("number_citations");
                int numberVehicles = rs.getInt("number_vehicles");
                float totalFees = rs.getFloat("total_fees");
                System.out.println(lotName + ", " + numberCitations + ", "
                        + numberVehicles + ", " + totalFees);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public void totalCitationsCountMonthly(Connection connection) throws SQLException {
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given year
        Scanner scanner = new Scanner(System.in);
        try (Statement stmt = connection.createStatement()) {

            int month;

            while (true) {
                System.out.print("Enter Month as an integer: ");
                try {
                    month = scanner.nextInt();
                    System.out.println("Month: " + month);
                    break; // Exit the loop when a valid date is entered
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter an integer.");
                }
            }

            String query = "SELECT lot_name, COUNT(citation_number) AS number_citations,"
                    + "COUNT(DISTINCT car_license_number) AS number_vehicles, SUM(fee) AS total_fees"
                    + "FROM Shows NATURAL JOIN Citation NATURAL JOIN GivenTo"
                    + "WHERE MONTH(citation_date) = " + month
                    + "GROUP BY lot_name;";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lotName = rs.getString("lot_name");
                int numberCitations = rs.getInt("number_citations");
                int numberVehicles = rs.getInt("number_vehicles");
                float totalFees = rs.getFloat("total_fees");
                System.out.println(lotName + ", " + numberCitations + ", "
                        + numberVehicles + ", " + totalFees);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }

    }

    public void totalCitationsCountYearly(Connection connection) throws SQLException {
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given year
        Scanner scanner = new Scanner(System.in);
        try (Statement stmt = connection.createStatement()) {

            int year;

            while (true) {
                System.out.print("Enter Year: ");
                try {
                    year = scanner.nextInt();
                    System.out.println("Year: " + year);
                    break; // Exit the loop when a valid date is entered
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter an integer.");
                }
            }

            String query = "SELECT lot_name, COUNT(citation_number) AS number_citations,"
                    + "COUNT(DISTINCT car_license_number) AS number_vehicles, SUM(fee) AS total_fees"
                    + "FROM Shows NATURAL JOIN Citation NATURAL JOIN GivenTo"
                    + "WHERE YEAR(citation_date) = " + year
                    + "GROUP BY lot_name;";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String lotName = rs.getString("lot_name");
                int numberCitations = rs.getInt("number_citations");
                int numberVehicles = rs.getInt("number_vehicles");
                float totalFees = rs.getFloat("total_fees");
                System.out.println(lotName + ", " + numberCitations + ", "
                        + numberVehicles + ", " + totalFees);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public void listOfZones(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            String query = "SELECT * FROM Zone ORDER  BY lot_name;";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String zoneId = rs.getString("zone_id");
                String lotName = rs.getString("lot_name");
                System.out.println(zoneId + ", " + lotName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void carsInViolation(Connection connection) throws SQLException {
        // Return the number of cars that are currently in violation
        try (Statement stmt = connection.createStatement()) {

            String query = "SELECT COUNT(DISTINCT car_license_number)AS number_cars_violation" 
            + "FROM GivenTo NATURAL JOIN Citation WHERE payment_status = 0;";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int numberCarsViolation = rs.getInt("number_cars_violation");
                System.out.println(numberCarsViolation );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void employeesHavePermits(Connection connection) throws SQLException {
        // Return the number of employees having permits for a given parking zone
        try (Statement stmt = connection.createStatement()) {

            String query = "SSELECT COUNT(DISTINCT phone) as Number_Employees"  
            + "FROM IsAssigned NATURAL JOIN Permit NATURAL JOIN Driver NATURAL JOIN HasZone"
            + "WHERE zone_id= 'BS' and status='E';";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int numberEmployees = rs.getInt("number_employees");
                System.out.println(numberEmployees );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnPermitInfo(Connection connection) throws SQLException {
        // Return permit information given an ID or phone number
        Scanner scanner = new Scanner(System.in);
        try (Statement stmt = connection.createStatement()) {

            int option = -1;
            int parameter = 0;

            //select using ID or phone number
            do {
                    try {
                        System.out.println("1.Permit ID");
                        System.out.println("2.Phone Number");
                        System.out.print("Choose 1 or 2 to return permit information: ");
                        option = scanner.nextInt();

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
                    }
                    catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Consume the invalid input and discard it
                    }
                } while (option > 2);

            String query = "";
            int permitIdInput = -1;
            String phone = ""; 

            if (parameter == 1) {
                while(true) {
                    try {
                        System.out.print("Enter Permit ID (as an integer): ");
                        permitIdInput = scanner.nextInt();
                        break;
                    }
                    catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine(); // Consume the invalid input and discard it
                    }
                }
                query = "SELECT * FROM Permit WHERE permit_id =" + permitIdInput + ";";
            }
                
            else {
                while(true) {
                    try {
                        System.out.print("Enter phone number (do not include spaces or dashes): ");
                        phone = scanner.nextLine();
                        break;
                    }
                    catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine(); // Consume the invalid input and discard it
                    }
                }
                query = "SELECT * FROM Permit WHERE permit_id in (SELECT permit_id FROM IsAssigned WHERE phone =" + phone + ");";
            }

            ResultSet rs = stmt.executeQuery(query);
            // permit_id | space_type | start_date | expiration_date | expiration_time | permit_type 
            while (rs.next()) {
                int permitId = rs.getInt("permit_id");
                String spaceType = rs.getString("space_type");
                Date startDate = rs.getDate("start_date");
                Date expirationDate = rs.getDate("expiration_date");
                Time expirationTime = rs.getTime("expiration_time");
                String permitType = rs.getString("permit_type");
                System.out.println(permitId + ", " + spaceType + ", " + startDate + ", " + expirationDate + ", " + expirationTime 
                + ", " + permitType) ;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }

    }

    public void generateSpaceAvailable(Connection connection) throws SQLException {
        // Return an available space number given a space type in a given parking lot

        Scanner scanner = new Scanner(System.in);

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
                    option = scanner.nextInt();

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
                }
                catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input and discard it
                }
            } while (option > 4);

            String lot_name = ""; 
            do {
                try {
                    System.out.println("1.Dan Allen Parking Deck");
                    System.out.println("2.Partners Way Deck");
                    System.out.println("3.Poulton Deck");
                    System.out.print("Enter Parking Lot (as an integer): ");
                    option = scanner.nextInt();

                    switch (option) {
                        case 1:
                            lot_name = "Dan Allen Parking Deck";
                            break;
                        case 2: 
                            lot_name = "Partners Way Deck";
                            break;
                        case 3:
                            lot_name = "Poulton Deck";
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                            continue;
                    }
                }
                catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input and discard it
                }
            } while (option > 3);

            String query = "SELECT space_number FROM Space"
            + "WHERE space_type = " + space_type
            + " AND lot_name = " + lot_name
            + "AND availability_status = 1 LIMIT 1;";

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int spaceNumber = rs.getInt("space_number");
                System.out.println(spaceNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

}
