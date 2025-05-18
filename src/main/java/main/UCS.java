package main;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class UCS extends BaseSearch {
    public PriorityQueue<State> queue;
    public Set<State> visited;
    public State goalState;

    public UCS(State initState) {
        super(initState);
        this.queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.cost));
        this.visited = new HashSet<>();
        this.goalState = null;
    }

    @Override
    public void search() {
        queue.add(initState);
        while (!queue.isEmpty()) {
            State currState = queue.poll();
            visitedNodesCount++;

            if (currState.isGoal(currState.vehicles.get('P'))) {
                goalState = currState;
                return;
            }

            processState(currState);
        }
    }

    @Override
    protected void processState(State currState) {
        if (visited.contains(currState)) {
            return;
        }
        visited.add(currState);

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
