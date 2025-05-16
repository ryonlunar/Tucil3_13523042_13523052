package app;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar extends BaseSearch {
    private PriorityQueue<State> openSet;
    private Set<State> closedSet;
    private State goalState;
    private String heuristic;

    public AStar(State initState, String heuristic) {
        super(initState);
        this.heuristic = heuristic;
        this.openSet = new PriorityQueue<>(Comparator.comparingInt(s -> s.cost + heuristic(s)));
        this.closedSet = new HashSet<>();
    }
    
    // Placeholder untuk heuristic GBFS yang akan diintegrasikan nanti
    private int heuristic(State state) {
        // Placeholder for future implementation (GBFS heuristic)
        return 0; // Sementara return 0 sebagai UCS
    }

    @Override
    public void search() {
        openSet.add(initState);
        System.out.println("DEBUG AStar: Memulai pencarian dengan heuristic " + heuristic);
        
        while(!openSet.isEmpty()) {
            State current = openSet.poll();
            visitedNodesCount++;
            
            if(current.isGoal(current.vehicles.get('P'))) {
                goalState = current;
                System.out.println("DEBUG AStar: Solusi ditemukan di node ke-" + visitedNodesCount);
                return;
            }
            
            processState(current);
        }
        System.out.println("DEBUG AStar: Pencarian selesai tanpa solusi");
    }

    @Override
    protected void processState(State current) {
        if(closedSet.contains(current)) return;
        
        closedSet.add(current);
        for(State successor : current.generateSucc()) {
            if(!closedSet.contains(successor)) {
                // Update cost dengan heuristic
                successor.cost = current.cost + 1 + heuristic(successor);
                openSet.add(successor);
            }
        }
    }

    public State getGoalState() {
        if(goalState == null) search();
        return goalState;
    }

    @Override
    public int getVisitedNodesCount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getVisitedNodesCount'");
    }
}
