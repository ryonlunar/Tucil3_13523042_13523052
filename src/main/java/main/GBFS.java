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
            // System.out.println("DEBUG GBFS : Visit node #" + visitedNodesCount);
            visitedNodesCount++;
            State current = queue.poll();
            // System.out.println("Heuristic Cost: " + current.heuristicCost);
            if (current.isGoal(current.vehicles.get('P'))) {
                // handle solusi
                goalState = current;
                // System.out.println("DEBUG GBFS: GOAL STATE DITEMUKAN pada node #" + visitedNodesCount);
                return;
            }
            processState(current);
        }
    }

    @Override
    public void processState(State currState) {
        if (visited.contains(currState)) {
            // System.out.println("DEBUG GBFS: State sudah dikunjungi, skipping");
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
