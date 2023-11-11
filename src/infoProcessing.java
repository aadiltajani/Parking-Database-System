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
                System.out.println("Driver Deleted");
               
            }
        } while (option != 1 && option != 2);
        sc.close();
    }

    public void deleteDriverInfoHelper(Statement statement, String id, String phone){
        try{
            if(phone == null){
                statement.executeUpdate(String.format("DELETE FROM Driver WHERE univ_id = \'%s\'';", id));
            } else {
                statement.executeUpdate(String.format("DELETE FROM Driver WHERE phone = \'%s\'';", phone));
            }
        } catch (Throwable oops) {
            oops.printStackTrace();
        }

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
        String query = String.format("INSERT INTO ParkingLot (lot_name, address) VALUES(%s, %s);", lot_name, address);
        try{
            statement.executeUpdate(query);
            System.out.println("Parking Lot Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
    }

    public void updateParkingLotInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        int option = -1;
        String lot_name = null;
        String address = null;
        do{
            System.out.print("Update lot by (1) address or (2) name");
            option = sc.nextInt();
            if (option == 1){
                System.out.print("\nEnter address: ");
                address = sc.nextLine();
                System.out.print("\nUpdate lot name: ");
                lot_name = sc.nextLine();
                updateParkingLotInfoHelper(statement, lot_name, address, option);
                System.out.println("Lot updated");

            }
            if (option == 2){
                System.out.print("\nEnter lot name: ");
                lot_name = sc.nextLine();
                System.out.print("\nUpdate address: ");
                address = sc.nextLine();
                updateParkingLotInfoHelper(statement, lot_name, address, option);
                System.out.println("Lot updated");
               
            }
        } while (option != 1 && option != 2);
        sc.close();
    }

    public void updateParkingLotInfoHelper(Statement statement, String lot_name, String address, int option){
        try{
            if(option == 1){
                statement.executeUpdate(String.format("UPDATE ParkingLot SET lot_name = \'%s\' WHERE address = \'%s\'';", lot_name, address));
            }else{
                statement.executeUpdate(String.format("UPDATE ParkingLot SET address = \'%s\' WHERE lot_name = \'%s\'';", address, lot_name));
            }
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

    public void deleteParkingLotInfoHelper(Statement statement, String lot_name){
        try{
            statement.executeUpdate(String.format("DELETE FROM ParkingLot WHERE lot_name = \'%s\';", lot_name));
            System.out.println("Parking Lot Deleted");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
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
        System.out.print("\nEnter updated zone id: ");
        String new_zone_id = sc.nextLine();
        System.out.print("\nEnter updated lot name: ");
        String new_lot_name = sc.nextLine();
        sc.close();
        updateZoneInfoHelper(statement, zone_id, lot_name, new_zone_id, new_lot_name);
    }

    public void updateZoneInfoHelper(Statement statement, String zone_id, String lot_name, String new_zone_id, String new_lot_name){
        String query = String.format("Update Zone SET zone_id = \'%s\', lot_name = \'%s\' WHERE zone_id = \'%s\' AND lot_name = \'%s\';",new_zone_id, new_lot_name, zone_id, lot_name);
        try{
            statement.executeUpdate(query);
            System.out.println("Zone Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
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

    public void deleteZoneInfoHelper(Statement statement, String zone_id, String lot_name){
        try{
            statement.executeUpdate(String.format("DELETE FROM Zone WHERE zone_id = \'%s\' AND lot_name = \'%s\'';",zone_id, lot_name));
            System.out.println("Zone Deleted");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
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
        // TODO implement
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

    public void deleteSpaceInfoHelper(Statement statement, int space_number, String zone_id, String lot_name){
        try{
            statement.executeUpdate(String.format("DELETE FROM Space WHERE space_number = %d AND zone_id = %s AND lot_name = %s;",space_number, zone_id, lot_name));
            System.out.println("Zone Deleted");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
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
         try{
            statement.executeUpdate(String.format("INSERT INTO Permit (permit_id, space_type, permit_type, start_date, expiration_date, expiration_time) VALUES (%d, %s, %s, %s', %s, %s);",permit_id, space_type, permit_type, start_date, expiration_date, expiration_time));
            System.out.println("Permit Added");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public void updatePermitInfo(){
        // TODO
    }

    public void deletePermitInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter permit id: ");
        int permit_id = sc.nextInt();
        sc.close();
         try{
            statement.executeUpdate(String.format("DELETE from Permit WHERE permit_id = %d;",permit_id));
            System.out.println("Permit Deleted");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
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
        try{
            statement.executeUpdate(String.format("UPDATE Space SET space_type = %s WHERE space_number = %d AND lot_name = %s AND zone_id = %s;", space_type, space_number, lot_name, zone_id));
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