package src;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DisplayTables {
    private static final int COLUMN_WIDTH = 22;

    public static void main(Connection connection, Scanner scanner) {

        try {
            // Get the list of tables in your database
            List<String> tableNames = getTableNames(connection);

            // Ask the user to choose a table
            String selectedTable = chooseTable(tableNames, scanner);
            if (selectedTable == null) return;
            // Display records from the selected table
            displayTableRecords(connection, selectedTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();

        // Fetch table names from the database
        String query = "SHOW TABLES";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                tableNames.add(resultSet.getString(1));
            }
        }

        return tableNames;
    }

    private static String chooseTable(List<String> tableNames, Scanner scanner) {
        System.out.println("Available Tables:");
        int c = -2;
        for (int i = 0; i < tableNames.size(); i++) {
            System.out.println((i + 1) + ". " + tableNames.get(i));
            c = i + 2;
        }
        System.out.println(c + ". Go back.");
        int choice;
        System.out.print("Enter the number of the table you want to see: ");
        // Scanner scanner = new Scanner(System.in);
        choice = scanner.nextInt();
        // scanner.close();
        if (choice < 1 || choice > tableNames.size()) return null;
        else return tableNames.get(choice - 1);
    }

    private static void displayTableRecords(Connection connection, String tableName) throws SQLException {
        // Fetch all records from the selected table
        String query = "SELECT * FROM " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Get metadata about the result set
            int columnCount = resultSet.getMetaData().getColumnCount();

            // Print column headers with fixed width
            System.out.println("===================================================================================================");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                System.out.printf("%-" + COLUMN_WIDTH + "s", columnName);
            }
            System.out.println();
            System.out.println("===================================================================================================");
            // Print rows with fixed width
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnValue = String.valueOf(resultSet.getObject(i));
                    System.out.printf("%-" + COLUMN_WIDTH + "s", columnValue);
                }
                System.out.println();
            }
            System.out.println("===================================================================================================");
        }
    }
}
