package src;
import java.sql.*;
import java.util.Scanner;

public class infoProcessing {
    // private Connection connection = null;
    // private Statement statement = null;
    // private ResultSet result = null;

    // public void infoProcessing(Connection connection){
    //     this.statement = connection.createStatement();
    // }

    public void enterDriverInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.print("\nEnter name: ");
        String name = sc.nextLine();
        System.out.print("\nEnter status: ");
        String status = sc.nextLine();
        System.out.print("\nEnter university id: ");
        String id = sc.nextLine();
        sc.close();
        String query = String.format("INSERT INTO Driver (phone, name, status, univ_id) VALUES(%s, %s, %s, %s);", phoneNumber, name, status, id);
        try{
            statement.executeUpdate(query);
            System.out.println("Driver Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
       
    }

    public void updateDriverInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.print("\nUpdate status? (y/n): ");
        if(sc.next() == "y"){
            System.out.print("\nEnter status : ");
        }
        sc.close();

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
                try{
                    statement.executeUpdate(String.format("DELETE FROM Driver WHERE phone = %s;", phoneNumber));
                    System.out.println("Driver Deleted");
                } catch (Throwable oops) {
                    oops.printStackTrace();
                }

            }
            if (option == 2){
                System.out.print("\nEnter id: ");
                id = sc.nextLine();
                try{
                    statement.executeUpdate(String.format("DELETE FROM Driver WHERE univ_id = %s;", id));
                    System.out.println("Driver Deleted");
                } catch (Throwable oops) {
                    oops.printStackTrace();
                }
            }
        } while (option != 1 && option != 2);
        sc.close();
    }

    public void enterParkingLotInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter lot name: ");
        String lot_name = sc.nextLine();
        System.out.print("\nEnter address: ");
        String address = sc.nextLine();
        sc.close();
        String query = String.format("INSERT INTO ParkingLot (lot_name, address) VALUES(%s, %s);", lot_name, address);
        try{
            statement.executeUpdate(query);
            System.out.println("Parking Lot Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
    }

    public void updateParkingLotInfo(Statement statement){
        //TODO implement
    }

    public void deleteParkingLotInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter lot name: ");
        String lot_name = sc.nextLine();
        sc.close();
        try{
            statement.executeUpdate(String.format("DELETE FROM ParkingLot WHERE lot_name = %s;", lot_name));
            System.out.println("Parking Lot Deleted");
        } catch (Throwable oops) {
            oops.printStackTrace();
        }
    }

    public void enterZoneInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        sc.close();
        String query = String.format("INSERT INTO Zone (zone_id, lot_name) VALUES(%s, %s);", zone_id, lot_name);
        try{
            statement.executeUpdate(query);
            System.out.println("Zone Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
    }

    public void updateZoneInfo(Statement statement){
        // TODO impement
    }

    public void deleteZoneInfo(Statement statement){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter zone id: ");
        String zone_id = sc.nextLine();
        System.out.print("\nEnter lot name: ");
        String lot_name = sc.nextLine();
        sc.close();
        try{
            statement.executeUpdate(String.format("DELETE FROM Zone WHERE zone_id = %s AND lot_name = %s;",zone_id, lot_name));
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
        try{
            statement.executeUpdate(String.format("INSERT INTO Space (space_number, lot_name, zone_id, space_type, avilability_status) VALUES(%d, %s, %s, %s, 1);", space_number, lot_name, zone_id, space_type));
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
        // TODO
    }

}