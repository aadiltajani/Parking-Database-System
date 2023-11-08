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
        String query = String.format("INSERT INTO Driver VALUES(%s, %s, %s, %s);", phoneNumber, name, status, id);
        try{
            statement.executeUpdate(query);
            System.out.println("Driver Added");
        } catch (Throwable oops) {
			oops.printStackTrace();
		}
       
    }

    // public void updateDriverInfo(Statement statement){
    //     Scanner sc = new Scanner(System.in);
    //     System.out.print("Enter phone number: ");
    //     String phoneNumber = sc.nextLine();
    //     System.out.print("\nUpdate status? (y/n): ");
    //     if(sc.next() == "y"){
    //         System.out.print("\nEnter status : ");
    //     }
    //     sc.close();

    // }
}