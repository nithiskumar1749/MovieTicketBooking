//THIS JAVA CODE DEFINES A MOVIE TICKET BOOKING SYSTEM THAT ALLOWS USERS TO VIEW MOVIES BY LOCATION, SELECT A MOVIE, THEATER, DATE AND SEATS. 
//IT INCLUDES FUNCTIONALITY FOR DISPLAYING SHOWTIMES, SEATING LAYOUT, AND UPI PAYMENT PROCESSING.
package movies;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LocationMovies {
    
    private static final double TICKET_PRICE = 200.0; 

    // Method to handle user booking
    public static void handleUserBooking(Scanner sc) {
        String location;

        while (true) {
            System.out.print("Enter your City: ");
            location = sc.nextLine().trim();
            if (displayMoviesForLocation(location)) {
                break;
            } else {
                System.out.println("City not found. Please try again.");
            }
        }

        while (true) {
            System.out.println("Do you want to book a ticket or exit? (book/exit)");
            String action = sc.nextLine().trim().toLowerCase();

            if (action.equals("exit")) {
                System.out.println("Exiting...");
                break;
            } else if (action.equals("book")) {
                System.out.print("Enter the name of the movie you want to book: ");
                String movieName = sc.nextLine().trim();

                if (isMovieAvailable(location, movieName)) {
                    displayMovieDetails(location, movieName, sc);
                    return; 
                } else {
                    System.out.println("Movie not found in " + location + ".");
                }
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static boolean displayMoviesForLocation(String location) {
        String fileName = location + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            System.out.println("Movies available in " + location + ":");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error: File for location '" + location + "' not found.");
            return false;
        }
    }

    private static boolean isMovieAvailable(String location, String movieName) {
        String fileName = location + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(movieName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file for location " + location + ": " + e.getMessage());
        }
        return false;
    }

    private static void displayMovieDetails(String location, String movieName, Scanner sc) {
        String fileName = movieName + "_" + location + ".txt";
        List<String> theaters = new ArrayList<>();

        // Get upcoming dates
        List<String> upcomingDates = getUpcomingDates();

        // Display available theaters
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            System.out.println("Available theaters for " + movieName + ":");
            int serialNumber = 1;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                String theaterName = details[0].trim();
                theaters.add(line);
                System.out.println(serialNumber + ". " + theaterName);
                serialNumber++;
            }
        } catch (IOException e) {
            System.out.println("Error reading file for movie " + movieName + " in location " + location + ": " + e.getMessage());
            return;
        }

        // Ask the user to choose a date
        System.out.println("Please select a date for your booking:");
        for (int i = 0; i < upcomingDates.size(); i++) {
            System.out.println((i + 1) + ". " + upcomingDates.get(i));
        }

        int dateChoice;
        while (true) {
            System.out.print("Choose a date (1-" + upcomingDates.size() + "): ");
            try {
                dateChoice = Integer.parseInt(sc.nextLine().trim());
                if (dateChoice >= 1 && dateChoice <= upcomingDates.size()) {
                    break; // Valid choice made
                } else {
                    System.out.println("Invalid date choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        String selectedDate = upcomingDates.get(dateChoice - 1);
        System.out.println("You selected the date: " + selectedDate);

        // Ask the user to choose a theater
        while (true) {
            System.out.print("Choose a theater by entering the serial number: ");
            int theaterChoice;
            try {
                theaterChoice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                continue;
            }

            if (theaterChoice > 0 && theaterChoice <= theaters.size()) {
                String selectedTheater = theaters.get(theaterChoice - 1);
                String[] details = selectedTheater.split(",", 2);
                System.out.println("Available showtimes for " + details[0].trim() + ":");

                if (details.length > 1) {
                    String showtimes = details[1].trim();
                    System.out.println(showtimes);

                    String chosenShowtime;
                    while (true) {
                        System.out.print("Enter the desired showtime or type 'exit' to cancel: ");
                        chosenShowtime = sc.nextLine().trim();

                        if (chosenShowtime.equalsIgnoreCase("exit")) {
                            System.out.println("Booking cancelled.");
                            return;
                        } else if (showtimes.contains(chosenShowtime)) {
                            if (selectSeats(movieName, location, details[0].trim(), chosenShowtime, selectedDate, sc)) {
                                return;
                            } else {
                                break;
                            }
                        } else {
                            System.out.println("Invalid showtime. Please try again or type 'exit' to cancel.");
                        }
                    }
                } else {
                    System.out.println("No showtimes available.");
                    return;
                }
            } else {
                System.out.println("Invalid theater choice. Please try again.");
            }
        }
    }

    private static List<String> getUpcomingDates() {
        List<String> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy"); // Format for dates
        for (int i = 0; i < 3; i++) {
            dates.add(today.plusDays(i).format(formatter));
        }
        return dates; 
    }

    private static boolean selectSeats(String movieName, String location, String theaterName, String showtime, String date, Scanner sc) {
        // Define seat layout (10 rows x 26 columns)
        char[][] seats = new char[10][26];
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                seats[i][j] = 'O'; 
            }
        }

        // Load booked seats from a file if it exists
        String seatFileName = movieName + "_" + theaterName + "_" + date.replace(" ", "") + "_" + showtime.replace(":", "") + ".txt";
        try (BufferedReader seatReader = new BufferedReader(new FileReader(seatFileName))) {
            String line;
            int row = 0;
            while ((line = seatReader.readLine()) != null && row < seats.length) {
                seats[row] = line.toCharArray();
                row++;
            }
        } catch (IOException e) {
            System.out.println("No previous seat bookings found. All seats are available.");
        }

        
        int numSeats;
        while (true) {
            System.out.print("Enter the number of seats you want to book: ");
            try {
                numSeats = Integer.parseInt(sc.nextLine().trim());
                if (numSeats > 0) {
                    break;
                } else {
                    System.out.println("Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        
        System.out.println("\n\t\t\tSCREEN THIS WAY\n");
        System.out.print("   ");
        for (int i = 1; i <= seats[0].length; i++) {
            System.out.printf("%3d", i);
        }
        System.out.println();

        for (int i = 0; i < seats.length; i++) {
            System.out.print((char) ('A' + i) + " ");
            for (int j = 0; j < seats[i].length; j++) {
                System.out.printf("%3c", seats[i][j]);
            }
            System.out.println();
        }

        // Loop to ensure correct seat selection
        List<String> bookedSeats = new ArrayList<>();
        while (true) {
            System.out.print("Enter the seat numbers you want to book (e.g., A1, B2): ");
            String[] selectedSeats = sc.nextLine().trim().split(",");

            if (selectedSeats.length == numSeats) {
                boolean validSelection = true;
                for (String seat : selectedSeats) {
                    seat = seat.trim().toUpperCase();
                    int row = seat.charAt(0) - 'A';
                    int col = Integer.parseInt(seat.substring(1)) - 1;

                    // Check if seat is valid and available
                    if (row >= 0 && row < seats.length && col >= 0 && col < seats[row].length && seats[row][col] == 'O') {
                        bookedSeats.add(seat);
                        seats[row][col] = 'X'; // Mark seat as booked
                    } else {
                        validSelection = false;
                        System.out.println("Seat " + seat + " is not valid or already booked.");
                        break;
                    }
                }

                if (validSelection) {
                    break;
                }
            } else {
                System.out.println("Please enter exactly " + numSeats + " seat numbers.");
            }
        }

        // Save the booking to a file
        try (PrintWriter seatWriter = new PrintWriter(new FileWriter(seatFileName))) {
            for (int i = 0; i < seats.length; i++) {
                seatWriter.println(seats[i]);
            }
        } catch (IOException e) {
            System.out.println("Error saving seat bookings.");
        }

        System.out.println("You have successfully booked seats: " + String.join(", ", bookedSeats));
        double totalPrice = bookedSeats.size() * TICKET_PRICE;
        System.out.println("Total price: " + totalPrice + " INR");
        return UPIPayment.processUPIPayment(sc, totalPrice);
    }
}
