import java.sql.*;

public Class infoProcessing {
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet result = null;

    public void infoProcessing(Connection connection){
        this.statement = connection.createStatement();
    }

    public String enterDriverInfo(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.print("\nEnter name: ");
        String name = sc.nextLine();
        System.out.print("\nEnter status: ")
        String status = sc.nextLine();
        System.out.print("\nEnter university id: ")
        String id = sc.nextLine();
        return String.format("INSERT INTO Driver VALUES(%s, %s, %s, %s);", phoneNumber, name, status, id);
    }

    public String updateDriverInfo(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.print("\nUpdate status? (y/n): ");
        if(sc.next() == 'y'){
            System.out.print("\nEnter status : ");
        }

    }
}