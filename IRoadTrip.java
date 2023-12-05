import java.io.*;
import java.util.*;

public class IRoadTrip {

    HashMap<String, HashMap<String, Integer>> graph;


    public IRoadTrip (String [] args) {
        // Replace with your code
        graph = new HashMap<>();
        // generate the map by running a bunch of methods
    }


    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
        // Replace with your code
        return null;
    }


    public void acceptUserInput() {
        // Replace with your code
        System.out.println("IRoadTrip - skeleton");
    }

    public static HashMap<String, Integer> buildTranslator(String filePath){
        HashMap<String, Integer> translatorMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Read each line from the TSV file
            while ((line = br.readLine()) != null) {
                // Split the line using tab as a delimiter
                String[] data = line.split("\t");

                // Extract everything before the comma, exclude anything in parentheses,
                // and exclude the part after the slash
                if (data.length == 5 && data[4].equals("2020-12-31")) {
                    String[] countryParts = data[2].split(",");
                    String countryName = countryParts[0].replaceAll("\\(.*?\\)", "").split("/")[0].trim();
                    // Add the extracted country name to the list
                    translatorMap.put(countryName, Integer.valueOf(data[0]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return translatorMap;
    }


    public static String[] extractStateName(String filePath) {
        ArrayList<String> countriesList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Read each line from the TSV file
            while ((line = br.readLine()) != null) {
                // Split the line using tab as a delimiter
                String[] data = line.split("\t");

                // Extract everything before the comma, exclude anything in parentheses,
                // and exclude the part after the slash
                if (data.length == 5 && data[4].equals("2020-12-31")) {
                    String[] countryParts = data[2].split(",");
                    String countryName = countryParts[0].replaceAll("\\(.*?\\)", "").split("/")[0].trim();
                    // Add the extracted country name to the list
                    countriesList.add(countryName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the list to an array
        String[] countryArray = new String[countriesList.size()];
        countryArray = countriesList.toArray(countryArray);

        return countryArray;
    }

    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();

        String filePath = "state_name.tsv";
        String[] countryArray = extractStateName(filePath);

        // Print the extracted country names
        System.out.println("Countries with end date as 2020-12-31:");
        for (String country : countryArray) {
            System.out.println(country);
        }
    }

}

