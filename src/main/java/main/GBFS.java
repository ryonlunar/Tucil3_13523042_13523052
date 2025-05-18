package main;

import java.util.*;

public class GBFS extends BaseSearch {
    public PriorityQueue<State> queue;
    public Set<State> visited;
    public State goalState;

    public GBFS(State initState) {
        super(initState);
        this.queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.heuristicCost));
        this.visited = new HashSet<>();
        this.goalState = null;
    }

    @Override
    public void search() {
        queue.add(initState);
        while (!queue.isEmpty()) {
            visitedNodesCount++;
            State current = queue.poll();
            if (current.isGoal(current.vehicles.get('P'))) {
                goalState = current;
                return;
            }
            processState(current);
        }
    }

    @Override
    public void processState(State currState) {
        if (visited.contains(currState)) {
            return;
        }
        visited.add(currState);

        // Generate successors
        List<State> succ = currState.generateSucc();
        for (State s : succ) {
            if (!visited.contains(s)) {
                queue.add(s);
            }
        }
    }

    public State getGoalState() {
        if (goalState == null)
            search();
        return goalState;
    }

    @Override
    public int getVisitedNodesCount() {
        return visitedNodesCount;
    }


}
