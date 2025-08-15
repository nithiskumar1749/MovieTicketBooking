//THIS JAVA CODE HANDLES UPI PAYMENTS BY VERIFYING THE UPI ID AND PASSWORD, CHECKING ACCOUNT BALANCE, AND DEDUCTING THE PAYMENT AMOUNT IF SUFFICIENT FUNDS ARE AVAILABLE.
package movies;

import java.io.*;
import java.util.Scanner;

public class UPIPayment {

    public static boolean processUPIPayment(Scanner sc, double totalAmount) {
        String upiID;
        String password;

        while (true) {
            System.out.print("Enter your UPI ID for payment: ");
            upiID = sc.nextLine().trim();

            if (!isValidUPI(upiID)) {
                System.out.println("UPI ID not found. Type '1' to try again or '2' to cancel.");
                String userChoice = sc.nextLine().trim();

                if (userChoice.equalsIgnoreCase("2")) {
                    return false; 
                } else if (userChoice.equalsIgnoreCase("1")) {
                    continue; // Restart the loop to enter UPI ID again
                } else {
                    System.out.println("Invalid choice. Please enter '1' or '2'.");
                    continue; 
                }
            }

            System.out.print("Enter your password for UPI ID: ");
            password = sc.nextLine().trim();
            if (!isPasswordCorrect(upiID, password)) {
                System.out.println("Incorrect password. Please try again.");
                continue; 
            }

            
            if (deductAmountFromUPI(upiID, totalAmount)) {
                System.out.println("Payment of " + totalAmount + " from UPI ID " + upiID + " successful.");
                return true; 
            } else {
                System.out.println("Payment failed due to insufficient balance. Please top up your account.");
                return false; 
            }
        }
    }

    private static boolean isValidUPI(String upiID) {
        File upiFile = new File("upi.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(upiFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] upiInfo = line.split(",");
                if (upiInfo[0].trim().equalsIgnoreCase(upiID)) {
                    return true; 
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading UPI file: " + e.getMessage());
        }
        return false; 
    }

    private static boolean isPasswordCorrect(String upiID, String password) {
        File upiFile = new File("upi.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(upiFile))) {
            String line;
            while ((line = reader.readLine()) != null){
                String[] upiInfo = line.split(",");
                if (upiInfo[0].trim().equalsIgnoreCase(upiID)) {
                    return upiInfo[1].trim().equals(password); 
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading UPI file: " + e.getMessage());
        }
        return false; 
    }

    private static boolean deductAmountFromUPI(String upiID, double totalAmount) {
        File upiFile = new File("upi.txt");
        StringBuilder updatedData = new StringBuilder();
        boolean upiFound = false;
        double currentBalance = 0.0;

        // Read UPI details from upi.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(upiFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] upiInfo = line.split(",");
                if (upiInfo[0].trim().equalsIgnoreCase(upiID)) {
                    upiFound = true;
                    currentBalance = Double.parseDouble(upiInfo[2].trim()); // Get the balance

                    
                    if (currentBalance < totalAmount) {
                        System.out.println("Insufficient balance. Please top up your account.");
                        return false;
                    }

                    
                    double updatedBalance = currentBalance - totalAmount;
                    updatedData.append(upiID).append(",").append(upiInfo[1]).append(",").append(updatedBalance).append("\n");
                } else {
                    updatedData.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading UPI file: " + e.getMessage());
            return false;
        }

        // Write updated balance back to file if UPI ID was found
        if (upiFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(upiFile))) {
                writer.write(updatedData.toString());
            } catch (IOException e) {
                System.out.println("Error updating UPI file: " + e.getMessage());
                return false;
            } 
            return true;
        }

        System.out.println("UPI ID not found.");
        return false;
    }
}
