package main.java;

public class Main {
    public static void main(String[] args) {
        try {

            // Parse input file
            InputParser.Result result = InputParser.parse("test/in/t1.txt");

            long startTime = System.currentTimeMillis();

            State goalState = null;
            int visitedNodesCount = 0;
            // Run the appropriate algorithm
            switch (result.algo) {
                case "UCS":
                    UCS ucs = new UCS(result.initState);
                    ucs.search();
                    goalState = ucs.getGoalState();
                    visitedNodesCount = ucs.getVisitedNodesCount();
                    break;
                case "GBFS":
                    // Placeholder for future implementation
                    System.out.println("GBFS algorithm not implemented yet.");
                    break;
                // Main.java (perubahan di switch case)
                case "A*":
                    AStar astar = new AStar(result.initState, result.heuristic);
                    astar.search();
                    goalState = astar.getGoalState();
                    visitedNodesCount = astar.getVisitedNodesCount();
                    break;
                default:
                    System.out.println("Unknown algorithm: " + result.algo);
            }

            long endTime = System.currentTimeMillis();

            if (goalState != null) {
                System.out.println("Goal state found!");
                OutputHandler.printSolutionPath(goalState, startTime, endTime, visitedNodesCount);
            } else {
                System.out.println("no solusi ditemukan");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
