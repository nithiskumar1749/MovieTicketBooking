package movies;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminFeatures {
    
    public void addMovie(Scanner sc) {
        System.out.print("Enter the location: ");
        String location = sc.nextLine().trim();
        if (location.isEmpty()) {
            System.out.println("Location cannot be empty.");
            return;
        }

        String locationFileName = location + ".txt";

        System.out.print("Enter the movie name to add: ");
        String movieName = sc.nextLine().trim();
        if (movieName.isEmpty()) {
            System.out.println("Movie name cannot be empty.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(locationFileName, true))) {
            writer.write(movieName);
            writer.newLine();

            File movieFile = new File(movieName + "_" + location + ".txt");
            movieFile.createNewFile(); 
        } catch (IOException e) {
            System.out.println("Error adding movie: " + e.getMessage());
        }
    }
    
    public void removeMovie(Scanner sc) {
        System.out.print("Enter the location: ");
        String location = sc.nextLine().trim();
        if (location.isEmpty()) {
            System.out.println("Location cannot be empty.");
            return;
        }

        String locationFileName = location + ".txt";

        System.out.print("Enter the movie name to remove: ");
        String movieName = sc.nextLine().trim();
        if (movieName.isEmpty()) {
            System.out.println("Movie name cannot be empty.");
            return;
        }

        try {
            List<String> movies = new ArrayList<>();
            boolean movieFound = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(locationFileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase(movieName)) {
                        movieFound = true;
                    } else {
                        movies.add(line);
                    }
                }
            }

            if (movieFound) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(locationFileName))) {
                    for (String movie : movies) {
                        writer.write(movie);
                        writer.newLine();
                    }
                }

                File movieFile = new File(movieName + "_" + location + ".txt");
                movieFile.delete(); 
            } else {
                System.out.println("Movie not found in the " + location + " list.");
            }

        } catch (IOException e) {
            System.out.println("Error removing movie: " + e.getMessage());
        }
    }

    public void addTheaterAndTimings(String movieName, String location, String theaterName, String[] timings) {
        if (movieName == null || theaterName == null || timings == null || timings.length == 0) {
            System.out.println("Invalid input. Movie name, location, theater name, and timings are required.");
            return;
        }

        String fileName = movieName + "_" + location + ".txt";
        File movieFile = new File(fileName);
        if (!movieFile.exists()) {
            System.out.println("Movie file does not exist: " + fileName);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(movieFile, true))) {
            writer.write(theaterName);
            for (String time : timings) {
                writer.write("," + time);
            }
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error adding theater and timings: " + e.getMessage());
        }
    }

    public void updateTheaterAndTimings(String movieName, String location, String theaterName, String[] newTimings) {
        if (movieName == null || theaterName == null || newTimings == null || newTimings.length == 0) {
            System.out.println("Invalid input. Movie name, location, theater name, and new timings are required.");
            return;
        }

        String fileName = movieName + "_" + location + ".txt";
        File movieFile = new File(fileName);
        if (!movieFile.exists()) {
            System.out.println("Movie file does not exist: " + fileName);
            return;
        }

        List<String> fileContent = new ArrayList<>();
        boolean theaterFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(movieFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(theaterName + ",")) {
                    theaterFound = true;
                    StringBuilder newEntry = new StringBuilder(theaterName);
                    for (String time : newTimings) {
                        newEntry.append(",").append(time);
                    }
                    fileContent.add(newEntry.toString());
                } else {
                    fileContent.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading movie file: " + e.getMessage());
            return;
        }

        if (theaterFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(movieFile))) {
                for (String entry : fileContent) {
                    writer.write(entry);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error updating movie file: " + e.getMessage());
            }
        } else {
            System.out.println("Theater not found in the movie file.");
        }
    }

    public void deleteTheater(String movieName, String location, String theaterName) {
        String fileName = movieName + "_" + location + ".txt";
        File movieFile = new File(fileName);
        if (!movieFile.exists()) {
            System.out.println("Movie file does not exist: " + fileName);
            return;
        }

        List<String> fileContent = new ArrayList<>();
        boolean theaterFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(movieFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(theaterName + ",")) {
                    theaterFound = true;
                } else {
                    fileContent.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading movie file: " + e.getMessage());
            return;
        }

        if (theaterFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(movieFile))) {
                for (String entry : fileContent) {
                    writer.write(entry);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error updating movie file: " + e.getMessage());
            }
        } else {
            System.out.println("Theater not found in the movie file.");
        }
    }
}
