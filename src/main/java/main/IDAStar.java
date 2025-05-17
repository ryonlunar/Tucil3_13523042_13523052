package main;
import java.util.Stack;

public class IDAStar extends BaseSearch {
    private int threshold;
    private State goalState;
    private String heuristicMethod;

    public IDAStar(State initState) {
        super(initState);
        this.threshold = initState.getHeuristicCost(initState.methode);
        this.heuristicMethod = initState.methode;
    }

    @Override
    public void search(){
        while (true){
            System.out.println("DEBUG IDA*: Threshold saat ini: " + threshold);
            int result = DLS(initState, 0);
            if (result == -1) {
                goalState = null;
                return;
            } else if (result == Integer.MIN_VALUE) {
                return;
            }
            threshold = result;
        }
    }

    private int DLS(State state, int g){
        visitedNodesCount++;
        int f = g + state.getHeuristicCost(heuristicMethod);

        if (f > threshold) return f;
        if (state.isGoal(state.vehicles.get('P'))){
            goalState = state;
            return Integer.MIN_VALUE;
        }

        int min = Integer.MAX_VALUE;
        for (State succ : state.generateSucc()){
            int res = DLS(succ, g+1);
            if (res == Integer.MIN_VALUE) return Integer.MIN_VALUE;
            if (res < min) min = res;
        }
        return min;
    }

    @Override
    protected void processState(State current) {
        // Tidak diperlukan untuk IDA*
    }
    public State getGoalState() {
        return goalState;
    }

    @Override
    public int getVisitedNodesCount() {
        return visitedNodesCount;
    }
}
