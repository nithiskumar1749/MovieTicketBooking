//THIS JAVA CODE FOR RUNNING MAIN FUNCTION FOR MOVIE TICKET BOOKING SYSTEM
package movies;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class MovieTicketSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);  

        try {
            System.out.println("Are you a user or admin? (user/admin)");
            String role = sc.nextLine().trim();  

            if (role.equalsIgnoreCase("admin")) {
                Admin admin = new Admin(null, null);

                if (admin.login()) { 
                    System.out.println("Admin logged in successfully!");
                } else {
                    System.out.println("Admin login failed.");
                }
            } else if (role.equalsIgnoreCase("user")) {
                LocationMovies.handleUserBooking(sc);  
            } else {
                System.out.println("Invalid role entered. Exiting.");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Error: No input found. Please ensure you provide the necessary input.");
        } finally {
            sc.close(); 
        }
    }
}
