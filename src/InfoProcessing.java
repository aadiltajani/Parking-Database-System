package src;
import java.sql.*;
import java.util.Scanner;
import java.util.regex.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Responsible for all tasks related to info processing
 */
public class InfoProcessing {

    /*
     * Function for entering driver info
     */
    public void enterDriverInfo(Statement statement, Scanner sc){
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.nextLine().trim();
         if(!isPhone(phoneNumber)){
            System.out.println("Phone number must be all digits");
            return;
        }
        System.out.print("\nEnter name: ");
        String name = sc.nextLine().trim();
        System.out.print("\nEnter status E, V, or S: ");
        String status = sc.nextLine().trim();
        if(!validStatus(status)){
            System.out.println("Status must have values 'E', 'S' or 'V'");
            return;
        }
        String id = null;
        if(!"V".equals(status)){
            System.out.print("\nEnter university id: ");
            id = sc.nextLine().trim();
        }

        if("".equals(id)){
            id = null;
        }
        enterDriverInfoHelper(statement, phoneNumber, name,status, id);       
    }

    /*
     * Checks that the string is all digits
     */
    private boolean isPhone(String phone){
        // regex for all digits
        String regex = "[0-9]+"; 
 
        Pattern p = Pattern.compile(regex); 
 
       
        if (phone == null) { 
            return false; 
        } 
        Matcher m = p.matcher(phone); 
 
        // Return if the match
        return m.matches(); 
    }

    /*
     * Checks that the status is either E, S, or V for employee, student, or visitor
     */
    private boolean validStatus(String status){
        String[] stat = {"E", "S", "V"};
        if (status == null){
            return false;
        }
        for (int i = 0; i < stat.length; i++){
            if(stat[i].equals(status)){
                return true;
            }
        }
        return false;
    }

    /*
     * checks if there is an id provided for employees and students
     */
    private boolean checkID(String status,  String id){
        if ((status.equals("E") || status.equals("S")) && id == null ){
            return false;
        }
        return true;

    }

    // checks inputs and executes query to insert a new entry in Driver
    public void enterDriverInfoHelper(Statement statement, String phoneNumber, String name, String status, String id){
        String query = null;
        if (!checkID(status, id)){
            System.out.println("Students and Employees must have an id");
            return;
        }
        if(id == null){
            query = String.format("INSERT INTO Driver (phone, name, status) VALUES (\'%s\', \'%s\', \'%s\');", phoneNumber, name, status);
        } else {
            query = String.format("INSERT INTO Driver (phone, name, status, univ_id) VALUES (\'%s\', \'%s\', \'%s\', \'%s\');", phoneNumber, name, status, id);
        }
        
        try{
            statement.executeUpdate(query);
            System.out.println("Driver Added");
        } catch (Throwable oops) {
			System.out.println("Error Occurred while inserting data " + oops.getMessage());
		}
    }

