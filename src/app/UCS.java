package app;

import java.util.*;

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
        System.out.println("DEBUG UCS: Pencarian dimulai dengan initial state");
        System.out.println("DEBUG UCS: Primary position: (" + initState.vehicles.get('P').row + ","
                + initState.vehicles.get('P').col + ")");
        System.out.println("DEBUG UCS: Exit position: (" + initState.exitRow + "," + initState.exitCol + ")");
        while (!queue.isEmpty()) {
            State currState = queue.poll();
            visitedNodesCount++;
            if (visitedNodesCount % 1000 == 0) {
                System.out.println("DEBUG UCS: Nodes visited: " + visitedNodesCount + ", Queue size: " + queue.size());
            }

            // Debug pengecekan goal state setiap 100 node
            if (visitedNodesCount % 100 == 0) {
                Vehicle primary = currState.vehicles.get('P');
                System.out.println("DEBUG UCS: Node #" + visitedNodesCount + " - Primary at (" +
                        primary.row + "," + primary.col + "), Cost: " + currState.cost);
            }

            if (currState.isGoal(currState.vehicles.get('P'))) {
                // handle solusi
                goalState = currState;
                System.out.println("DEBUG UCS: GOAL STATE DITEMUKAN pada node #" + visitedNodesCount);
                return;
            }

            processState(currState);
        }
        System.out.println("DEBUG UCS: Pencarian selesai tanpa menemukan solusi. Total nodes: " + visitedNodesCount);
    }

    @Override
    protected void processState(State currState) {
        if (visited.contains(currState)) {
            System.out.println("DEBUG UCS: State sudah dikunjungi, skipping");
            return;
        }
        visited.add(currState);

        // // Generate successors akan diimplementasikan di step berikutnya
        List<State> succ = currState.generateSucc();
        if (visitedNodesCount % 1000 == 0) {
            System.out.println("DEBUG UCS: Generated " + succ.size() + " successors for node #" + visitedNodesCount);
        }
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
