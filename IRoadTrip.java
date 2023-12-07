import java.io.*;
import java.util.*;

/**
 * Finds the shortest path between two capitals, going through all countries capitals on the way.
 */
public class IRoadTrip {

    String bordersPath; // File location for the list of borders
    String capDistPath; // File location for the distances of the capitals
    String stateNamePath; // File path for county names
    HashMap<Integer, HashMap<Integer, Integer>> graph; // The graph of countries
    HashMap<String, Integer> translator; // Translates country strings to ID numbers
    HashMap<Integer, String> translateBack; // Translates country ID numbers to strings

    /**
     * Generates a graph of countries
     *
     * @param args three string arguments: File location for the list of borders,
     *             File location for the distances of the capitals,
     *             File path for county names
     */
    public IRoadTrip(String[] args) {
        bordersPath = args[0];
        capDistPath = args[1];
        stateNamePath = args[2];

        translator = buildTranslator(stateNamePath);
        translateBack = buildTranslateBack(translator);

        graph = graphBuilder();

        setDistances();
//        System.out.println(graph);
    }

    /**
     * Gets the shortest distance from 1 country to another using Dijkstra's Algorithm
     *
     * @param country1 origin country
     * @param country2 destination country
     * @return the distance from 1 country to the other, -1 if they are not connected
     */
    public int getDistance(String country1, String country2) {
        int start;
        int end;
        try {
            start = translator.get(country1); // Translate the country strings to country
            end = translator.get(country2);
        } catch (NullPointerException e) {
            return -1;
        }


        Map<Integer, Integer> distances = new HashMap<>(); // Hashmap to store distances
        Map<Integer, Integer> previous = new HashMap<>(); // Hashmap to store the previous country
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(); // Queue to store the next countries

        distances.put(start, 0); // Add the starting country to the queue
        priorityQueue.add(new Node(start, 0));

        while (!priorityQueue.isEmpty()) { // While there are nodes to check
            int current = priorityQueue.poll().country; // Set the current country

            if (current == end) {
                return distances.get(end); // Return the final distance
            }

            for (Map.Entry<Integer, Integer> neighborEntry : graph.getOrDefault(current, new HashMap<>()).entrySet()) { // for each neighbor
                int neighbor = neighborEntry.getKey(); // get its key
                int newDistance = distances.get(current) + neighborEntry.getValue(); // add its distance

                if (!distances.containsKey(neighbor) || newDistance < distances.get(neighbor)) { // if we have the key in distance or the distance is less than the neighbor's
                    distances.put(neighbor, newDistance); // add the distance
                    previous.put(neighbor, current); // Add it to previous countries
                    priorityQueue.add(new Node(neighbor, newDistance)); // add the node to the queue
                }
            }
        }
        return -1; // No connection
    }

    /**
     * Finds the shortest path from an origin country to a destination country, and tells you the countries on that path
     *
     * @param country1 Origin country
     * @param country2 Destination country
     * @return String list of all the counties on the shortest path
     */
    public List<String> findPath(String country1, String country2) {
        int start = translator.get(country1); // Translate the country strings to country
        int end = translator.get(country2);

        if (start == end) {
            List<String> path = new ArrayList<>();
            path.add(country1 + " --> " + country2 + " (0 km.)");
            return path;
        }

        Map<Integer, Integer> distances = new HashMap<>(); // Hashmap to store distances
        Map<Integer, Integer> previous = new HashMap<>();  // Hashmap to store the previous country
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(); // Queue to store the next countries

        distances.put(start, 0); // Add the starting country to the queue
        priorityQueue.add(new Node(start, 0));

        while (!priorityQueue.isEmpty()) { // While there are nodes to check
            int current = priorityQueue.poll().country; // Set the current country

            if (current == end) {
                return buildPath(previous, start, end); // Return the final path
            }

            for (Map.Entry<Integer, Integer> neighborEntry : graph.getOrDefault(current, new HashMap<>()).entrySet()) { // for each neighbor
                int neighbor = neighborEntry.getKey(); // get its key
                int newDistance = distances.get(current) + neighborEntry.getValue(); // add the distance

                if (!distances.containsKey(neighbor) || newDistance < distances.get(neighbor)) { // if we have the key in distance or the distance is less than the neighbor's
                    distances.put(neighbor, newDistance); // add the distance
                    previous.put(neighbor, current); // Add it to previous countries
                    priorityQueue.add(new Node(neighbor, newDistance)); // add the node to the queue
                }
            }
        }
        return Collections.emptyList(); // No connection
    }

