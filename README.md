# Parking-Database-System
Parking Database Management System Project for CSC 540 - Team CC (Aadil _atajani_, Daniel _dsbuchan_, Manali _mteke_, Ophelia _oysin_)

Note:
- In order to prevent any sensitive data leaks, we have implemented a `db_keys` properties file that will have the user ID and password for the connection and doesn't get shared anywhere but is required locally, to run this application.
- Make sure the db_keys file is present outside the src folder as it is accessed in the program via `../db_keys` or change the path in the file where you have it but make sure it is not uploaded to GitHub.

Create a `db_keys` file with the fields:
- `username=<username>`
- `password=<password> `

# Project Details

## Narrative

You are to design and build the Wolf Parking Management System, for managing parking lots and its users on campus. The database system will be used by the administrators of the parking services, and should maintain information on at least the following:
Driver Information: name, status (‘S’, ‘E’, or ‘V’ depending on whether a student, or an employee or a visitor), ID (students and employees have a UnivID as a unique identifier) or phone number (the unique identifier for visitors).

- Parking Lot Information: name of lot, address, zones, spaces.
- Zone Information: zone ID (an identifier that is at most two characters long: A, B, C, D, AS, BS, CS, DS, V; zones A, B, C, D are for employees, zones with suffix ‘S’ are for students, V for visitors).
- Space Information: space number, space type (e.g., “electric”, “handicap”, “compact car”, default is “regular”), availability status.
- Permit Information: permit ID, lot, zone ID, space type, car license number, start date, expiration date, expiration time, associated UnivID or phone number, and permit type (“residential”, “commuter”, “peak hours”, “special event”, and “Park & Ride”).
- Vehicle Information: car license number, model, color, manufacturer, year.
- Citation Information: citation number, car license number, model, color, citation date, citation time, lot, category, fee ($25 for category “Invalid Permit,” $30 for category “Expired Permit,” $40 for category “No Permit”, but handicap users will receive a 50% discount on all citation fees), payment status.

