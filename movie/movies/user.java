//THIS JAVA CODE DEFINES AN ADMIN CLASS FOR A MOVIE MANAGEMENT SYSTEM, EXTENDING AN ABSTRACT USER CLASS. 
//IT PROVIDES METHODS FOR ADMIN LOGIN, WITH CREDENTIALS CHECKED FROM A FILE. 
//ONCE LOGGED IN, THE ADMIN CAN ACCESS A MENU TO:
//ADD OR REMOVE MOVIES, MANAGE THEATERS AND SHOWTIMES, UPDATE OR DELETE THEATERS, EXIT THE MENU
package movies;

import java.io.*;
import java.util.Scanner;

abstract class User {
    String username;
    String password;

    abstract boolean login();
}

class Admin extends User {
    
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    boolean login() {
        return authenticateAdmin();
    }

    // Method for handling Admin login process
    private boolean authenticateAdmin() {
        try (Scanner sc = new Scanner(System.in)) {
            int attempts = 3;

            while (attempts > 0) {
                System.out.print("Enter username: ");
                String user = sc.nextLine();

                System.out.print("Enter password: ");
                String pass = sc.nextLine();

                if (validateAdmin(user, pass)) {
                    System.out.println("Login successful!");
                    displayAdminMenu();
                    return true;
                } else {
                    attempts--;
                    System.out.println("Incorrect credentials. Remaining attempts: " + attempts);
                    if (attempts > 0) {
                        System.out.println("1. Retry");
                        System.out.println("2. Forgot password");
                        int choice = sc.nextInt();
                        sc.nextLine(); 

                        if (choice == 2) {
                            System.out.println("Contact the head office.");
                            return false;
                        }
                    }
                }
            }

            System.out.println("Login failed after 3 attempts.");
            return false;

        } catch (IOException e) {
            System.out.println("An error occurred while trying to authenticate: " + e.getMessage());
            return false;
        }
    }

    // Method to validate admin credentials by reading from the file
    private boolean validateAdmin(String username, String password) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("admin.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

private void displayAdminMenu() {
    try (Scanner sc = new Scanner(System.in)) {
        AdminFeatures adminFeatures = new AdminFeatures();
        int option = -1;

        while (option != 6) { 
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Movie");
            System.out.println("2. Remove Movie");
            System.out.println("3. Add Theater and Showtimes");
            System.out.println("4. Update Theater and Showtimes");
            System.out.println("5. Delete Theater");
            System.out.println("6. Exit"); 
            System.out.print("Choose an option: ");

            if (sc.hasNextInt()) {
                option = sc.nextInt();
                sc.nextLine(); 

                switch (option) {
                    case 1:
                        System.out.println("Adding a movie:");
                        adminFeatures.addMovie(sc);
                        break;
                    case 2:
                        System.out.println("Removing a movie:");
                        adminFeatures.removeMovie(sc);
                        break;
                    case 3:
                        System.out.print("Enter the location: ");
                        String locationForAdd = sc.nextLine().trim();
                        System.out.print("Enter the movie name: ");
                        String movieNameForTheater = sc.nextLine().trim();
                        System.out.print("Enter the theater name: ");
                        String theaterName = sc.nextLine().trim();
                        System.out.print("Enter showtimes (comma-separated): ");
                        String[] timings = sc.nextLine().trim().split(",");
                        adminFeatures.addTheaterAndTimings(movieNameForTheater, locationForAdd, theaterName, timings);
                        break;
                    case 4:
                        System.out.print("Enter the location: ");
                        String locationForUpdate = sc.nextLine().trim();
                        System.out.print("Enter the movie name: ");
                        String movieNameForUpdate = sc.nextLine().trim();
                        System.out.print("Enter the theater name to update: ");
                        String theaterNameToUpdate = sc.nextLine().trim();
                        System.out.print("Enter new showtimes (comma-separated): ");
                        String[] newTimings = sc.nextLine().trim().split(",");
                        adminFeatures.updateTheaterAndTimings(movieNameForUpdate, locationForUpdate, theaterNameToUpdate, newTimings);
                        break;
                    case 5: // New case for deleting a theater
                        System.out.print("Enter the location: ");
                        String locationForDelete = sc.nextLine().trim();
                        System.out.print("Enter the movie name: ");
                        String movieNameForDelete = sc.nextLine().trim();
                        System.out.print("Enter the theater name to delete: ");
                        String theaterNameToDelete = sc.nextLine().trim();
                        adminFeatures.deleteTheater(movieNameForDelete, locationForDelete, theaterNameToDelete);
                        break;
                    case 6: // Exit option
                        System.out.println("Exiting Admin Menu.");
                        break;
                    default:
                        System.out.println("Please enter a valid option between 1 and 6."); 
                }
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                sc.nextLine(); 
            }
        }
    }
}

}