    /**
     * Finds the countries on the route of the shortest path
     *
     * @param previous Map of the counties traveled through
     * @param start    Origin country
     * @param end      Destination country
     * @return String list of the path that taken
     */
    private List<String> buildPath(Map<Integer, Integer> previous, int start, int end) {
        List<String> path = new ArrayList<>();
        int current = end;

        while (current != start) { // Step back through the path and add the countries traveled through
            int prev = previous.get(current);
            path.add(translateBack.get(prev) + " --> " + translateBack.get(current) + " (" + graph.get(prev).get(current) + " km.)");
            current = prev;
        }
        Collections.reverse(path); // reverse the list to put it in the proper order
        return path;
    }

    /**
     * Takes input from the user to find paths
     */
    public void acceptUserInput() {
        Scanner scanner = new Scanner(System.in);
        List<String> output;
        while (true) { // Main loop
            System.out.println("Enter the name of the first country (type EXIT to quit):");
            String firstCountryName = scanner.nextLine().trim();

            if (firstCountryName.equalsIgnoreCase("EXIT")) {
                break; // Exit the loop if the user types EXIT
            }

            int firstCountryNumber;
            try { // Input validation
                firstCountryNumber = translator.get(firstCountryName);
            } catch (NullPointerException | IllegalArgumentException e) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue; // Retry if the country name is invalid
            }

            System.out.println("Enter the name of the second country (type EXIT to quit):");
            String secondCountryName = scanner.nextLine().trim();

            if (secondCountryName.equalsIgnoreCase("EXIT")) {
                break; // End the loop
            }

            int secondCountryNumber;
            try { // Input validation
                secondCountryNumber = translator.get(secondCountryName);
            } catch (NullPointerException | IllegalArgumentException e) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue;
            }

