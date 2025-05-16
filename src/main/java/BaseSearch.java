package main.java;
import java.util.*;


public abstract class BaseSearch implements SearchAlgorithm  {
    public State initState;
    public int visitedNodesCount;

    public BaseSearch(State initState){
        this.initState = initState;
    }

    protected abstract void processState(State current);
}
