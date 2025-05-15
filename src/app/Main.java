package app;

public class Main {
    public static void main(String[] args) {
        try {
            
            // Parse input file
            InputParser.Result result = InputParser.parse("app/test_input.txt");
            
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
                case "A*":
                    // Placeholder for future implementation
                    System.out.println("A* algorithm not implemented yet.");
                    break;
                default:
                    System.out.println("Unknown algorithm: " + result.algo);
            }

            long endTime = System.currentTimeMillis();

            if (goalState != null){
                System.out.println("Goal state found!");
                OutputHandler.printSolutionPath(goalState, startTime, endTime, visitedNodesCount);
            }else{
                System.out.println("no solusi ditemukan");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
