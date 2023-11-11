package src;
import java.sql.*;
import java.util.Scanner;

public class infoProcessing {

    public void enterDriverInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.print("\nEnter name: ");
        String name = sc.nextLine();
        System.out.print("\nEnter status E, V, or S: ");
        String status = sc.nextLine();
        System.out.print("\nEnter university id: ");
        String id = sc.nextLine();
        sc.close();
        enterDriverInfoHelper(statement, phoneNumber, name,status, id);       
    }

    public void enterDriverInfoHelper(Statement statement, String phoneNumber, String name, String status, String id){
        String query = String.format("INSERT INTO Driver (phone, name, status, univ_id) VALUES (\'%s\', \'%s\', \'%s\', \'%s\');", phoneNumber, name, status, id);
        try{
            statement.executeUpdate(query);
            System.out.println("Driver Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
    }

    /*
     * Identify with phone or univ_id 
     */
    public void updateDriverInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.nextLine();
        StringBuilder sb = new StringBuilder();
        System.out.print("\nUpdate status? (y/n): ");
        if(sc.next() == "y"){
            System.out.print("\nEnter status : ");
            String status = sc.next();
            sb.append(String.format("status = \'%s\'", status));
        }
        System.out.print("\nUpdate name? (y/n): ");
        if(sc.next() == "y"){
            System.out.print("\nEnter name : ");
            String status = sc.next();
            if(sb.length() != 0){
                sb.append(" , ");
            }
            sb.append(String.format("name = \'%s\'", status));
        }
        sc.close();
        updateDriverInfoHelper(statement, phoneNumber, sb.toString());
    }

    public void updateDriverInfoHelper(Statement statement, String phone, String update){
        try{
            statement.executeUpdate(String.format("UPDATE Driver SET %s WHERE phone = \'%s\';", update, phone));
            System.out.println("Driver Updated");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
    }

    public void deleteDriverInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        int option = -1;
        String phoneNumber = null;
        String id = null;
        do{
            System.out.print("Delete Driver by (1) phone or (2) university id");
            option = sc.nextInt();
            if (option == 1){
                System.out.print("\nEnter phone number: ");
                phoneNumber = sc.nextLine();
                deleteDriverInfoHelper(statement, id, phoneNumber); 
                System.out.println("Driver Deleted");

            }
            if (option == 2){
                System.out.print("\nEnter id: ");
                id = sc.nextLine();
                deleteDriverInfoHelper(statement, id, phoneNumber); 
               
            }
            
        } while (option != 1 && option != 2);
        sc.close();
    }

    public int deleteDriverInfoHelper(Statement statement, String id, String phone){
        try{
            int rowsAffected = 0;
            if(phone == null){
                rowsAffected = statement.executeUpdate(String.format("DELETE FROM Driver WHERE univ_id = \'%s\' AND (status = \'S\' OR status = \'E\');", id));
            } else {
                rowsAffected = statement.executeUpdate(String.format("DELETE FROM Driver WHERE phone = \'%s\';", phone));
            }
            if (rowsAffected > 0){
                System.out.println("Driver Deleted");
            } else if (rowsAffected == 0){
                System.out.println("Driver did not exist prior to delete");

            }
            return rowsAffected;
           
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
        return 0;

    }

    public void enterParkingLotInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter lot name: ");
        String lot_name = sc.nextLine();
        System.out.print("\nEnter address: ");
        String address = sc.nextLine();
        sc.close();
        enterParkingLotInfoHelper(statement, address, lot_name);
       
    }

    public void enterParkingLotInfoHelper(Statement statement, String lot_name, String address){
        String query = String.format("INSERT INTO ParkingLot (lot_name, address) VALUES(\'%s\', \'%s\');", lot_name, address);
        try{
            statement.executeUpdate(query);
            System.out.println("Parking Lot Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
    }

    public void updateParkingLotInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        String lot_name = null;
        String address = null;
        System.out.print("\nEnter lot name: ");
        lot_name = sc.nextLine();
        System.out.print("\nUpdate address: ");
        address = sc.nextLine();
        sc.close();
        updateParkingLotInfoHelper(statement, lot_name, address);
       
    }

    public void updateParkingLotInfoHelper(Statement statement, String lot_name, String address){
        try{
            statement.executeUpdate(String.format("UPDATE ParkingLot SET address = \'%s\' WHERE lot_name = \'%s\';",address, lot_name));
           
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
  
    }

    public void deleteParkingLotInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter lot name: ");
        String lot_name = sc.nextLine();
        sc.close();
        deleteParkingLotInfoHelper(statement, lot_name);
    }

    public int deleteParkingLotInfoHelper(Statement statement, String lot_name){
        int rowsAffected = -1;
        try{
            rowsAffected = statement.executeUpdate(String.format("DELETE FROM ParkingLot WHERE lot_name = \'%s\';", lot_name));
            if (rowsAffected > 0){
                System.out.println("Parking Lot Deleted");

            } else {
                System.out.println("Parking Lot did not exist prior to delete operation");
            }
            return rowsAffected;
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
        return 0;
    }

    /*
     * Does the following:
     * Enter zone info
     * Assign zones to each parking lot
     */
    public void enterZoneInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        sc.close();
        enterZoneInfoHelper(statement, zone_id, lot_name);
    }

    public void enterZoneInfoHelper(Statement statement, String zone_id, String lot_name){
        String query = String.format("INSERT INTO Zone (zone_id, lot_name) VALUES(\'%s\', \'%s\');", zone_id, lot_name);
        try{
            statement.executeUpdate(query);
            System.out.println("Zone Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
    }

    public void updateZoneInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        System.out.print("\nUpdate zone id (1) or update lot name (2)");
        int option = sc.nextInt();
        if (option == 1){
            System.out.print("\nEnter updated zone id: ");
            String new_zone_id = sc.nextLine(); 
            updateZoneInfoHelper(statement, zone_id, lot_name, new_zone_id, null);
        } else if (option == 2){
            System.out.print("\nEnter updated lot name: ");
            String new_lot_name = sc.nextLine();
            updateZoneInfoHelper(statement, zone_id, lot_name, null, new_lot_name);

        }
        sc.close();
       
    }

    public void updateZoneInfoHelper(Statement statement, String zone_id, String lot_name, String new_zone_id, String new_lot_name){
        String query = null;
        if (new_zone_id == null){
            query = String.format("Update Zone SET lot_name = \'%s\' WHERE zone_id = \'%s\' AND lot_name = \'%s\';",new_lot_name, zone_id, lot_name);

        } else if(new_lot_name == null){
            query = String.format("Update Zone SET zone_id = \'%s\' WHERE zone_id = \'%s\' AND lot_name = \'%s\';",new_zone_id, zone_id, lot_name);

        }
        if (query != null){
            try{
                statement.executeUpdate(query);
                System.out.println("Zone Updated");
            } catch (Throwable oops) {
                oops.printStackTrace();
            }
        } else {
            System.out.println("Invalid Option Selected");

        }
       
    }

    public void deleteZoneInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        sc.close();
        deleteZoneInfoHelper(statement, zone_id, lot_name);
    }

    public int deleteZoneInfoHelper(Statement statement, String zone_id, String lot_name){
        int rowsAffected = -1;
        try{
            rowsAffected = statement.executeUpdate(String.format("DELETE FROM Zone WHERE zone_id = \'%s\' AND lot_name = \'%s\';",zone_id, lot_name));
            if(rowsAffected > 0){
                System.out.println("Zone Deleted");

            } else {
                System.out.println("Zone did not exist prior to deletion");

            }
            return rowsAffected;
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
        return 0;
    }

    /*
     * TODO possible changes to space table
     */
    public void enterSpaceInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter space number: ");
        int space_number = sc.nextInt();
        System.out.print("\nEnter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        System.out.print("\nEnter space type: ");
        String space_type = sc.nextLine();
        sc.close();
        enterSpaceInfoHelper(statement, space_number, zone_id, lot_name, space_type);
     
    }

    public void enterSpaceInfoHelper(Statement statement, int space_number, String zone_id, String lot_name, String space_type){
        try{
            statement.executeUpdate(String.format("INSERT INTO Space (space_number, lot_name, zone_id, space_type, avilability_status) VALUES(%d, \'%s\', \'%s\', \'%s\', 1);", space_number, lot_name, zone_id, space_type));
            System.out.println("Space Added");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public void updateSpaceInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter space number: ");
        int space_number = sc.nextInt();
        System.out.print("\nEnter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        System.out.print("\nUpdate space type: ");
        String space_type = sc.nextLine();
        System.out.print("\nUpdate availability status (0 or 1): ");
        int availability_status = sc.nextInt();
        sc.close();
        updateSpaceInfoHelper(statement, zone_id, lot_name, space_number, space_type, availability_status);
    }

    public void updateSpaceInfoHelper(Statement statement, String zone_id, String lot_name, int space_number, String space_type, int availability_status){
        String query = "UPDATE Space SET availability_status = %d, space_type = \'%s\' " +
        "WHERE zone_id = \'s\' AND lot_name = \'s\' AND space_number = %d;";
        try{
            statement.executeUpdate(String.format(query, availability_status, space_type, zone_id, lot_name, space_number));
            System.out.println("Space Added");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
        
    }

    public void deleteSpaceInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter space number: ");
        int space_number = sc.nextInt();
        System.out.print("\nEnter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        sc.close();
        deleteSpaceInfoHelper(statement, space_number, zone_id, lot_name);
    }

    public int deleteSpaceInfoHelper(Statement statement, int space_number, String zone_id, String lot_name){
        int rowsAffected = 0;
        try{
            rowsAffected = statement.executeUpdate(String.format("DELETE FROM Space WHERE space_number = %d AND zone_id = \'%s\' AND lot_name = \'%s\';",space_number, zone_id, lot_name));
            if(rowsAffected > 0){
                System.out.println("Space Deleted");
            } else {
                System.out.println("Space did not exist prior to deletion");

            }
            return rowsAffected;
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
        return 0;
    }

    /*
     * TODO possible other way to enter date
     */
    public void enterPermitInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter permit id: ");
        int permit_id = sc.nextInt();
        System.out.print("\nEnter space type: ");
        String space_type = sc.nextLine();
        System.out.print("\nEnter permit type: ");
        String permit_type = sc.nextLine();
        System.out.print("\nEnter Start Date in format YYYY-MM-DD: ");
        String start_date = sc.nextLine();
        System.out.print("\nEnter Expiration Date in format YYYY-MM-DD: ");
        String expiration_date = sc.nextLine();
        System.out.print("\nEnter Expiration Time in format HH:MM:SS: ");
        String expiration_time = sc.nextLine();

        sc.close();
        enterPermitInfoHelper(statement, permit_id, space_type, permit_type, start_date, expiration_date, expiration_time);
  
    }

    public void enterPermitInfoHelper(Statement statement, int permit_id, String space_type, String permit_type, String start_date, String expiration_date, String expiration_time){
      try{
            String query = "INSERT INTO Permit (permit_id, space_type, permit_type, start_date, expiration_date, expiration_time)" +
            "VALUES (%d,\'%s\', \'%s\', \'%s\', \'%s\', \'%s\');";
            
            statement.executeUpdate(String.format(query,permit_id, space_type, permit_type, start_date, expiration_date, expiration_time));
            System.out.println("Permit Added");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public void updatePermitInfo(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter permit id: ");
        int permit_id = sc.nextInt();
        StringBuilder sb = new StringBuilder();
        System.out.print("\nUpdate space type (-1 to not update): ");
        String space_type = sc.nextLine();
        if (!space_type.equals("-1")){
            sb.append("space_type = \'" + space_type + "\'");
        } 
        System.out.print("\nUpdate permit type (-1 to not update): ");
        String permit_type = sc.nextLine();
        if (!permit_type.equals("-1")){
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append("permit_type = \'" + permit_type + "\'");
        } 
        System.out.print("\nUpdate Start Date in format YYYY-MM-DD (-1 to not update): ");
        String start_date = sc.nextLine();
        if (!start_date.equals("-1")){
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append("start_date = \'" + start_date + "\'");
        }
        System.out.print("\nUpdate Expiration Date in format YYYY-MM-DD (-1 to not update): ");
        String expiration_date = sc.nextLine();
        if (!expiration_date.equals("-1")){
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append("expiration_date = \'" + expiration_date + "\'");
        }
        System.out.print("\nUpdate Expiration Time in format HH:MM:SS (-1 to not update): ");
        String expiration_time = sc.nextLine();
         if (!expiration_time.equals("-1")){
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append("expiration_time = \'" + expiration_time + "\'");
        }
        sc.close();
        updatePermitInfoHelper(null, permit_id, expiration_time);

    }

    public void updatePermitInfoHelper(Statement statement, int permit_id, String updates){
        String query = "UPDATE Permit SET "+ updates + " WHERE permit_id = %d;";
        try{
            statement.executeUpdate(String.format(query,permit_id));
            System.out.println("Permit Updated");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public void deletePermitInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter permit id: ");
        int permit_id = sc.nextInt();
        sc.close();
        deletePermitInfoHelper(statement, permit_id);
    }

    public int deletePermitInfoHelper(Statement statement, int permit_id){
        int rowsDeleted = -1;
        try{
            rowsDeleted = statement.executeUpdate(String.format("DELETE from Permit WHERE permit_id = %d;",permit_id));
            if (rowsDeleted > 0){
                System.out.println("Permit Deleted");

            } else {
                System.out.println("Permit did not exist prior to deletion");

            }
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
        return 0;
    }

    public void assignTypeToSpace(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter space number: ");
        int space_number = sc.nextInt();
        System.out.print("\nEnter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        System.out.print("\nEnter space type: ");
        String space_type = sc.nextLine();
        sc.close();
        assignTypeToSpaceHelper(statement, space_type, space_number, lot_name, zone_id);
        
    }

    public void assignTypeToSpaceHelper(Statement statement, String space_type, int space_number, String lot_name, String zone_id){
        try{
            statement.executeUpdate(String.format("UPDATE Space SET space_type = \'%s\' WHERE space_number = %d AND lot_name = \'%s\' AND zone_id = \'%s\';", space_type, space_number, lot_name, zone_id));
            System.out.println("Space Type Assigned");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public void requestCitationAppeal(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter phone: ");
        String phone = sc.nextLine();
        System.out.print("\nEnter citation number: ");
        int citation_number = sc.nextInt();
        sc.close();
        requestCitationAppealHelper(statement, phone, citation_number);
    }

    public void requestCitationAppealHelper(Statement statement, String phone, int citation_number){
        try{
            statement.executeUpdate(String.format("INSERT INTO Appeals VALUES (\'%s\', %d, 'Pending');", phone, citation_number));
            System.out.println("Citation Appealed");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public void updateCitationPayment(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter citation number: ");
        int citation_number = sc.nextInt();
        sc.close();
        updateCitationPaymentHelper(statement, citation_number);
    }

    public void updateCitationPaymentHelper(Statement statement, int citation_number){
        try{
            statement.executeUpdate(String.format("UPDATE Citation set payment_status = 1 WHERE citation_number = %d;", citation_number));
            System.out.println("Citation Payment Successful");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

}