package main;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar extends BaseSearch {
    private PriorityQueue<State> openSet;
    private Set<State> closedSet;
    private State goalState;

    public AStar(State initState) {
        super(initState);
        this.openSet = new PriorityQueue<>(Comparator.comparingInt(s -> s.cost + heuristic(s)));
        this.closedSet = new HashSet<>();
    }
    
    private int heuristic(State state) {
        return state.getHeuristicCost(state.methode);
    }

    @Override
    public void search() {
        openSet.add(initState);
        
        while(!openSet.isEmpty()) {
            State current = openSet.poll();
            visitedNodesCount++;
            
            if(current.isGoal(current.vehicles.get('P'))) {
                goalState = current;
                return;
            }
            
            processState(current);
        }
    }

    @Override
    protected void processState(State current) {
        if(closedSet.contains(current)) return;
        
        closedSet.add(current);
        for(State successor : current.generateSucc()) {
            if(!closedSet.contains(successor)) {
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
        return visitedNodesCount;
    }
}
