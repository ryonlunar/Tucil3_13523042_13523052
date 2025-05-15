package app;

import java.util.*;

public class UCS extends BaseSearch {
    public PriorityQueue<State> queue;
    public Set<State> visited;

    public UCS(State initState){
        super(initState);
        this.queue = new PriorityQueue<>(Comparator.comparingInt(s->s.cost));
        this.visited = new HashSet<>();
    }
    
    @Override
    public void search() {
        queue.add(initState);

        while (!queue.isEmpty()){
            State currState = queue.poll();
            visitedNodesCount++;

            if (currState.isGoal(currState.vehicles.get('P'))){
                // handle solusi
                return;
            }

            processState(currState);
        }
    }

    @Override
    protected void processState(State currState){
        if (visited.contains(currState)) return;
        visited.add(currState);

        //         // Generate successors akan diimplementasikan di step berikutnya
        List<State> succ = currState.generateSucc();

        for (State s : succ){
            if (!visited.contains(s)){
                queue.add(s);
            }
        }
    }

    public State getGoalState(){
        search();;

        State currState = null;

        for (State s : visited){
            if (s.isGoal(s.vehicles.get('P'))){
                currState = s;
                break;
            }
        }
        return currState;
    }

    @Override
    public int getVisitedNodesCount(){
        return visitedNodesCount;
    }
}
