package src;
import java.sql.*;
import java.util.Scanner;

public class reports {
    public void generateReportCitations(Statement statement){

        // Generate a report for citations
    } 
    // Generate a report for citations
    
    public void generateZoneCitations(Statement statement) {
        
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given month
        // Implement code for generating the monthly citation report for each lot
    }
    
    public void totalCitationsCountMonthly(Statement statement) {
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given month
        // Implement code for generating the monthly citation report for each lot
    }
        
    public void totalCitationsCountYearly(Statement statement) {
        // For each lot, generate a report for the total number of citations given in
        // all zones in the lot for a given year
        // Implement code for generating the yearly citation report for each lot

    }
        
    public void listOfZones (Statement statement) {
        // Return the list of zones for each lot as tuple pairs (lot, zone)
        // Implement code to return the list of zones for each parking lot
    }
        

    public void carsInViolation(Statement statement) {
        // Return the number of cars that are currently in violation
        // Implement code to count the number of cars in violation
    }
        

    public void employeesHavePermits(Statement statement) {
        // Return the number of employees having permits for a given parking zone
        // Implement code to count the employees with permits in a specific zone
    }
        

    public void returnPermitInfo(Statement statement) {
        // Return permit information given an ID or phone number
        // Implement code to retrieve permit information by ID or phone number

    }
        
    public void generateSpaceAvailable(Statement statement) {
        // Return an available space number given a space type in a given parking lot
    }
        
}