    /*
     * Updates driver identified with phone or univ_id, and update specified fields
     */
    public void updateDriverInfo(Statement statement, Scanner sc){
        String id = null;
        String status = null;
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.nextLine().trim();
        if(!isPhone(phoneNumber)){
            System.out.println("Phone number must be all digits");
            return;
        }
        StringBuilder sb = new StringBuilder();
        System.out.print("\nUpdate status? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter status ('E', 'S', or 'V') : ");
            status = sc.nextLine().trim();
            if(!validStatus(status)){
                System.out.println("Status must have values 'E', 'S', or 'V'");
                return;
            }
            sb.append(String.format("status = \'%s\'", status));
        }
        System.out.print("\nUpdate name? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter name : ");
            String name = sc.nextLine().trim();
            if(sb.length() != 0){
                sb.append(" , ");
            }
            sb.append(String.format("name = \'%s\'", name));
        }
        if(!"V".equals(status)){

            System.out.print("\nUpdate university id? (y/n): ");
            if(sc.nextLine().equals("y")){
                System.out.print("\nEnter university id : ");
                id = sc.nextLine().trim();
                if(sb.length() != 0){
                    sb.append(" , ");
                }
                sb.append(String.format("univ_id = \'%s\'", id));
            }
            if(id == null && !prevEorS(statement, phoneNumber)){
                System.out.println("Id must be entered if not previously an employee or student");
                return;
            }
        } else {
            if(sb.length() != 0){
                sb.append(" , ");
            }
            sb.append(" univ_id = NULL ");
        }
        
        
        System.out.print("\nUpdate phone number? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter new phone number : ");
            String phone = sc.nextLine().trim();
            if(!isPhone(phone)){
                System.out.println("Phone number must be all digits");
                return;
            }
            if(sb.length() != 0){
                sb.append(" , ");
            }
            sb.append(String.format(" phone = \'%s\' ", phone));
        }
       
        if(sb.length() != 0){
            updateDriverInfoHelper(statement, phoneNumber, sb.toString());
        } else {
            System.out.println("There were no changes indicated");
        }
        
    }

    // checks the previous status of the driver to see if they need to update univ_id
    private boolean prevEorS(Statement statement, String phone){
        String getPrevStatus = String.format("SELECT * FROM Driver WHERE phone = \'%s\';", phone);
        try{
            ResultSet resultSet = statement.executeQuery(getPrevStatus);
            if(resultSet.next()){
                
                String stat = resultSet.getString("status");
                System.out.println(stat);
                if("E".equals(stat) || "S".equals(stat)){
                    return true;
                }
            } 
        }catch (Throwable oops) {
			System.out.println("Error while checking previous status: " + oops.getMessage());
		}
        return false;
    }
    /*
     * Executes query to update driver and returns whether it was successful
     */
    public void updateDriverInfoHelper(Statement statement, String phone, String update){
        try{
            
            int rowsAffected = 0;
            rowsAffected = statement.executeUpdate(String.format("UPDATE Driver SET %s WHERE phone = \'%s\';", update, phone));
            if(rowsAffected > 0){
                System.out.println("Driver Updated");
            } else {
                System.out.println("Unable to update driver info");
            }
            
        } catch (Throwable oops) {
			System.out.println("Error Occurred while updating data " + oops.getMessage());

		}
    }

    /*
     * Removes entry from Driver table
     */
    public void deleteDriverInfo(Statement statement, Scanner sc){
        int opt = -1;
        String phoneNumber = null;
        String id = null;
        do{
            System.out.print("Delete Driver by (1) phone or (2) university id: ");
            opt = sc.nextInt();
            sc.nextLine();
            if (opt == 1){
                System.out.print("\nEnter phone number: ");
                phoneNumber = sc.nextLine().trim();
                if(!isPhone(phoneNumber)){
                    System.out.println("Phone number must be all digits");
                    return;
                }
                deleteDriverInfoHelper(statement, id, phoneNumber); 

            }
            if (opt == 2){
                System.out.print("\nEnter id: ");
                id = sc.nextLine().trim();
                if(id == null || "".equals(id)){
                    System.out.println("ID must contain a value for deletion");
                    return;
                }
                deleteDriverInfoHelper(statement, id, phoneNumber); 
               
            }
            
        } while (opt != 1 && opt != 2);
        
    }

    /*
     * Executes query to remove driver and returns whether it was successful
     */
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
            System.out.println("Error Occurred while deleting data " + oops.getMessage());

        }
        return 0;

    }

    /**
     * Creates a new entry in parking lot
     */
    public void enterParkingLotInfo(Statement statement, Scanner sc){
        System.out.print("Enter lot name: ");
        String lot_name = sc.nextLine().trim();
        if (lot_name == null || "".equals(lot_name)){
            System.out.println("Lot name must have a value");
            return;
        }
        System.out.print("\nEnter address: ");
        String address = sc.nextLine().trim();
        enterParkingLotInfoHelper(statement, lot_name, address);
       
    }

    /**
     * Helper method to execute a query to insert a new entry in parking lot, and returns whether that query was successful
     */
    public void enterParkingLotInfoHelper(Statement statement, String lot_name, String address){
        String query = String.format("INSERT INTO ParkingLot (lot_name, address) VALUES(\'%s\', \'%s\');", lot_name, address);
        try{
            statement.executeUpdate(query);
            System.out.println("Parking Lot Added");
        } catch (Throwable oops) {
			System.out.println("Error Occurred while inserting data " + oops.getMessage());

		}
    }

    /**
     * Updates an entry in the parking lot table with the given information
     */
    public void updateParkingLotInfo(Statement statement, Scanner sc){
        String lot_name = null;
        String address = null;
        System.out.print("\nEnter lot name: ");
        lot_name = sc.nextLine().trim();
        if (lot_name == null || "".equals(lot_name)){
            System.out.println("Lot name must have a value");
            return;
        }
        StringBuilder sb = new StringBuilder();
        System.out.print("\nUpdate lot name? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter updated lot name: ");
            String new_lot_name = sc.nextLine().trim();
            sb.append(String.format("lot_name = \'%s\' ", new_lot_name));
        }
        System.out.print("\nUpdate address? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter updated address: ");
            address = sc.nextLine().trim();
            if(sb.length() != 0){
                sb.append(", ");
            }
            sb.append(String.format("address = \'%s\' ", address));
        }
        updateParkingLotInfoHelper(statement, lot_name, sb.toString());
       
    }

    /**
     * Executes query to update an entry in parking lot table and returns whether it was successful
     */
    public void updateParkingLotInfoHelper(Statement statement, String lot_name, String updates){
        try{
            int rowsAdded = statement.executeUpdate(String.format("UPDATE ParkingLot SET %s WHERE lot_name = \'%s\';", updates, lot_name));
            if(rowsAdded > 0){
                System.out.println("Parking lot updated successfully");
            } else {
                System.out.println("Could not update parking lot");
            }
        } catch (Throwable oops) {
			System.out.println("Error Occurred while updating data " + oops.getMessage());

		}
  
    }

    /**
     * Removes parking lot with given lot name from the parking lot table
     */
    public void deleteParkingLotInfo(Statement statement, Scanner sc){
        System.out.print("Enter lot name: ");
        String lot_name = sc.nextLine().trim();
        deleteParkingLotInfoHelper(statement, lot_name);
    }

    /*
     * Helper method that removes the parking lot with the given lot name from the parking lot table
     */
    public int deleteParkingLotInfoHelper(Statement statement, String lot_name){
        int rowsAffected = -1;
        if(lot_name == null){
            System.out.println("Lot name must have a value");
            return 0;
        }
        try{
            rowsAffected = statement.executeUpdate(String.format("DELETE FROM ParkingLot WHERE lot_name = \'%s\';", lot_name));
            if (rowsAffected > 0){
                System.out.println("Parking Lot Deleted");

            } else {
                System.out.println("Parking Lot did not exist prior to delete operation");
            }
            return rowsAffected;
        } catch (Throwable oops) {
            System.out.println("Error Occurred while deleting data " + oops.getMessage());

        }
        return 0;
    }

    /*
     * Checks whether there is a string of length 1 or 2 for zone id
     */
    private boolean validZoneId(String zone_id){
        if(zone_id == null){
            return false;
        }
        if(zone_id.length() > 0 && zone_id.length() <= 2){
            return true;
        }
        return false;
    }

    /*
     * Adds an entry in zone with the given information
     */
    public void enterZoneInfo(Statement statement, Scanner sc){
        System.out.print("Enter zone id: ");
        String zone_id = sc.nextLine().trim();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine().trim();
        enterZoneInfoHelper(statement, zone_id, lot_name);
    }

    /*
     * checks for valid values for zone and executes query to add entry to zone table
     */
    public void enterZoneInfoHelper(Statement statement, String zone_id, String lot_name){
        if(!validZoneId(zone_id)){
            System.out.println("Zone id must be at least 1 character and at most 2 characters");
            return;
        }
        String query = String.format("INSERT INTO Zone (zone_id, lot_name) VALUES(\'%s\', \'%s\');", zone_id, lot_name);
        try{
            statement.executeUpdate(query);
            System.out.println("Zone Added");
        } catch (Throwable oops) {
			System.out.println("Error Occurred while inserting data " + oops.getMessage());
		}
    }

    /*
     * Updates an entry in the zone table with the given information
     */
    public void updateZoneInfo(Statement statement, Scanner sc){
        System.out.print("Enter zone id: ");
        String zone_id = sc.nextLine().trim();
        if(!validZoneId(zone_id)){
            System.out.println("Zone id must be at least 1 character and at most 2 characters");
            return;
        }
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine().trim();
        StringBuilder sb = new StringBuilder();

        System.out.print("\nUpdate zone id? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter updated zone id: ");
            String new_zone_id = sc.nextLine().trim(); 
            if(!validZoneId(new_zone_id)){
                System.out.println("Zone id must be at least 1 character and at most 2 characters");
                return;
            }
            sb.append(String.format("zone_id = \'%s\'", new_zone_id));
        }
        System.out.print("\nUpdate lot name? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter updated lot name: ");
            String new_lot_name = sc.nextLine().trim();
            if(sb.length() != 0){
                sb.append(", ");
            }
            sb.append(String.format("lot_name = \'%s\'", new_lot_name));
        }
        updateZoneInfoHelper(statement, zone_id, lot_name, sb.toString());
              
    }

    /*
     * Executes query to update entry in zone table with the given info
     */
    public void updateZoneInfoHelper(Statement statement, String zone_id, String lot_name, String updates){
        if (0 != updates.length()){
            try{
                String query = String.format("UPDATE Zone SET %s WHERE zone_id = '%s' AND lot_name = '%s';", updates, zone_id, lot_name);
                
                statement.executeUpdate(query);
                System.out.println("Zone Updated");
            } catch (Throwable oops) {
                System.out.println("Error Occurred while updating data " + oops.getMessage());

            }
        } else {
            System.out.println("No updates specified");
        }
       
    }

    /*
     * Removes entry from zone given zone id and lot name
     */
    public void deleteZoneInfo(Statement statement, Scanner sc){
        System.out.print("Enter zone id: ");
        String zone_id = sc.nextLine().trim();
        if(!validZoneId(zone_id)){
            System.out.println("Zone id must be at most 2 characters");
            return;
        }
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine().trim();
        
        deleteZoneInfoHelper(statement, zone_id, lot_name);
    }

    /*
     * Executes query to remove entry from zone table given zone id and lot name
     */
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
            System.out.println("Error Occurred while deleting data " + oops.getMessage());
        }
        return 0;
    }

    /*
     * checks if the value of space type is Electric, Handicap, Regular, or Compact Car
     */
    private boolean validSpaceType(String space_type){
        String[] types = {"Electric", "Handicap", "Regular", "Compact Car"};
        if(space_type == null){
            return false;
        }
        for(int i = 0; i < types.length; i++){
            if(types[i].equals(space_type)){
                return true;
            }
        }
        return false;
    }

    /*
     * Adds entry to space given the input information
     */
    public void enterSpaceInfo(Statement statement, Scanner sc){
        System.out.print("Enter space number: ");
        int space_number = sc.nextInt();
        sc.nextLine();
        System.out.print("\nEnter zone id: ");
        String zone_id = sc.nextLine().trim();
        if(!validZoneId(zone_id)){
            System.out.println("Zone id must be at most 2 characters");
            return;
        }
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine().trim();
        if(lot_name == null || "".equals(lot_name)){
            System.out.println("Lot name must have a value");
            return;
        }
        System.out.print("\nEnter space type ('Electric', 'Handicap', 'Compact Car', or 'Regular'): ");
        String space_type = sc.nextLine().trim();
        if(!validSpaceType(space_type)){
            System.out.println("Space must be of type 'Electric', 'Handicap', 'Compact Car', or 'Regular'");
            return;
        }
        enterSpaceInfoHelper(statement, space_number, zone_id, lot_name, space_type);
     
    }

    /*
     * Executes query to insert entry into space table
     */
    public void enterSpaceInfoHelper(Statement statement, int space_number, String zone_id, String lot_name, String space_type){
        try{
            statement.executeUpdate(String.format("INSERT INTO Space (space_number, lot_name, zone_id, space_type, availability_status) VALUES(%d, \'%s\', \'%s\', \'%s\', 1);", space_number, lot_name, zone_id, space_type));
            System.out.println("Space Added");
        } catch (Throwable oops) {
            System.out.println("Error Occurred while inserting data " + oops.getMessage());

        }
    }

    /*
     * Updates a space with the input information
     */
    public void updateSpaceInfo(Statement statement, Scanner sc){
        System.out.print("Enter space number: ");
        int space_number = sc.nextInt();
        sc.nextLine();
        System.out.print("\nEnter zone id: ");
        String zone_id = sc.nextLine().trim().trim();
        if(!validZoneId(zone_id)){
            System.out.println("Zone id must be at most 2 characters");
            return;
        }
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine().trim().trim();
        if(lot_name == null || "".equals(lot_name)){
            System.out.println("Lot name must have a value");
            return;
        }
        String space_type = null;
        int availability_status = -1;
        StringBuilder sb = new StringBuilder();
        System.out.print("\nUpdate space type? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nUpdate space type ('Electric', 'Handicap', 'Compact Car', or 'Regular'): ");
            space_type = sc.nextLine().trim();
            if(!validSpaceType(space_type)){
                System.out.println("Space must be of type 'Electric', 'Handicap', 'Compact Car', or 'Regular'");
                return;
            }
            sb.append(String.format("space_type = \'%s\'", space_type));
        }
        System.out.print("\nUpdate availability status? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nUpdate availability status (0 or 1): ");
            availability_status = sc.nextInt();
            sc.nextLine();
            if (availability_status != 1 && availability_status != 0){
                System.out.println("Availability status must have value 0 or 1");
                return; 
            }
            if(sb.length() != 0){
                sb.append(String.format(" , availability_status = %d", availability_status));
            } else {
                sb.append(String.format("availability_status = %d", availability_status));
            }
        }
        System.out.print("\nUpdate space number? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter updated space number: ");
            int new_space_number = sc.nextInt();
            sc.nextLine();
            if(sb.length() != 0){
                sb.append(", ");
            }
            sb.append(String.format("space_number = %d ", new_space_number));
        }
        System.out.print("\nUpdate zone id? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter updated zone id: ");
            String new_zone_id = sc.nextLine().trim();
            if(!validZoneId(new_zone_id)){
                System.out.println("Invalid zone id. Must be greater than 0 characters and less than 3 characters");
                return;
            }
            if(sb.length() != 0){
                sb.append(", ");
            }
            
            sb.append(String.format(" zone_id = \'%s\' ", new_zone_id));
        }
        System.out.print("\nUpdate lot name? (y/n): ");
        if(sc.nextLine().equals("y")){
            System.out.print("\nEnter updated lot name: ");
            String new_lot_name = sc.nextLine().trim();
            if(new_lot_name.length() == 0){
                System.out.println("There must be a valud for lot name to update");
                return;
            }
            if(sb.length() != 0){
                sb.append(", ");
            }
            sb.append(String.format(" lot_name = \'%s\' ", new_lot_name));
        }

        
        if(sb.length() == 0){
            System.out.println("No updates to space indicated");
            return;
        }
        
        updateSpaceInfoHelper(statement, zone_id, lot_name, space_number, sb.toString());
    }

    /*
     * Executes query to update space with identifying and updating information
     */
    public void updateSpaceInfoHelper(Statement statement, String zone_id, String lot_name, int space_number, String updates){
        String query = String.format("UPDATE Space SET " + updates +
        " WHERE zone_id = \'%s\' AND lot_name = \'%s\' AND space_number = %d;",  zone_id, lot_name, space_number);
        try{
            statement.executeUpdate(query);
            System.out.println("Space Updated");
        } catch (Throwable oops) {
			System.out.println("Error Occurred while updating data " + oops.getMessage());
        }
        
    }

    /*
     * Removes entry from Space given space number, zone id, and lot name
     */
    public void deleteSpaceInfo(Statement statement, Scanner sc){
        System.out.print("Enter space number: ");
        int space_number = sc.nextInt();
        sc.nextLine();
        System.out.print("\nEnter zone id: ");
        String zone_id = sc.nextLine().trim();
        if(!validZoneId(zone_id)){
            System.out.println("Zone id must be at most 2 characters");
            return;
        }
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine().trim();
        if(lot_name == null || "".equals(lot_name)){
            System.out.println("Lot name must have a value");
            return;
        }
        deleteSpaceInfoHelper(statement, space_number, zone_id, lot_name);
    }

    /*
     * Executes query to remove entry from Space table given the identifying information
     */
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
            System.out.println("Error Occurred while deleting data " + oops.getMessage());

        }
        return 0;
    }

    /*
     * Checks whether date is in valid format
     */
    private boolean isValidDate(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
    

    /*
     * Checks whether permit is of type commuter residential, special event, peak hours, or park & ride
     */
    private boolean validPermitType(String permit_type){
        if(permit_type == null || "".equals(permit_type)){
            return false;
        }
        String[] types = {"Commuter", "Residential", "Special Event", "Peak Hours", "Park & Ride"};
        for(int i = 0; i < types.length; i++){
            if(types[i].toLowerCase().equals(permit_type.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    /*
     * Updates entry in permit with the given information
     */
    public void updatePermitInfo(Statement statement, Scanner sc){
        System.out.print("Enter permit id: ");
        int permit_id = sc.nextInt();
        sc.nextLine();
        StringBuilder sb = new StringBuilder();
        System.out.print("\nUpdate space type ('Electric', 'Handicap', 'Compact Car', or 'Regular')\n or (-1 to not update): ");
        String space_type = sc.nextLine().trim();
        if (!space_type.equals("-1")){
            sb.append("space_type = \'" + space_type + "\'");
        } 
        System.out.print("\nUpdate permit type ('Commuter', 'Residential', 'Special Evenet', 'Peak Hours', and 'Park & Ride')\n or (-1 to not update): ");
        String permit_type = sc.nextLine().trim();
        if (!permit_type.equals("-1")){
            if(sb.length() > 0){
                sb.append(", ");
            }
            if(!validPermitType(permit_type)){ 
                System.out.println("Invalid permit type. Must be 'Commuter', 'Residential', 'Special Evenet', 'Peak Hours', and 'Park & Ride'");
                return;
            }
            sb.append("permit_type = \'" + permit_type + "\'");
        } 
        System.out.print("\nUpdate Start Date in format YYYY-MM-DD (-1 to not update): ");
        String start_date = sc.nextLine().trim();
        if (!start_date.equals("-1")){
            if (!isValidDate("yyyy-MM-dd", start_date)){
                System.out.println("Date must be in format YYYY-MM-DD");
                return;
            }
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append("start_date = \'" + start_date + "\'");
        }
        System.out.print("\nUpdate Expiration Date in format YYYY-MM-DD (-1 to not update): ");
        String expiration_date = sc.nextLine().trim();
        if (!expiration_date.equals("-1")){
            if (!isValidDate("yyyy-MM-dd", expiration_date)){
                System.out.println("Date must be in format YYYY-MM-DD");
                return;
            }
            if(sb.length() > 0){
                sb.append(", ");
            }
            sb.append("expiration_date = \'" + expiration_date + "\'");
        }
        System.out.print("\nUpdate Expiration Time in format HH:MM:SS (-1 to not update): ");
        String expiration_time_str = sc.nextLine().trim();

         if (!expiration_time_str.equals("-1")){
            try{
                Time expiration_time = Time.valueOf(expiration_time_str);
                if(sb.length() > 0){
                    sb.append(", ");
                }
            
            sb.append("expiration_time = \'" + expiration_time.toString() + "\'");

            } catch(IllegalArgumentException e){
                System.out.println("Time must be in format HH:MM:SS");
                return;
            }

            
           
        }
        if(sb.length() > 0){
            updatePermitInfoHelper(statement, permit_id, sb.toString());

        } else{
            System.out.println("No updates were indicated for permit");
        }

    }

    /**
     * Executes query to update permit
     */
    public void updatePermitInfoHelper(Statement statement, int permit_id, String updates){
        String query = "UPDATE Permit SET "+ updates + " WHERE permit_id = %d;";
        try{
            statement.executeUpdate(String.format(query,permit_id));
            System.out.println("Permit Updated");
        } catch (Throwable oops) {
            System.out.println("Error Occurred while updating data " + oops.getMessage());
        }
    }

    /*
     * Deletes permit with the given id
     */
    public void deletePermitInfo(Statement statement, Scanner sc){
        System.out.print("Enter permit id: ");
        int permit_id = sc.nextInt();
        sc.nextLine();
        deletePermitInfoHelper(statement, permit_id);
    }

    /*
     * Executes query to delete permit with the given id
     */
    public int deletePermitInfoHelper(Statement statement, int permit_id){
        int rowsDeleted = -1;
        try{
            rowsDeleted = statement.executeUpdate(String.format("DELETE FROM Permit WHERE permit_id = %d;", permit_id));
            if (rowsDeleted > 0){
                System.out.println("Permit Deleted");

            } else {
                System.out.println("Permit did not exist prior to deletion");

            }
            return rowsDeleted;
        } catch (Throwable oops) {
			System.out.println("Error Occurred while deleting data " + oops.getMessage());
        }
        return 0;
    }

    /*
     * Assign type to an existing space
     */
    public void assignTypeToSpace(Statement statement, Scanner sc){
        System.out.print("Enter space number: ");
        int space_number = sc.nextInt();
        sc.nextLine();
        System.out.print("\nEnter zone id: ");
        String zone_id = sc.nextLine().trim();
        if(!validZoneId(zone_id)){
            System.out.println("Zone id must be at max two characters");
            return;
        }
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine().trim();
        if(lot_name == null || lot_name.equals("")){
            System.out.println("Lot name must have a value");
            return;
        }
        System.out.print("\nEnter space type ('Electric', 'Handicap', 'Compact Car', or 'Regular'): ");
        String space_type = sc.nextLine().trim();
        if(!validSpaceType(space_type)){
            System.out.println("Space must be of type 'Electric', 'Handicap', 'Compact Car', or 'Regular'");
            return;
        }
        assignTypeToSpaceHelper(statement, space_type, space_number, lot_name, zone_id);
        
    }

    /*
     * helper method that executes query to update entry in space
     */
    public void assignTypeToSpaceHelper(Statement statement, String space_type, int space_number, String lot_name, String zone_id){
        try{
            statement.executeUpdate(String.format("UPDATE Space SET space_type = \'%s\' WHERE space_number = %d AND lot_name = \'%s\' AND zone_id = \'%s\';", space_type, space_number, lot_name, zone_id));
            System.out.println("Space Type Assigned");
        } catch (Throwable oops) {
            System.out.println("Error Occurred while inserting data " + oops.getMessage());
        }
    }

    /*
     * Updates a citation to paid or unpaid
     */
    public void updateCitationPayment(Statement statement, Scanner sc){
        System.out.print("\nEnter citation number: ");
        int citation_number = sc.nextInt();
        sc.nextLine();
        System.out.print("\nEnter payment status 0 (not paid) or 1 (paid): ");
        int payment_status = sc.nextInt();
        sc.nextLine();
        if(payment_status != 1 && payment_status != 0){
            System.out.println("Payment status must be of values 0 or 1");
        }
        updateCitationPaymentHelper(statement, citation_number, payment_status);
    }

    /*
     * Execute query that updates the payment status of a citation
     */
    public void updateCitationPaymentHelper(Statement statement, int citation_number, int payment_status){
        try{
            statement.executeUpdate(String.format("UPDATE Citation set payment_status = %d WHERE citation_number = %d;", payment_status, citation_number));
            if(payment_status == 1){
                System.out.println("Citation Payment Successful");
            } else {
                System.out.println("Citation Status changed to unpaid");
            }
        } catch (Throwable oops) {
            System.out.println("Error Occurred while updating data " + oops.getMessage());

        }
    }

}
