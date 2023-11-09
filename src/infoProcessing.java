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
}