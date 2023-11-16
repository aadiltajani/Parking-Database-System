package src;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class Citations {
    // Function to detect violations
    public static void detectParkingViolations(Connection connection, Scanner scanner) throws Exception{
        // Scanner scanner = new Scanner(System.in);

        try {
            scanner.nextLine();
            System.out.print("Enter Lot Name: ");
            String lotName = scanner.nextLine().trim();

            System.out.print("Enter Zone ID: ");
            String zoneId = scanner.nextLine().trim();

            System.out.print("Enter Space Type: ");
            String spaceType = scanner.nextLine().trim();

            System.out.print("Enter Car License Number: ");
            String carLicenseNumber = scanner.nextLine().trim();

            System.out.print("Enter Citation Date(YYYY-MM-DD): ");
            String expiration_date_str = scanner.nextLine();
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(expiration_date_str);
            Date expiration_date = new Date(utilDate.getTime());

            System.out.print("Enter Citation Time(HH:MM:SS): ");
            String expiration_time_str = scanner.nextLine().trim();
            Time expiration_time = Time.valueOf(expiration_time_str);

            String query = "SELECT P.permit_id, P.space_type, P.expiration_date, P.expiration_time, H.zone_id, H.lot_name " +
                           "FROM IsAssigned IA " +
                           "JOIN Permit P ON IA.permit_id = P.permit_id " +
                           "JOIN HasZone H ON H.permit_id = P.permit_id " +
                           "WHERE H.lot_name = ? " +
                           "AND H.zone_id = ? " +
                           "AND P.space_type = ? " +
                           "AND IA.car_license_number = ? " +
                           "AND (P.expiration_date > ? " +
                           "OR (P.expiration_time > ? AND P.expiration_date = ?))";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, lotName);
                preparedStatement.setString(2, zoneId);
                preparedStatement.setString(3, spaceType);
                preparedStatement.setString(4, carLicenseNumber);
                preparedStatement.setDate(5, expiration_date);
                preparedStatement.setTime(6, expiration_time);
                preparedStatement.setDate(7, expiration_date);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("No parking violation detected. Valid permit found.");
                    } else {
                        System.out.println("Parking violation detected.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while detecting parking violations: " + e.getMessage());
        } finally {
            // scanner.close();
        }
    }
    // Function to generate a citation
    public static void generateCitation(Connection connection, Scanner scanner) throws Exception {
        try{
            String insertCitationQuery = "INSERT INTO Citation VALUES (?, ?, ?, ?, ?, ?)";
            String insertShowsQuery = "INSERT INTO Shows VALUES (?, ?)";
            String insertGivenToQuery = "INSERT INTO GivenTo VALUES (?, ?)";
            String insertVehicleQuery = "INSERT INTO Vehicle VALUES (?, ?, ?, ?, ?)";
            // Scanner scanner = new Scanner(System.in);

            System.out.println("Enter citation details:");

            System.out.print("Citation Number: ");
            int citation_number = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Category(Invalid Permit, Expired Permit, No Permit): ");
            String category = scanner.nextLine().trim();
            List<String> permitCategories = List.of("Expired Permit", "Invalid Permit", "No Permit");
            if (!permitCategories.contains(category)){
                // scanner.close();
                System.out.println("Invalid Category");
                return;
            }
            
            System.out.print("Car License Number: ");
            String car_license_number = scanner.next().trim();
            
            // Get Space Type for Fee 
            String space_type = "";
            String query = "SELECT p.space_type " +
                            "FROM Vehicle v " +
                            "JOIN IsAssigned ia ON v.car_license_number = ia.car_license_number " +
                            "JOIN Permit p ON ia.permit_id = p.permit_id " +
                            "WHERE v.car_license_number = ?";
            String model = null;
            String color = null;
            Boolean insertVehicle = false;
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, car_license_number);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Car license number exists, and space type is retrieved
                        space_type = resultSet.getString("space_type");
                        System.out.println("Vehicle Information with Permit Found!");
                    } else {
                        String vehicle_query = "SELECT v.car_license_number " +
                            "FROM Vehicle v " +
                            "WHERE v.car_license_number = ?";
                        try (PreparedStatement vehiclePreparedStatement = connection.prepareStatement(vehicle_query)) {
                            vehiclePreparedStatement.setString(1, car_license_number);
                            try (ResultSet vehicleResultSet = vehiclePreparedStatement.executeQuery()) {
                                if (!vehicleResultSet.next()) {
                                    // Car license number not found, take vehicle info to add it in DB
                                    scanner.nextLine();
                                    System.out.print("Car Model: ");
                                    model = scanner.nextLine().trim();
                                    System.out.print("Car Color: ");
                                    color = scanner.nextLine().trim();
                                    insertVehicle = true;
                                }
                            }
                        }
                    }
                }
            }

            // Assign Fee value and check for handicap discount
            float fee = 0;
            if (category.equals("No Permit")) fee = (float) 40.0;
            else if (category.equals("Invalid Permit")) fee = (float) 25.0;
            else if (category.equals("Expired Permit")) fee = (float) 30.0;
            if (space_type.equals("Handicap")) fee = fee / 2;

            System.out.print("Citation Date (YYYY-MM-DD): ");
            String citation_date_str = scanner.next();
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(citation_date_str);
            Date citation_date = new Date(utilDate.getTime());
            
            System.out.print("Citation Time (HH:mm:ss): ");
            String citation_time_str = scanner.next();
            Time citation_time = Time.valueOf(citation_time_str);

            scanner.nextLine();
            System.out.print("Lot Name: ");
            String lot_name = scanner.nextLine().trim();
            
            System.out.print("Payment Status (0 for unpaid, 1 for paid): ");
            boolean payment_status = scanner.nextInt() == 0 ? false : true;
            
            // Execute insert queries in transaction in all the tables at the same time
            try{
                connection.setAutoCommit(false); // set autocommit to false before executing statements
                // insert in citation
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertCitationQuery)) {
                    preparedStatement.setInt(1, citation_number);
                    preparedStatement.setDate(2, citation_date);
                    preparedStatement.setTime(3, citation_time);
                    preparedStatement.setString(4, category);
                    preparedStatement.setFloat(5, fee);
                    preparedStatement.setBoolean(6, payment_status);
                    preparedStatement.executeUpdate(); // execute query
                } catch (Exception e) {
                    connection.rollback(); // in case there is an issue, rollback, return back to menu to avoid any further and go to finally
                    System.out.println("Error Occurred while inserting citation data " + e.getMessage());
                    return;
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertShowsQuery)) {
                    // insert in shows
                    preparedStatement.setInt(1, citation_number);
                    preparedStatement.setString(2, lot_name);
                    preparedStatement.executeUpdate();
                    System.out.println("Citation Lot Assigned successfully.");
                } catch (Exception e) {
                    connection.rollback(); // in case there is an issue, rollback, return back to menu to avoid any further and go to finally
                    System.out.println("Error Occurred while inserting lot data " + e.getMessage());
                    return;
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertGivenToQuery)) {
                    // insert in givento
                    preparedStatement.setInt(1, citation_number);
                    preparedStatement.setString(2, car_license_number);
                    preparedStatement.executeUpdate();
                } catch (Exception e) {
                    connection.rollback(); // in case there is an issue, rollback, return back to menu to avoid any further and go to finally
                    System.out.println("Error Occurred while inserting lot data " + e.getMessage());
                    return;
                }
                if (insertVehicle && !model.equals(null) && !color.equals(null)){
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertVehicleQuery)) {
                        // insert in vehicle if not found in DB
                        preparedStatement.setString(1, car_license_number);
                        preparedStatement.setString(2, model);
                        preparedStatement.setInt(3, 0000);
                        preparedStatement.setString(4, color);
                        preparedStatement.setString(5, "N/A");
                        preparedStatement.executeUpdate();
                    } catch (Exception e) {
                        connection.rollback(); // in case there is an issue, rollback, return back to menu to avoid any further and go to finally
                        System.out.println("Error Occurred while inserting vehicle data " + e.getMessage());
                        return;
                    }   
                }
                connection.commit(); // all execution went well, so commit the transaction
                System.out.println("Citation generated successfully.");
            } catch (SQLException e) {
                System.out.println("Error Occurred while managing transaction: " + e.getMessage());
            } finally {
                try { // transaction is complete so set autocommit back to true
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Error Occurred while getting data " + e.getMessage());
        }
    }

    // Function to maintain a citation
    public static void maintainCitation(Connection connection, Scanner scanner) throws Exception {
        // Scanner scanner = new Scanner(System.in);

        try {
            // Take user input for Citation Number
            System.out.print("Enter Citation Number: ");
            int citation_number = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Check if the citation number exists
            String selectQuery = "SELECT * FROM Citation WHERE citation_number = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, citation_number);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        System.out.println("Citation with Citation Number " + citation_number + " does not exist.");
                        return;
                    }
                }
            }
            
            // Display current details of the citation
            DisplayGetCitation(connection, citation_number);
            selectQuery = "SELECT C.*, S.lot_name, G.car_license_number, A.appeal_status " +
            "FROM Citation C " +
            "INNER JOIN Shows S ON C.citation_number = S.citation_number " +
            "INNER JOIN GivenTo G ON C.citation_number = G.citation_number " +
            "LEFT JOIN Appeals A ON C.citation_number = A.citation_number " +
            "WHERE C.citation_number = ?";
                
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, citation_number);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()){
                        Date citation_date = resultSet.getDate("C.citation_date");
                        Time citation_time = resultSet.getTime("C.citation_time");
                        String category = resultSet.getString("C.category");
                        float fee = resultSet.getFloat("C.fee");
                        boolean payment_status = resultSet.getBoolean("C.payment_status");
                        String lot_name = resultSet.getString("S.lot_name");
                        String car_license_number = resultSet.getString("G.car_license_number");
                        System.out.print("\nDo you want to make changes to this citation? (yes/no): ");
                        String userChoice = scanner.nextLine().trim().toLowerCase();
                        String appeal_status = resultSet.getString("A.appeal_status");

                        if (userChoice.equals("yes")) {
                            // Take user input for new details

                            System.out.print("Do you want to change the Citation Date? (yes/no): ");
                            if (scanner.next().trim().equalsIgnoreCase("yes")) {
                                System.out.print("Citation Date (YYYY-MM-DD): ");
                                String citation_date_str = scanner.next();
                                java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(citation_date_str);
                                citation_date = new Date(utilDate.getTime());
                            }

                            System.out.print("Do you want to change the Citation Time? (yes/no): ");
                            if (scanner.next().trim().equalsIgnoreCase("yes")) {
                                System.out.print("Citation Time (HH:mm:ss): ");
                                String citation_time_str = scanner.next();
                                citation_time = Time.valueOf(citation_time_str);
                            }

                            scanner.nextLine();

                            System.out.print("Do you want to change the Category? (yes/no): ");
                            if (scanner.next().trim().equalsIgnoreCase("yes")) {
                                scanner.nextLine();
                                System.out.print("Category(Invalid Permit, Expired Permit, No Permit): ");
                                category = scanner.nextLine().trim();
                                List<String> permitCategories = List.of("Expired Permit", "Invalid Permit", "No Permit");
                                if (!permitCategories.contains(category)) {
                                    // scanner.close();
                                    System.out.println("Invalid Category");
                                    return;
                                }
                            }

                            System.out.print("Do you want to change the Car License Number? (yes/no): ");
                            if (scanner.next().trim().equalsIgnoreCase("yes")) {
                                System.out.print("Car License Number: ");
                                car_license_number = scanner.next().trim();
                            }

                            System.out.print("Do you want to change the Fee? (yes/no): ");
                            if (scanner.next().trim().equalsIgnoreCase("yes")) {
                                System.out.print("Fee: ");
                                fee = scanner.nextFloat();
                            }

                            scanner.nextLine();

                            System.out.print("Do you want to change the Lot Name? (yes/no): ");
                            if (scanner.next().trim().equalsIgnoreCase("yes")) {
                                scanner.nextLine();
                                System.out.print("Lot Name: ");
                                lot_name = scanner.nextLine().trim();
                            }

                            System.out.print("Do you want to change the Payment Status? (yes/no): ");
                            if (scanner.next().trim().equalsIgnoreCase("yes")) {
                                System.out.print("Payment Status (0 for unpaid, 1 for paid): ");
                                payment_status = scanner.nextInt() == 0 ? false : true;
                            }
                            
                            if(appeal_status != null) {
                                System.out.print("Do you want to change the Appeal Status? (yes/no): ");
                                if (scanner.next().trim().equalsIgnoreCase("yes")) {
                                    System.out.print("Appeal Status (0:Pending, 1:Approved, 2:Rejected): ");
                                    int appeal_option = scanner.nextInt();
                                    if (appeal_option == 0) appeal_status = "Pending";
                                    else if (appeal_option == 1) appeal_status = "Approved";
                                    else if (appeal_option == 2) appeal_status = "Rejected";
                                    else {
                                        System.out.println("Invalid option!\n");
                                        return;
                                    }
                                }
                            }

                            // Update the Citation table
                            try {
                                connection.setAutoCommit(false);
                                String updateCitationQuery = "UPDATE Citation SET citation_date = ?, citation_time = ?, category = ?, fee = ?, payment_status = ? WHERE citation_number = ?";
                                String updateLotQuery = "UPDATE Shows SET lot_name = ? WHERE citation_number = ?";
                                String updateLicenseQuery = "UPDATE GivenTo SET car_license_number = ? WHERE citation_number = ?";
                                String updateAppealsQuery = "UPDATE Appeals SET appeal_status = ? WHERE citation_number = ?";
                                try (PreparedStatement preparedStatementCitation = connection.prepareStatement(updateCitationQuery)) {
                                    preparedStatementCitation.setDate(1, citation_date);
                                    preparedStatementCitation.setTime(2, citation_time);
                                    preparedStatementCitation.setString(3, category);
                                    preparedStatementCitation.setFloat(4, fee);
                                    preparedStatementCitation.setBoolean(5, payment_status);
                                    preparedStatementCitation.setInt(6, citation_number);

                                    preparedStatementCitation.executeUpdate();
                                } catch (Exception e) {
                                    connection.rollback();
                                    System.out.println("Error Occurred while updating citation data " + e.getMessage());
                                    return;
                                }  
                                
                                try (PreparedStatement preparedStatementLot = connection.prepareStatement(updateLotQuery)) {
                                    preparedStatementLot.setString(1, lot_name);
                                    preparedStatementLot.setInt(2, citation_number);

                                    preparedStatementLot.executeUpdate();
                                } catch (Exception e) {
                                    connection.rollback();
                                    System.out.println("Error Occurred while updating citation data " + e.getMessage());
                                    return;
                                }  
                                
                                try (PreparedStatement preparedStatementLicense = connection.prepareStatement(updateLicenseQuery)) {
                                    preparedStatementLicense.setString(1, car_license_number);
                                    preparedStatementLicense.setInt(2, citation_number);

                                    preparedStatementLicense.executeUpdate();
                                } catch (Exception e) {
                                    connection.rollback();
                                    System.out.println("Error Occurred while updating citation data " + e.getMessage());
                                    return;
                                } 

                                if (appeal_status != null) {
                                    try (PreparedStatement preparedStatementAppeal = connection.prepareStatement(updateAppealsQuery)) {
                                    preparedStatementAppeal.setString(1, appeal_status);
                                    preparedStatementAppeal.setInt(2, citation_number);

                                    preparedStatementAppeal.executeUpdate();
                                    } catch (Exception e) {
                                        connection.rollback();
                                        System.out.println("Error Occurred while updating citation data " + e.getMessage());
                                        return;
                                    }
                                }
                                connection.commit();
                                System.out.println("Citation Updated Successfully");
                                DisplayGetCitation(connection, citation_number);
                            } catch (Exception e) {
                                System.out.println("Error Occurred while managing transaction: " + e.getMessage());
                            } finally {
                                try {
                                    connection.setAutoCommit(true);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }       
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while maintaining citation: " + e.getMessage());
        } finally {
            // scanner.close();
        }
    }

    public static void  DisplayGetCitation(Connection connection, int citation_number) throws Exception {
        String selectQuery = "SELECT C.*, S.lot_name, G.car_license_number, A.appeal_status " +
            "FROM Citation C " +
            "INNER JOIN Shows S ON C.citation_number = S.citation_number " +
            "INNER JOIN GivenTo G ON C.citation_number = G.citation_number " +
            "LEFT JOIN Appeals A ON C.citation_number = A.citation_number " +
            "WHERE C.citation_number = ?";
                
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, citation_number);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Date citation_date = resultSet.getDate("C.citation_date");
                    Time citation_time = resultSet.getTime("C.citation_time");
                    String category = resultSet.getString("C.category");
                    float fee = resultSet.getFloat("C.fee");
                    boolean payment_status = resultSet.getBoolean("C.payment_status");
                    String lot_name = resultSet.getString("S.lot_name");
                    String car_license_number = resultSet.getString("G.car_license_number");
                    String appeal_status = resultSet.getString("A.appeal_status");
                    System.out.println("\n\nDetails of Citation " + citation_number + ":\n");
                    System.out.println("Citation Date: " + citation_date);
                    System.out.println("Citation Time: " + citation_time);
                    System.out.println("Category: " + category);
                    System.out.println("Fee: " + fee);
                    System.out.println("Payment Status: " + payment_status);
                    System.out.println("Lot Name: " + lot_name);
                    System.out.println("Car License Number: " + car_license_number);
                    if (appeal_status != null) {
                        System.out.println("Appeal Status: " + appeal_status);
                    }
                } else {
                    System.out.println("No records found for citation number: " + citation_number);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function to pay a citation
    public static void payCitation(Connection connection, Scanner scanner) throws Exception {
        // Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Citation Number: ");
            int citation_number = scanner.nextInt();

            // Check if already paid
            String selectQuery = "SELECT * FROM Citation WHERE citation_number = ? AND payment_status = true";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, citation_number);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()){
                        System.out.println("Citation already paid!");
                        return;
                    }
                }
            }

            // Run the query to update the payment status
            String updateQuery = "UPDATE Citation SET payment_status = true WHERE citation_number = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, citation_number);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Payment status updated successfully for Citation Number: " + citation_number);
                } else {
                    System.out.println("No records found for Citation Number: " + citation_number);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while updating payment status: " + e.getMessage());
        } finally {
            // scanner.close();
        }
    }

    // Function to appeal a citation
    public static void appealCitation(Connection connection, Scanner scanner) throws Exception {
        // Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Citation Number: ");
            int citation_number = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter Driver's Phone: ");
            String phone = scanner.nextLine().trim();
            if (!phone.matches("\\d+")){
                System.out.println("Invalid phone number");
                return;
            }

            // Check if citation number belongs to phone
            String selectQuery = "SELECT * " +
                            "FROM GivenTo g " +
                            "JOIN IsAssigned ia ON g.car_license_number = ia.car_license_number " +
                            "WHERE g.citation_number = ? AND ia.phone = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, citation_number);
                preparedStatement.setString(2, phone);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()){
                        System.out.println("Phone " + phone + " cannot appeal for citation " + citation_number);
                        return;
                    }
                }
            }

            // Check if a record already exists in Appeals, and if so, don't do anything as this is just to create new appeals
            selectQuery = "SELECT * FROM Appeals WHERE citation_number = ? AND phone = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, citation_number);
                preparedStatement.setString(2, phone);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()){
                        System.out.println("An appeal record already exists for Citation Number: " + citation_number +
                                   " and Phone: " + phone);
                        return;
                    }
                }
            }

            // Run the query to insert a record into Appeals
            String insertQuery = "INSERT INTO Appeals (phone, citation_number, appeal_status) VALUES (?, ?, 'Pending')";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, phone);
                preparedStatement.setInt(2, citation_number);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Citation successfully appealed.");
                } else {
                    System.out.println("Failed to insert appeal record.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while processing appeal: " + e.getMessage());
        } finally {
            // scanner.close();
        }
    }

    public static void deleteCitation(Connection connection, Scanner scanner) {
        try {
            // Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Citation Number: ");
            int citation_number = scanner.nextInt();
            scanner.nextLine();
            // scanner.close();
            connection.setAutoCommit(false);
    
            // Delete from Appeals
            String deleteAppealsQuery = "DELETE FROM Appeals WHERE citation_number = ?";
            try (PreparedStatement deleteAppealsStatement = connection.prepareStatement(deleteAppealsQuery)) {
                deleteAppealsStatement.setInt(1, citation_number);
                deleteAppealsStatement.executeUpdate();
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Error Occurred while deleting citation " + e.getMessage());
                return;
            }  
    
            // Delete from GivenTo
            String deleteGivenToQuery = "DELETE FROM GivenTo WHERE citation_number = ?";
            try (PreparedStatement deleteGivenToStatement = connection.prepareStatement(deleteGivenToQuery)) {
                deleteGivenToStatement.setInt(1, citation_number);
                deleteGivenToStatement.executeUpdate();
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Error Occurred while deleting citation " + e.getMessage());
                return;
            }  
    
            // Delete from Shows
            String deleteShowsQuery = "DELETE FROM Shows WHERE citation_number = ?";
            try (PreparedStatement deleteShowsStatement = connection.prepareStatement(deleteShowsQuery)) {
                deleteShowsStatement.setInt(1, citation_number);
                deleteShowsStatement.executeUpdate();
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Error Occurred while deleting citation " + e.getMessage());
                return;
            }  
    
            // Delete from Citation
            String deleteCitationQuery = "DELETE FROM Citation WHERE citation_number = ?";
            try (PreparedStatement deleteCitationStatement = connection.prepareStatement(deleteCitationQuery)) {
                deleteCitationStatement.setInt(1, citation_number);
                deleteCitationStatement.executeUpdate();
            } catch (Exception e) {
                connection.rollback();
                System.out.println("Error Occurred while deleting citation " + e.getMessage());
                return;
            }  
    
            connection.commit(); // Commit the transaction
            System.out.println("Citation and related records deleted successfully.");
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            System.out.println("Error occurred while deleting citation: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }       
    }
    
}
