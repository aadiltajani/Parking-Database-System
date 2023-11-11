import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class Citations {
    // Function to detect violations
    public static void detectParkingViolations(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Lot Name: ");
            String lotName = scanner.nextLine().trim();

            System.out.print("Enter Zone ID: ");
            String zoneId = scanner.nextLine().trim();

            System.out.print("Enter Space Type: ");
            String spaceType = scanner.nextLine().trim();

            System.out.print("Enter Car License Number: ");
            String carLicenseNumber = scanner.nextLine().trim();

            System.out.print("Enter Expiration Date(YYYY-MM-DD): ");
            String expirationDate = scanner.nextLine().trim();

            System.out.print("Enter Expiration Time(HH:MM:SS): ");
            String expirationTime = scanner.nextLine().trim();

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
                preparedStatement.setDate(5, java.sql.Date.valueOf(expirationDate));
                preparedStatement.setTime(6, java.sql.Time.valueOf(expirationTime));
                preparedStatement.setDate(7, java.sql.Date.valueOf(expirationDate));

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
            scanner.close();
        }
    }
    // Function to generate a citation
    public static void generateCitation(Connection connection) throws Exception {
        try{
            String insertCitationQuery = "INSERT INTO Citation VALUES (?, ?, ?, ?, ?, ?)";
            String insertShowsQuery = "INSERT INTO Shows VALUES (?, ?)";
            String insertGivenToQuery = "INSERT INTO GivenTo VALUES (?, ?)";
            String insertVehicleQuery = "INSERT INTO Vehicle VALUES (?, ?, ?, ?, ?)";
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter citation details:");

            System.out.print("Citation Number: ");
            int citation_number = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Category(Invalid Permit, Expired Permit, No Permit): ");
            String category = scanner.nextLine().trim();
            List<String> permitCategories = List.of("Expired Permit", "Invalid Permit", "No Permit");
            if (!permitCategories.contains(category)){
                scanner.close();
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
            

            scanner.close();
            // Execute insert queries in transaction
            try{
                connection.setAutoCommit(false);
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertCitationQuery)) {
                    preparedStatement.setInt(1, citation_number);
                    preparedStatement.setDate(2, citation_date);
                    preparedStatement.setTime(3, citation_time);
                    preparedStatement.setString(4, category);
                    preparedStatement.setFloat(5, fee);
                    preparedStatement.setBoolean(6, payment_status);

                    preparedStatement.executeUpdate();
                    System.out.println("Citation generated successfully.");
                } catch (Exception e) {
                    connection.rollback();
                    System.out.println("Error Occurred while inserting citation data " + e.getMessage());
                    return;
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertShowsQuery)) {
                    preparedStatement.setInt(1, citation_number);
                    preparedStatement.setString(2, lot_name);
                    preparedStatement.executeUpdate();
                    System.out.println("Citation Lot Assigned successfully.");
                } catch (Exception e) {
                    connection.rollback();
                    System.out.println("Error Occurred while inserting lot data " + e.getMessage());
                    return;
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertGivenToQuery)) {
                    preparedStatement.setInt(1, citation_number);
                    preparedStatement.setString(2, car_license_number);
                    preparedStatement.executeUpdate();
                    System.out.println("Citation assigned to vehicle successfully.");
                } catch (Exception e) {
                    connection.rollback();
                    System.out.println("Error Occurred while inserting lot data " + e.getMessage());
                    return;
                }
                if (insertVehicle && !model.equals(null) && !color.equals(null)){
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertVehicleQuery)) {
                        preparedStatement.setString(1, car_license_number);
                        preparedStatement.setString(2, model);
                        preparedStatement.setInt(3, 0000);
                        preparedStatement.setString(4, color);
                        preparedStatement.setString(5, "N/A");
                        preparedStatement.executeUpdate();
                        System.out.println("Vehicle data added successfully.");
                    } catch (Exception e) {
                        connection.rollback();
                        System.out.println("Error Occurred while inserting vehicle data " + e.getMessage());
                        return;
                    }   
                }
                connection.commit();
            } catch (SQLException e) {
                System.out.println("Error Occurred while managing transaction: " + e.getMessage());
            } finally {
                try {
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
    public static void maintainCitation(Connection connection, int citationNumber, String newCategory, String newCitationTime) throws SQLException {
        String updateCategoryQuery = "UPDATE Citation SET category = ? WHERE citation_number = ?";
        String updateCitationTimeQuery = "UPDATE Citation SET citation_time = ? WHERE citation_number = ?";

        try (PreparedStatement updateCategoryStatement = connection.prepareStatement(updateCategoryQuery);
             PreparedStatement updateCitationTimeStatement = connection.prepareStatement(updateCitationTimeQuery)) {

            // Update category
            updateCategoryStatement.setString(1, newCategory);
            updateCategoryStatement.setInt(2, citationNumber);
            updateCategoryStatement.executeUpdate();

            // Update citation time
            updateCitationTimeStatement.setString(1, newCitationTime);
            updateCitationTimeStatement.setInt(2, citationNumber);
            updateCitationTimeStatement.executeUpdate();

            System.out.println("Citation updated successfully.");
        }
    }

    // Function to pay a citation
    public static void payCitation(Connection connection) throws Exception {
        Scanner scanner = new Scanner(System.in);

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
            scanner.close();
        }
    }

    // Function to appeal a citation
    public static void appealCitation(Connection connection) throws Exception {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Citation Number: ");
            int citation_number = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter Driver's Phone: ");
            String phone = scanner.nextLine().trim();

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
            scanner.close();
        }
    }
}