By talking to the university parking manager, we have elicited for you the following information about the Wolf Parking Management System. (Note that in working on this project, you might discover that not every bit of the information has to be explicitly captured in the database. Part of the modeling effort is to decide what to keep and what to discard. In doing your project, you will need to make additional assumptions as well as identify the potential inconsistencies and resolve them. Any reasonable assumptions are fine, but they must be documented in your reports. (You can consult with the TAs or instructor if you have questions about the assumptions.) 

- Administrators of the system can add parking lots to the system, assign zones and spaces to the lots, assign a parking permit to a driver, change the availability of a space, and can check if a car has a valid permit in their lot.
- To park in a lot, a user’s permit must have one of the zones designed for that lot. For example, a lot with zone designation A/B/C allows users with permits either A, B or C. The permit also must include the appropriate type designation to park in dedicated type spaces -like for electric cars.
- Security will create/update/delete citations to vehicles that violate parking regulations; the required information for generating a citation is described above. The payment status of a citation will be changed from unpaid to paid by invoking a payment procedure.
- Driver information, parking lots information, permit information and citation records should be kept in the database system.
- Students and visitors can only have one vehicle on a permit at a time, employees up to 2. A total of 1 permit per student, 1 permit per visitor, and 2 permits per employee are allowed. Students and Employees are allowed an additional permit for special events, or Park & Ride.

## Main Tasks and Operations
- *Information processing*: Enter/update/delete basic information about drivers, parking lots, zones, spaces, and permits. Assign zones to each parking lot and a type to a given space. Citation appeals can be requested and citation payment status updated accordingly. 
- *Maintaining permits and vehicle information for each driver*: Assign permits to drivers according to their status. Enter/update permit information and vehicle ownership information, including remove or add vehicles.
- *Generating and maintaining citations*: Generate/maintain appropriate information for each citation. Before generating a citation, detect parking violations by checking if a car has a valid permit in the lot. Drivers have the ability to pay or appeal citations.
- *Reports*: Generate a report for citations. For each lot, generate a report for the total number of citations given in all zones in the lot for a given time range (e.g., monthly or annually). Return the list of zones for each lot as tuple pairs (lot, zone). Return the number of cars that are currently in violation. Return the number of employees having permits for a given parking zone. Return permit information given an ID or phone number. Return an available space number given a space type in a given parking lot.

## Requirements
- MariaDB JDBC JAR file to connect to the database, and it should be added to the `lib` folder with the appropriate path set in settings or configuration files for Java and any recent version of JDK.

## Structure
- `src/DemoDataLoad.java`: This file takes care of deleting any existing tables, and adding new ones with the demo data all by itself to make sure the database is loaded and ready to perform any tasks. `data/demo_data.json` is provided to view the data in json format and the script doesn't use that and is present just for reference. _Citation_number_ and _PermitId_ fields have been converted to type INT from VARCHAR for our application as that is the design that we followed prior to the availability of the demo data and as per the TAs, we are allowed to the minor changes in order to prevent last-minute design changes.
- `src/Main.java`: This is the main script that has to be run in order to use our CLI-based menu-driven application. We have designed the application that starts with a Menu showing the various types of tasks that can be accomplished through this system. We have followed the 5 task design that includes the Information Processing Menu, Permits, and Vehicles Menu, Citations Menu, Reports Menu, Display Tables, and an Exit option. This allows us to encapsulate all tasks in their respective menu as described in the narrative, making testing and locating the needed tasks easier and simpler. We also make use of a ShutDownHook in Java that makes sure the connection to the database closes for graceful as well as erroneous shutdown of the application.
- `src/Citations.java`: This file takes care of all the functions and tasks related to generating and maintaining citations, as well as checking for any parking violations. These are the functions:
  - `detectParkingViolations`: Take data from user and check if it violates permits
  - `generateCitation`: Take data from user and generate a new citation
  - `maintainCitation`: Ask user what fields are needed to be changed in an existing citation and update them
  - `DisplayGetCitation`: Get citation information and display it before asking user to change it
  - `payCitation`: Pay a citation.
  - `appealCitation`: Appeal a citation, if not already appealed and set the status to Pending
  - `deleteCitation`: Delete a citation and all related citation relationship info
- `src/DisplayTables.java`: This file enables the user/admin to display selected tables from the database from a menu.
  - `main`: Display menu of all table names and take user input to show table data
- `src/InfoProcessing.java`: This file contains all the functions regarding information processing such as CRUD operations for drivers, lots, zones, space, permits, and more.
  - `enterDriverInfo`: Creates a new entry in Driver from the information input by a user.
  - `updateDriverInfo`: Updates an entry in Driver with the information input by a user.
  - `deleteDriverInfo`: Removes entry in Driver with the phone number or university id input by the user.
  - `enterParkingLotInfo`: Creates a new entry in ParkingLot from the information input by a user.
  - `updateParkingLotInfo`: Updates an entry in ParkingLot with the information input by a user.
  - `deleteParkingLotInfo`: Removes entry in ParkingLot with the lot name input by a user.
  - `enterZoneInfo`: Creates a new entry in Zone with a zone id and lot name input by a user.
  - `updateZoneInfo`: Updates an entry in Zone with a different zone id or lot names as input by a user.
  - `deleteZoneInfo`: Removes an entry from Zone with the zone id and lot name input by a user.
  - `enterSpaceInfo`: Creates a new entry in Space with the space number, zone id, and lot name input by a user.
  - `updateSpaceInfo`: Updates an entry in Space with the space number, zone id, and lot name with information as specified by the user.
  - `deleteSpaceInfo`: Remove an entry in Space with the space number, zone id, and lot name input by a user.
  - `updatePermitInfo`: Updates an entry in Permit with the information specified by a user.
  - `deletePermitInfo`: Removes and entry in Permit with the permit id specified by a user.
  - `assignTypeToSpace`: Given a space number, zone id, and lot name, update the space with the space type input by a user.
  - `updateCitationPayment`: Update the payment status of the given permit to paid.

- `src/MaintainPermits.java`: This file handles all functions and tasks related to handling permits and vehicle data.
  - `addPermit` : Takes permit data from the user and assigns permits to driver.
  - `addVehicle`: Creates a new vehicle in Vehicle table with information input from the user.
  - `updateVehicle`: Allows user to update the attributes for the given vehicle.
  - `DisplayGetVehicle`: Helper function for updateVehicle used to display all information for the given vehicle.
  - `updateVehicleOwnership`: Updates the owner for given vehicle.
  - `deleteVehicle`: Removes the vehicle and all related records from the database.
    
- `src/Reports.java`: This file handles report generations of different kinds for the Parking Database System.
   - `generateReportCitations`: Generates a report that includes the number of citations, the total number of vehicles to which citations were given, and the total fee
   - `totalCitationsCountByTimeRange`: Generates a report for the total number of citations given in all zones in a parking lot for a specified time range (e.g. 2020-01-01 to 2021-03-15)
   - `totalCitationsCountByMonth`: Generates a report for the total number of citations given in all zones in a parking lot for a given month
   - `totalCitationsCountByYear`: Generates a report for the total number of citations given in all zones in a parking lot for a given year
   - `listOfZones`: Lists all the parking zones
   - `carsInViolation`: Returns the number of cars that are currently in violation
   - `employeesHavePermits`: Returns the number of employees having permits for a given parking zone
   - `returnPermitInfo`: Returns permit information given a permit ID or phone number
   - `generateSpaceAvailable`: Returns an available space number given a space type in a given parking lot

## Design Decisions:

We have designed the application that starts with a Menu showing the various types of tasks that can be accomplished through this system. We have followed the 5 task design that includes the Information Processing Menu, Permits, and Vehicles Menu, Citations Menu, Reports Menu, Display Tables, and Exit. This allows us to encapsulate all tasks in their respective menu as described in the narrative, making testing and locating the needed tasks easier and simpler through CLI CLI-based menu. 

Selecting the different menus provides another menu to choose the tasks from and use them as required. This is accomplished by having modular code where we have separate Java files for each of the 5 tasks, where we just call functions in the main file. We have decided to implement transactions at several places where multiple tables need to be updated at once together to maintain completeness, and we are also checking for some constraints, and erroneous inputs in Java. 

We also make use of a ShutDownHook in Java that makes sure the connection to the database closes for graceful as well as erroneous shutdown of the application. Try catch and finally blocks at every stage to make sure the program outputs the behavior and doesn’t stop working abruptly.

## Functional Roles:

### Part 1:
- Software Engineer: Aadil (Prime), Daniel(Backup)
- Database Designer/Administrator: Manali(Prime), Ophelia(Backup)
- Application Programmer: Ophelia(Prime), Aadil (Backup)
- Test Plan Engineer: Daniel(Prime), Manali(Backup)

### Part 2:
- Software Engineer: Manali(Prime), Aadil(Backup)
- Database Designer/Administrator: Ophelia(Prime), Daniel(Backup)
- Application Programmer: Daniel(Prime), Ophelia(Backup)
- Test Plan Engineer: Aadil (Prime), Manali(Backup)

### Part 3:
- Software Engineer: Daniel(Prime), Ophelia(Backup)
- Database Designer/Administrator: Manali(Prime), Daniel(Backup)
- Application Programmer: Aadil (Prime), Manali(Backup)
- Test Plan Engineer: Ophelia(Prime), Aadil (Backup)