            if (secondCountryNumber == -1) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue;
            }

            output = findPath(firstCountryName, secondCountryName); // finding the path
            if (output.isEmpty()) {
                System.out.println("No path exists!");
            } else {
                for (String line : output) {
                    System.out.println(line);
                }
            }
        }
        System.out.println("Exiting program. Thank you!");
    }

    /**
     * Sets the weights (distances) on the graph
     */
    public void setDistances() {
        try (BufferedReader br = new BufferedReader(new FileReader(capDistPath))) { // open the file
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { // Skip the first line
                    isFirstLine = false;
                    continue; // Skip the header
                }

                String[] parts = line.split(","); // Break the csv down into lines

                int fromCountry = Integer.parseInt(parts[0]); // Set the values from the csv
                int toCountry = Integer.parseInt(parts[2]);
                int distance = Integer.parseInt(parts[4]);

                setDistance(fromCountry, toCountry, distance); // Set the weight in the graph
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method for setting weights in graph
     *
     * @param fromCountry Origin country
     * @param toCountry   Destination country
     * @param distance    Distance between them
     */
    private void setDistance(int fromCountry, int toCountry, int distance) {
        if (graph.containsKey(fromCountry) && graph.get(fromCountry).containsKey(toCountry)) { // If the countries are in the graph
            graph.get(fromCountry).put(toCountry, distance); // set the distance
        }
    }

    /**
     * Builds a hashmap that translates country string names to country ID numbers
     *
     * @param filePath // File location of the state name file
     * @return Hashmap that translates string names to country ID numbers
     */
    public static HashMap<String, Integer> buildTranslator(String filePath) {
        HashMap<String, Integer> translatorMap = new HashMap<>(); // Make the hashmap

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { // Open the file
            String line;

            while ((line = br.readLine()) != null) { // While there are still lines
                String[] data = line.split("\t"); // break the lines into parts

                if (data.length == 5 && data[4].equals("2020-12-31")) { // If the string name is the modern string name
                    String[] countryParts = data[2].split(","); // split by comma
                    String countryName = countryParts[0].replaceAll("\\(.*?\\)", "").split("/")[0].trim();
                    translatorMap.put(countryName, Integer.valueOf(data[0])); // Add it to the list
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Data cleaning, Fixing all the names in the borders file, this is what Veomett did in her implementation
        translatorMap.put("Democratic Republic of the Congo", 490);
        translatorMap.put("Republic of the Congo", 484);
        translatorMap.put("Germany", 260);
        translatorMap.put("Bahamas, The", 31);
        translatorMap.put("Burma", 775);
        translatorMap.put("Bosnia and Herzegovina", 346);
        translatorMap.put("Romania", 360);
        translatorMap.put("Cote d'Ivoire", 437);
        translatorMap.put("US", 2);
        translatorMap.put("Denmark (Greenland)", -2);
        translatorMap.put("North Korea", 731);
        translatorMap.put("Kyrgyzstan", 703);
        translatorMap.put("Congo, Democratic Republic of the", 490);
        translatorMap.put("Congo, Republic of the", 484);
        translatorMap.put("Eswatini", 572);
        translatorMap.put("Gambia, The", 420);
        translatorMap.put("Timor-Leste", 860);
        translatorMap.put("UK", 200);
        translatorMap.put("Korea, North", 731);
        translatorMap.put("South Korea", 732);
        translatorMap.put("Korea, South", 732);
        translatorMap.put("UAE", 696);
        translatorMap.put("Russia (Kaliningrad Oblast)", -3);
        translatorMap.put("The Gambia", 420);
        translatorMap.put("Turkey (Turkiye)", 640);
        translatorMap.put("United States", 2);
        translatorMap.put("Czechia", 316);
        translatorMap.put("Macedonia", 343);
        translatorMap.put("North Macedonia", 343);
        translatorMap.put("Spain (Ceuta)", 230);

        return translatorMap;
    }

    /**
     * Flips the translator map around to turn county IDs into string names
     *
     * @param originalMap the translator that translates String names into country ID's
     * @return a Hashmap for translating county IDs into string names
     */
    public HashMap<Integer, String> buildTranslateBack(HashMap<String, Integer> originalMap) {
        HashMap<Integer, String> flippedMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : originalMap.entrySet()) { // for each key
            flippedMap.put(entry.getValue(), entry.getKey()); // Put it flipped
        }
        return flippedMap;
    }

    /**
     * Builds the graph of countries
     *
     * @return Hashmap of the counties, with distances of 0
     */
    public HashMap<Integer, HashMap<Integer, Integer>> graphBuilder() { // for now, we default the distances to 0

        HashMap<Integer, HashMap<Integer, Integer>> countryGraph = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(bordersPath))) {
            String line;
            while ((line = br.readLine()) != null) { // go through the lines of the file
                String[] parts = line.split(" = "); // split by =
                String countryName = parts[0]; // set the country names

                HashMap<Integer, Integer> countryBorders = new HashMap<>();

                try { // see if they have a state name, if not we don't add
                    if (parts.length > 1 && parts[1].trim().length() > 0) { // if there is a border
                        int countryNumber = translator.get(countryName); // this is where the exception would be thrown
                        if (!countryGraph.containsKey(countryNumber)) { // If it's not already in the graph
                            String[] borders = parts[1].split("; "); // split by ;
                            for (String border : borders) { // add all the borders
                                String borderName = border.replaceAll("\\d.*", "").trim(); // split by that date
                                try {
                                    int borderNumber = translator.get(borderName); // get the border's ID
                                    countryBorders.put(borderNumber, 0); // add the border
                                } catch (Exception e) {
                                }
                            }
                            countryGraph.put(countryNumber, countryBorders); // add the country to the graph
                        }
                    } else { // add the borderless country
                        int temp = translator.get(countryName);
                        countryGraph.put(translator.get(countryName), countryBorders);
                    }
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Removing links to counties that do not have distances
        countryGraph.get(20).remove(-2);
        countryGraph.remove(340);
        countryGraph.get(346).remove(340);
        countryGraph.get(310).remove(340);
        countryGraph.get(355).remove(340);
        countryGraph.get(341).remove(340);
        countryGraph.get(344).remove(340);
        countryGraph.get(347).remove(340);
        countryGraph.get(359).remove(340);
        countryGraph.get(360).remove(340);
        countryGraph.get(343).remove(340);
        countryGraph.get(530).remove(626);
        countryGraph.get(290).remove(-3);
        countryGraph.get(339).remove(347);
        countryGraph.get(341).remove(347);
        countryGraph.get(347).remove(339);
        countryGraph.get(347).remove(341);
        countryGraph.get(625).remove(626);
        countryGraph.remove(626);
        countryGraph.get(482).remove(626);
        countryGraph.get(484).remove(490);
        countryGraph.get(490).remove(484);
        countryGraph.get(490).remove(626);
        countryGraph.get(500).remove(626);
        countryGraph.get(501).remove(626);
        countryGraph.get(390).remove(20);

        return countryGraph;
    }

    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }
}

/**
 * Makes a node for Dijkstra's Algorithm
 */
class Node implements Comparable<Node> {
    int country; // country ID number
    int distance; // distance to a given country

    /**
     * Creates a node
     *
     * @param country  country for the node to represent
     * @param distance Distance to a given country
     */
    public Node(int country, int distance) {
        this.country = country;
        this.distance = distance;
    }

    public String toString() {
        return "Node{" + "country=" + country + ", distance=" + distance + '}';
    }

    @Override
    public int compareTo(Node other) {
        // Compare nodes based on their distance


        if (other == null) {
            return 1;  // If we are comparing to null, the distance is greater
        }

        Integer thisDistance = this.distance;
        Integer otherDistance = other.distance;

        if (thisDistance == Integer.MAX_VALUE) {
            return 1; // nodes without a distance are larger
        } else if (otherDistance == Integer.MAX_VALUE) {
            return -1; // nodes without a distance are larger
        } else {
            return Integer.compare(thisDistance, otherDistance); // compare their distances
        }
    }
}

