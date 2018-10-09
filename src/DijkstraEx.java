import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class DijkstraEx {

    private static List<Place> vertex;
    private static List<Brink> brinks;

    public static void main(String[] args) throws IOException {

        vertex = new ArrayList<>();
        brinks = new ArrayList<>();

        @SuppressWarnings("algorithm")
        Scanner scanner = new Scanner(System.in);
        System.out.println(" Enter the name of input file : ");

        String dijkstra = new java.io.File(".").getCanonicalPath() + "\\src\\" + scanner.nextLine();

        File txt = new File(dijkstra);

        String bestPlace = null;
        int edgeCosts = Integer.MAX_VALUE;

        try {
            scanner = new Scanner(txt);

            scanner.nextInt();

            ArrayList<Integer> inputs = new ArrayList<>();
            ArrayList<Integer> cities = new ArrayList<>();

            while (scanner.hasNextInt())    {
                inputs.add(scanner.nextInt());
            }

            for (int i = 0; i < inputs.size(); i++) {
                if (i % 3 == 0 || i%3 == 1) {
                    if (!cities.contains(inputs.get(i))) {
                        cities.add(inputs.get(i));
                    }
                }
            }
            for (int i = 0; i < cities.size(); i++) {
                int j = i+1;
                Place place = new Place("City(" + j +")", "City(" + j +")");
                vertex.add(place);
                System.out.println(place.toString());
            }
            int edgeCounter = 0;

            for (int i = 0; i < inputs.size(); i=i+3)  {
                addLine("Edge_" + edgeCounter, inputs.get(i), inputs.get(i+1),inputs.get(i+2));
                edgeCounter++;
            }

//            for (Brink edge1 : brinks) {
//                System.out.println("Edges:" + edge1);
//            }

            Dijkstra graph = new Dijkstra(vertex, brinks);
            Algorithm dijkstraAlgorithm = new Algorithm(graph);

            for (int i = 0; i < vertex.size(); i++) {
                int totalEdgeWeights = 0;
                for (int j = 0; j < vertex.size(); j++) {
                    dijkstraAlgorithm.execute(vertex.get(i));
                    LinkedList<Place> path = dijkstraAlgorithm.getPath(vertex.get(j));

                    if (i !=j) {
                        for (int k = 0; k < path.size() - 1; k++) {
                            for (Brink edge : brinks) {

                                if (edge.getSource().equals(path.get(k)) && edge.getDestination().equals(path.get(k + 1))) {
                                    totalEdgeWeights += edge.getWeight();
                                }
                            }
                        }
                    }
                }
                if (edgeCosts>totalEdgeWeights)  {
                    edgeCosts = totalEdgeWeights;
                    bestPlace = vertex.get(i).toString();
                }
            }
            System.out.println("\nThe optimal city is "+bestPlace +" to install headquarters ");

        }catch (FileNotFoundException e)    {
            e.printStackTrace();
        }
    }

    private static void addLine(String laneId, int sourceLocNo, int destLocNo, int distance) {
        Brink edge = new Brink(laneId, vertex.get(sourceLocNo-1), vertex.get(destLocNo-1), distance);
        Brink edgeTurnBack = new Brink(laneId, vertex.get(destLocNo-1), vertex.get(sourceLocNo-1), distance);
        brinks.add(edge);
        brinks.add(edgeTurnBack);
    }
    static class Dijkstra {
        private final List<Place> cities;
        private static List<Brink> brinks;

        public Dijkstra(List<Place> cities, List<Brink> brinks) {
            this.cities = cities;
            Dijkstra.brinks = brinks;
        }

        /**
         * @return the brinks
         */
        public static List<Brink> getEdges() {
            return brinks;
        }

        /**
         * @param brinks the brinks to set
         */
        public static void setEdges(List<Brink> brinks) {
            Dijkstra.brinks = brinks;
        }

        /**
         * @return the cities
         */
        public List<Place> getCities() {
            return cities;
        }
    }
    static class Brink {
        /******
         * This class to initializing the brinks between Cities.
         ******/
        private final String id;
        private final Place source, destination;
        private final int weight;

        public Brink(String id, Place source, Place destination, int weight)	{
            this.id = id;
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }


        /**
         * @return the id
         */
        public String getId() {
            return id;
        }


        /**
         * @return the source
         */
        public Place getSource() {
            return source;
        }


        /**
         * @return the destination
         */
        public Place getDestination() {
            return destination;
        }


        /**
         * @return the weight
         */
        public int getWeight() {
            return weight;
        }

        @Override
        public String toString()	{
            return source + " " + destination;
        }

    }
    static class Algorithm {
        private List<Place> cities;
        private List<Brink> brinks;
        private Set<Place> settledCities, unSettledCities;
        private Map<Place, Place> predecessors;
        private Map<Place, Integer> distances;

        public Algorithm(Dijkstra graph) {

            this.cities = new ArrayList<Place>(graph.getCities());
            this.brinks = new ArrayList<Brink>(Dijkstra.getEdges());
        }

        public void execute	(Place source) {
            settledCities = new HashSet<>();
            unSettledCities = new HashSet<>();
            distances = new HashMap<>();
            predecessors = new HashMap<>();
            distances.put(source, 0);
            unSettledCities.add(source);
            while (unSettledCities.size() > 0) {
                Place node = getMinimum(unSettledCities);
                settledCities.add(node);
                unSettledCities.remove(node);
                findMinimalDistances(node);
            }

        }

        private void findMinimalDistances(Place node) {
            List<Place> adjacentCities = getNeighbors(node);
            for (Place target : adjacentCities) {
                if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                    distances.put(target, getShortestDistance(node) + getDistance(node, target));
                    predecessors.put(target, node);
                    unSettledCities.add(target);
                }
            }
        }

        private int getDistance(Place node, Place target) {
            for (Brink edge : brinks) {
                if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
                    return edge.getWeight();
                }
            }
            throw new RuntimeException("That Should Not Happen!");
        }

        private int getShortestDistance(Place destination) {
            Integer d = distances.get(destination);
            if (d == null) {
                return Integer.MAX_VALUE;
            } else return d;
        }

        private List<Place> getNeighbors(Place node) {
            List<Place> neighbors = new ArrayList<>();
            for (Brink edge : brinks)
                if ((edge.getSource().equals(node))&& !isSettled(edge.getDestination())) {
                    neighbors.add(edge.getDestination());
                }
            return neighbors;
        }

        private boolean isSettled(Place city) {
            return settledCities.contains(city);
        }

        private Place getMinimum(Set<Place> cities) {
            Place minimum = null;
            for (Place city : cities) {
                if (minimum == null) {
                    minimum = city;
                } else	{
                    if (getShortestDistance(city) < getShortestDistance(minimum)) {
                        minimum = city;
                    }
                }
            }
            return minimum;    }

        /**********
         * This method returns the path from the source to the selected target and
         * NULL if no path exists
         **********/

        public LinkedList<Place> getPath(Place target) {
            LinkedList<Place> path = new LinkedList<>();
            Place step = target;
            // check if a path exists
            if (predecessors.get(step) == null) {
                return null;
            }
            path.add(step);
            while (predecessors.get(step) != null) {
                step = predecessors.get(step);
                path.add(step);
            }
            // Put it into the correct order
            Collections.reverse(path);
            return path;
        }
    }
    static class Place {

        /*********
         * Each Place has a id and a name.
         ********/

        final private String id, name;

        /********
         * Initializing a Place.
         **********/

        public Place(String id, String name)	{
            this.id = id;
            this.name = name;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Place other = (Place) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            return true;
        }

        @Override
        public String toString()    {
            return getName();
        }
    }
}