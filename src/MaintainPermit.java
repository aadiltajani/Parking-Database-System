package src;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class MaintainPermit {
	 public static void addPermit(Connection connection, Scanner scanner) throws Exception{
		 try { 
				 String insertPermitQuery = "INSERT INTO Permit VALUES (?, ?, ?, ?, ?, ?)";
				 String insertIsAssignedQuery = "INSERT INTO IsAssigned VALUES (?,?,?)";
				 String insertHasZoneQuery = "INSERT INTO HasZone VALUES (?,?,?)";
				 String insertVehicleQuery = "INSERT INTO Vehicle VALUES (?, ?, ?, ?, ?)";
				 
				 System.out.println("Enter permit details:");
				 System.out.print("Permit ID: ");
		         int permit_id = scanner.nextInt();
				 scanner.nextLine();
		        
	         
		         System.out.print("Space Type (Electric, Handicap, Compact Car, Regular): ");
		         String space_type = scanner.nextLine().trim();
		         List<String> spaceCategories = List.of("Electric", "Handicap", "Compact Car", "Regular");
		         if (!spaceCategories.contains(space_type)){
		                // scanner.close();
		              System.out.println("Invalid Category");
		              return;
		          }
		         
		         System.out.print("Start Date (YYYY-MM-DD): ");
		         String start_date_str = scanner.nextLine().trim();
		         java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(start_date_str);
		         Date start_date = new Date(utilDate.getTime());
		         
		         System.out.print("Expiration Date (YYYY-MM-DD): ");
		         String expiration_date_str = scanner.nextLine().trim();
		         java.util.Date utilDate2 = new SimpleDateFormat("yyyy-MM-dd").parse(expiration_date_str);
		         Date expiration_date = new Date(utilDate2.getTime());
		         
		         System.out.print("Expiration Time (HH:mm:ss): ");
		         String permit_time_str = scanner.nextLine().trim();
		         Time expiration_time = Time.valueOf(permit_time_str);
		         
		         System.out.print("Permit Type (Residential, Commuter, Special Event, Peak Hours, Park & Ride): ");
		         String permit_type = scanner.nextLine().trim();
		         List<String> permitCategories = List.of("Residential", "Commuter", "Special Event", "Peak Hours", "Park & Ride");
		         if (!permitCategories.contains(permit_type)){
		              System.out.println("Invalid Category");
		              return;
		          }
		         
		         System.out.print("Lot Name: ");
		         String lot_name = scanner.nextLine().trim();
		         
		         System.out.print("Zone ID: ");
		         String zone_id = scanner.nextLine().trim();
		         
		         System.out.print("Car License Number: ");
		         String car_license_number = scanner.nextLine().trim();
		         boolean insertVehicle = false;
		         String model= null;
		         String color= null;
		         String manufacturer =null;
		         int year= -1;
                 String vehicle_query = "SELECT v.car_license_number " +
                         "FROM Vehicle v " +
                         "WHERE v.car_license_number = ?";
                     try (PreparedStatement vehiclePreparedStatement = connection.prepareStatement(vehicle_query)) {
                         vehiclePreparedStatement.setString(1, car_license_number);
                         try (ResultSet vehicleResultSet = vehiclePreparedStatement.executeQuery()) {
                             if (!vehicleResultSet.next()) {
                                 // Car license number not found, take vehicle info to add it in DB
                                 System.out.println("Enter vehicle details: ");
                                 System.out.print("Model: ");
                                 model = scanner.nextLine().trim();
                                 System.out.print("Year: ");
                                 year = scanner.nextInt();
								 scanner.nextLine();
                                 System.out.print("Color: ");
                                 color = scanner.nextLine().trim();
                                 System.out.print("Manufacturer: ");
                                 manufacturer = scanner.nextLine().trim();
                                 insertVehicle = true;
                             }
                         }
                     }
		         insertVehicle = false;
				 boolean secondvehicle = false;
		         System.out.print("Driver Phone: ");
		         String phone = scanner.nextLine().trim();
		         
		         boolean flag= false;
		         String checkDriverStatusQuery = "SELECT status FROM Driver WHERE phone = ?";
		         try (PreparedStatement statusStatement = connection.prepareStatement(checkDriverStatusQuery)) {
		        	 statusStatement.setString(1, phone);
		             ResultSet statusResult = statusStatement.executeQuery();
		             
		             if (statusResult.next()) { 
		            	 String driverStatus = statusResult.getString("status");
		            	 String permitcountquery = "SELECT COUNT(DISTINCT permit_id) AS unique_permit_count FROM IsAssigned WHERE phone = ?";
						 String vehiclesonpermitquery = "SELECT COUNT(car_license_number) AS permit_car_count FROM IsAssigned WHERE permit_id = ?";
		            	 try (PreparedStatement permitStatement = connection.prepareStatement(permitcountquery)) {
		            		 permitStatement.setString(1, phone);
		                     ResultSet permitResult = permitStatement.executeQuery();
		                     int existingPermits = 0;
							 if (permitResult.next()){
								existingPermits = permitResult.getInt("unique_permit_count");
							 }
		                    int vehicles_on_permit = 0;
							try (PreparedStatement vehiclesonpermitstatement = connection.prepareStatement(vehiclesonpermitquery)) {
								vehiclesonpermitstatement.setInt(1, permit_id);
								ResultSet vehiclepermitResult = vehiclesonpermitstatement.executeQuery();
								if(vehiclepermitResult.next()){
									vehicles_on_permit = vehiclepermitResult.getInt("permit_car_count");
								}
							}

							

		                     if(driverStatus.equals("V") && !zone_id.equals("V")) { 
		                    	 System.out.println("Invalid zone for Visitor Permit. Can assign V only");
		                    	 return;
		                     }if (driverStatus.equals("S") && !zone_id.endsWith("S") && zone_id.length() != 2) {
		                    	 System.out.println("Invalid zone for Student Permit. Can zone ending in S only");
		                    	 return;
		                     }if (driverStatus.equals("E") && (zone_id.endsWith("S") || zone_id.equals("V")) && zone_id.length() != 1 ) {
		                    	 System.out.println("Invalid zone for Employee Permit. Can assign single letter zone only");
		                    	 return;
		                     }
		                     
		                     switch (driverStatus) { 
			                     case "V":
			                         // Driver status is V
			                         if (existingPermits == 0) {
			                             // No existing permits, assign permit
			                             flag=true;
			                         } else {
			                             System.out.println("Visitor already has a permit. Cannot assign another permit.");
			                             return;
			                         }
			                         break;
			                         
			                      case "S":
			                         // Driver status is S
			                    	  if (existingPermits == 0) {
				                             // No existing permits, assign permit
				                             flag=true;
				                             break;
				                      }
				                      if (existingPermits == 1) {
				                    	 if(vehicles_on_permit > 0){
											System.out.println("Limit Exhausted!");
											return;
										 }
				                    	  
				                             // Check permit type from Permit table
				                             if (permit_type.equals("Special Event") || permit_type.equals("Park & Ride")) {
				                                 flag=true;
				                                 break;
				                             }else {
				                                 System.out.println("Invalid permit type for additional permit. Cannot assign permit.");
				                                 return;
				                             }

			                    	}
									
									if(existingPermits >=2) {
			                    		System.out.println("Student already has 2 permits. Cannot assign permit.");
			                    		return; }
			                         break;
			                         
			                      case "E":
			                          // Driver status is E
									  if (vehicles_on_permit == 1) secondvehicle = true;
			                    	  if (existingPermits == 0 || existingPermits == 1) {
										if(vehicles_on_permit > 1){
											System.out.println("Limit Exhausted!");
											return;
										 }
										// Within permit limits, assign permit
										flag=true;
										break;
				                       }
			                    	  
			                    	if (existingPermits == 2) {
										
			                    			// Check permit type from Permit table
			                    			if (permit_type.equals("Special Event") || permit_type.equals("Park & Ride")) {
												if(vehicles_on_permit > 1){
													System.out.println("Limit Exhausted!");
													return;
												}
				                                  flag= true;
				                                  break;
				                              } else {
				                                  System.out.println("Invalid permit type for additional permit. Cannot assign permit or car limit exhausted");
				                                  return;
				                              }
			                    		
				                    }
			                    	  
			                    	   if(existingPermits == 3) {
										if(vehicles_on_permit == 1){
											flag= true;
											break;
										} else {
			                              System.out.println("Maximum number of permits reached. Cannot assign another permit.");
			                              return;
										}
			                          }
			                    	  
			                          break; 
		                     }
		            	 }
		             } else {
						System.out.println("No driver records found");
						return;
					 }
	                    
	                } catch (Exception e) {
	                    connection.rollback();
	                    System.out.println("Error Occurred while assigning permit" + e.getMessage());
	                    return;
	                }
		         
		         try{
					if (flag == true) {
		                connection.setAutoCommit(false);
						if(!secondvehicle){
							try (PreparedStatement preparedStatement = connection.prepareStatement(insertPermitQuery)) {
								preparedStatement.setInt(1, permit_id);
								preparedStatement.setString(2, space_type);
								preparedStatement.setDate(3, start_date);
								preparedStatement.setDate(4, expiration_date);
								preparedStatement.setTime(5, expiration_time);
								preparedStatement.setString(6, permit_type);
								preparedStatement.executeUpdate();
							} catch (Exception e) {
								connection.rollback();
								System.out.println("Error Occurred while inserting permit data " + e.getMessage());
								return;
							}
							if (insertVehicle && !model.equals(null) && !color.equals(null) && !manufacturer.equals(null) && year!=-1){
								try (PreparedStatement preparedStatement = connection.prepareStatement(insertVehicleQuery)) {
									preparedStatement.setString(1, car_license_number);
									preparedStatement.setString(2, model);
									preparedStatement.setInt(3, year);
									preparedStatement.setString(4, color);
									preparedStatement.setString(5, manufacturer);
									preparedStatement.executeUpdate();
									System.out.println("Vehicle data added successfully.");
								} catch (Exception e) {
									connection.rollback();
									System.out.println("Error Occurred while inserting vehicle data " + e.getMessage());
									return;
								}   
							}
							
							try (PreparedStatement preparedStatement = connection.prepareStatement(insertHasZoneQuery)) {
								preparedStatement.setInt(1, permit_id);
								preparedStatement.setString(2, zone_id);
								preparedStatement.setString(3, lot_name);
								preparedStatement.executeUpdate();
								System.out.println("Added permit zone");
							} catch (Exception e) {
								connection.rollback();
								System.out.println("Error Occurred while inserting permit zone " + e.getMessage());
								return;
							}
						}
						try (PreparedStatement preparedStatement = connection.prepareStatement(insertIsAssignedQuery)) {
							preparedStatement.setString(1, phone);
							preparedStatement.setInt(2, permit_id);
							preparedStatement.setString(3, car_license_number);
							preparedStatement.executeUpdate();
							System.out.println("Permit Assigned to Driver successfully.");
						} catch (Exception e) {
							connection.rollback();
							System.out.println("Error Occurred while assigning permit " + e.getMessage());
							return;
						}
		                connection.commit();
		                System.out.println("Permit Added successfully.");
					}
				} catch (SQLException e) {
					connection.rollback();
					System.out.println("Error Occurred while managing transaction: " + e.getMessage());
				} finally {
					try {
						connection.setAutoCommit(true);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}		 
		 } catch(Exception e) {
			 System.out.println("Error Occurred while getting data " + e.getMessage());
		 }
	
}
		                  
     public static void addVehicle(Connection connection, Scanner scanner) throws Exception{
	 try {
		 String insertVehicleQuery = "INSERT INTO Vehicle VALUES (?, ?, ?, ?, ?)";
		 System.out.println("Enter vehicle details: ");
		 System.out.print("Car License Number: ");
         String car_license_number = scanner.nextLine().trim();
         System.out.print("Model: ");
         String model = scanner.nextLine().trim();
         System.out.print("Year: ");
         int year = scanner.nextInt();
         System.out.print("Color: ");
         String color = scanner.nextLine().trim();
         System.out.print("Manufacturer: ");
         String manufacturer = scanner.nextLine().trim();
         try (PreparedStatement preparedStatement = connection.prepareStatement(insertVehicleQuery)) {
         	preparedStatement.setString(1, car_license_number);
         	preparedStatement.setString(2, model);
            preparedStatement.setInt(3, year);
            preparedStatement.setString(4, color);
            preparedStatement.setString(5, manufacturer);
            preparedStatement.executeUpdate();
            System.out.println("Vehicle data added.");
         } catch (Exception e) {
             connection.rollback();
             System.out.println("Error Occurred while inserting vehicle " + e.getMessage());
             return;
         }	 
	 }
	 catch(Exception e) {
		 System.out.println("Error Occurred while getting data " + e.getMessage());
	 }
	 }
	 
     public static void updateVehicle(Connection connection, Scanner scanner) throws Exception {
    	 try { 
    		 System.out.print("Enter Car License Number: ");
    		 String car_license_number = scanner.nextLine().trim();
    		// Check if the vehicle exists
             String selectQuery = "SELECT * FROM Vehicle WHERE car_license_number = ? ";
             try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                 preparedStatement.setString(1, car_license_number);
                 try (ResultSet resultSet = preparedStatement.executeQuery()) {
                     if (!resultSet.next()) {
                         System.out.println("Vehicle with Car License Number " + car_license_number + " does not exist.");
                         return;
                     }
                 }
             }
    		 
             //display Vehicle information
             DisplayGetVehicle(connection,car_license_number);
             selectQuery = "Select * from Vehicle where car_license_number = ? ";
             try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                 preparedStatement.setString(1, car_license_number); 
                 try (ResultSet resultSet = preparedStatement.executeQuery()) { 
                	 if (resultSet.next()){ 
                		 String model = resultSet.getString("model");
                         String manufacturer = resultSet.getString("manufacturer");
                         int year= resultSet.getInt("year");
                         String color = resultSet.getString("color");
                         
                         System.out.print("\nDo you want to make changes to this Vehicle? (yes/no): ");
                         String userChoice = scanner.nextLine().trim().toLowerCase();
                         
                         if (userChoice.equals("yes")) { 
                        	 System.out.print("Do you want to change the Car Model? (yes/no): ");
                             if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                                 System.out.print("Model: ");
                                 model = scanner.nextLine().trim();
                             }
                             
                             System.out.print("Do you want to change the Car Color? (yes/no): ");
                             if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                                 System.out.print("Color: ");
                                 color = scanner.nextLine().trim();
                             }
                             
                             System.out.print("Do you want to change the Car Manufacturer? (yes/no): ");
                             if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                                 System.out.print("Manufacturer: ");
                                 manufacturer = scanner.nextLine().trim();
                             }
                             
                             System.out.print("Do you want to change the Car Year? (yes/no): ");
                             if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                                 System.out.print("Year: ");
                                 year = scanner.nextInt();
                             }
                             
                             try {
                            	 String updateVehicleQuery = "UPDATE Vehicle SET model = ?, year = ?, color = ?, manufacturer = ? WHERE car_license_number = ?";
                            	 try (PreparedStatement preparedStatementCitation = connection.prepareStatement(updateVehicleQuery)) {
                                     preparedStatementCitation.setString(1, model);
                                     preparedStatementCitation.setInt(2, year);
                                     preparedStatementCitation.setString(3, color);
                                     preparedStatementCitation.setString(4, manufacturer);
                                     preparedStatementCitation.setString(5, car_license_number);
                                     preparedStatementCitation.executeUpdate();
									 System.out.println("Vehicle information updated");
                                 } catch (Exception e) {
                                     connection.rollback();
                                     System.out.println("Error Occurred while updating vehicle data " + e.getMessage());
                                     return;
                                 }  
                             }catch (Exception e) {
                                 e.printStackTrace();
                             }
                         }
                	 }
                 }
              }
             
             
    	 }catch (Exception e) {
             System.out.println("Error occurred while updating Permit: " + e.getMessage());
         }
     }
     
     public static void  DisplayGetVehicle(Connection connection, String car_license_number) throws Exception {
         String selectQuery = "SELECT * from Vehicle where car_license_number = ?";
                 
         try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
             preparedStatement.setString(1, car_license_number);

             try (ResultSet resultSet = preparedStatement.executeQuery()) {
                 if (resultSet.next()) {
                     String model = resultSet.getString("model");
                     String manufacturer = resultSet.getString("manufacturer");
                     int year= resultSet.getInt("year");
                     String color = resultSet.getString("color");
                     
                     System.out.println("\n\nDetails of Vehicle " + car_license_number + ":\n");
                     System.out.println("Model: " + model);
                     System.out.println("Year: " + year);
                     System.out.println("Color: " + color);
                     System.out.println("Manufacturer: " + manufacturer);
                     
                 } else {
                     System.out.println("No records found for citation number: " + car_license_number);
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
     
     public static void updateVehicleOwnership(Connection connection, Scanner scanner) throws Exception {
    	 try {
    		System.out.print("Enter Car License Number: ");
    		String car_license_number = scanner.nextLine().trim();

				String deletePermitQuery = "DELETE FROM Permit WHERE permit_id IN (SELECT permit_id FROM IsAssigned WHERE car_license_number = ?)";
				try (PreparedStatement preparedStatement = connection.prepareStatement(deletePermitQuery)) {
					preparedStatement.setString(1, car_license_number);
					int rowsAffected = preparedStatement.executeUpdate();
					if (rowsAffected > 0) {
						System.out.println("Vehicle Ownership Removed Successfully. Please assign a new Permit to the vehicle with the intended new Owner.");
					} else {
						System.out.println("No records were found for the specified car license number.");
					}
				}
			} catch (Exception e) {
				System.out.println("Error Occurred while managing transaction: " + e.getMessage());
			}   
     }
	 
     public static void deleteVehicle(Connection connection, Scanner scanner) {
	        try {
	            System.out.print("Enter Car licence Number: ");
	            String car_license_number = scanner.nextLine().trim();
	            boolean isAssignedFlag= false;
	            
	            String checkIsAssignedQuery = "SELECT 1 FROM IsAssigned WHERE car_license_number = ?";
	            try (PreparedStatement checkIsAssignedStatement = connection.prepareStatement(checkIsAssignedQuery)) { 
	            	checkIsAssignedStatement.setString(1, car_license_number);
	                try (ResultSet resultSet = checkIsAssignedStatement.executeQuery()) { 
	                	if (resultSet.next()) { 
	                		isAssignedFlag= true;
	                     }
	                }
	             }
	            
	            connection.setAutoCommit(false);
	    
	            
	        
	            //if present in IsAssigned then delete from IsAssigned and also delete corresponding permits
	            if(isAssignedFlag) { 
	            String deleteIsAssignedQuery = "DELETE FROM IsAssigned WHERE car_license_number = ?";
                String deletePermitQuery = "DELETE FROM Permit WHERE permit_id IN (SELECT permit_id FROM IsAssigned WHERE car_license_number = ?)";
                try (PreparedStatement deleteIsAssignedStatement = connection.prepareStatement(deleteIsAssignedQuery);
                        PreparedStatement deletePermitStatement = connection.prepareStatement(deletePermitQuery)) { 
                	deleteIsAssignedStatement.setString(1, car_license_number);
                    deletePermitStatement.setString(1, car_license_number);

                    deleteIsAssignedStatement.executeUpdate();
                    deletePermitStatement.executeUpdate(); }
	            }
	            
	           	// Delete from Vehicle
	            String deleteVehicleQuery = "DELETE FROM Vehicle WHERE car_license_number = ?";
	            try (PreparedStatement deleteVehicleStatement = connection.prepareStatement(deleteVehicleQuery)) {
	                deleteVehicleStatement.setString(1, car_license_number);
	                deleteVehicleStatement.executeUpdate();
	            }
	            connection.commit(); // Commit the transaction
	            System.out.println("Vehicle and related records deleted successfully.");
	        } catch (SQLException e) {
	            try {
	                connection.rollback(); // Rollback the transaction in case of an error
	            } catch (SQLException rollbackException) {
	                rollbackException.printStackTrace();
	            }
	            System.out.println("Error occurred while deleting vehicle data: " + e.getMessage());
	        } finally {
	            try {
	                connection.setAutoCommit(true);
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }       
	    }
	 
	 
	 public static void getVehicleData(Connection connection, Scanner scanner) throws Exception{
		 //add queries to get all permits associated with that license_number
		 //add queries to get all citations associated with that license_number
	 }
}
