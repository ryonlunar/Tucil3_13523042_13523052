package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GA {
    private static final int POPULATION_SIZE = 30;
    private static final double MUTATION_RATE = 0.1;
    private static final int MAX_GENERATIONS = 20; // Dikurangi dari 100
    private static final int STALL_GENERATIONS = 2; // Batas generasi 
    
    private State initState;
    
    public GA(State initState) {
        this.initState = initState;
    }
    
    public HeuristicSolution evolveHeuristic() {
        List<HeuristicIndividual> population = initializePopulation();
        evaluatePopulationFitness(population);
        
        // Tambahkan variabel untuk melacak perbaikan
        int lastImprovement = 0;
        int bestNodesVisited = Integer.MAX_VALUE;
        HeuristicSolution bestSolution = null;
        
        for(int gen=0; gen < MAX_GENERATIONS; gen++) {
            // Cek apakah ada perbaikan
            HeuristicSolution currentBest = getBestSolution(population);
            if (currentBest != null && currentBest.nodesVisited < bestNodesVisited) {
                bestNodesVisited = currentBest.nodesVisited;
                lastImprovement = gen;
                bestSolution = currentBest;
                // System.out.println("GA: Perbaikan pada generasi " + gen + ", nodes = " + bestNodesVisited);
            }
            
            // Berhenti jika tidak ada perbaikan dalam beberapa generasi
            if (gen - lastImprovement >= STALL_GENERATIONS) {
                // System.out.println("GA berhenti di generasi " + gen + " karena tidak ada perbaikan dalam " + 
                                //   STALL_GENERATIONS + " generasi terakhir");
                // print visited nodes count
                // System.out.println("GA: " + bestSolution.nodesVisited);
                            
                break;
            }
            
            population = createNewGeneration(population);
            evaluatePopulationFitness(population);
        }
        
        return bestSolution != null ? bestSolution : getBestSolution(population);
    }

    private List<HeuristicIndividual> initializePopulation() {
        List<HeuristicIndividual> population = new ArrayList<>();
        for(int i=0; i<POPULATION_SIZE; i++) {
            population.add(new HeuristicIndividual());
        }
        return population;
    }

    private void evaluatePopulationFitness(List<HeuristicIndividual> population) {
        population.forEach(ind -> {
            AStar astar = new AStar(initState.copy());
            astar.search();
            ind.setFitness(1.0 / (astar.getVisitedNodesCount() + 1));
            ind.setSolution(new HeuristicSolution(
                astar.getVisitedNodesCount(),
                astar.getGoalState()
            ));
        });
    }

    private List<HeuristicIndividual> createNewGeneration(List<HeuristicIndividual> population) {
        population.sort(Comparator.comparingDouble(ind -> ind.getFitness()));
        Collections.reverse(population);
        
        List<HeuristicIndividual> newPopulation = new ArrayList<>();
        newPopulation.add(population.get(0).copy());
        newPopulation.add(population.get(1).copy());
        
        while(newPopulation.size() < POPULATION_SIZE) {
            HeuristicIndividual parent1 = selectParent(population);
            HeuristicIndividual parent2 = selectParent(population);
            HeuristicIndividual child = crossover(parent1, parent2);
            mutate(child);
            newPopulation.add(child);
        }
        
        return newPopulation;
    }

    private HeuristicIndividual selectParent(List<HeuristicIndividual> population) {
        return population.stream()
            .sorted(Comparator.comparingDouble(HeuristicIndividual::getFitness).reversed())
            .limit(3)
            .findFirst()
            .get();
    }

    private HeuristicIndividual crossover(HeuristicIndividual p1, HeuristicIndividual p2) {
        HeuristicIndividual child = new HeuristicIndividual();
        p1.getHeuristics().forEach(h -> 
            child.setWeight(h, Math.random() < 0.5 ? p1.getWeight(h) : p2.getWeight(h))
        );
        return child;
    }

    private void mutate(HeuristicIndividual ind) {
        if(Math.random() < MUTATION_RATE) {
            String randomHeuristic = ind.getRandomHeuristic();
            ind.setWeight(randomHeuristic, Math.random());
        }
    }

    private HeuristicSolution getBestSolution(List<HeuristicIndividual> population) {
        return population.stream()
            .max(Comparator.comparingDouble(HeuristicIndividual::getFitness))
            .get()
            .getSolution();
    }

    public static class HeuristicIndividual {
        private Map<String, Double> weights;
        private double fitness;
        private HeuristicSolution solution;
        
        public HeuristicIndividual() {
            weights = new HashMap<>();
            weights.put("MANHATTAN", Math.random());
            weights.put("BLOCKED", Math.random());
            weights.put("COMBINEDMB", Math.random());
            weights.put("CHEBYSHEV", Math.random());
        }
        
        public HeuristicIndividual copy() {
            HeuristicIndividual copy = new HeuristicIndividual();
            copy.weights = new HashMap<>(this.weights);
            copy.fitness = this.fitness;
            copy.solution = this.solution;
            return copy;
        }

        // Getters and setters
        public double getWeight(String heuristic) { return weights.get(heuristic); }
        public void setWeight(String h, double w) { weights.put(h, w); }
        public List<String> getHeuristics() { return new ArrayList<>(weights.keySet()); }
        public String getRandomHeuristic() { 
            return getHeuristics().get((int)(Math.random()*weights.size())); 
        }
        public double getFitness() { return fitness; }
        public void setFitness(double fitness) { this.fitness = fitness; }
        public HeuristicSolution getSolution() { return solution; }
        public void setSolution(HeuristicSolution solution) { this.solution = solution; }
    }
    
    public static class HeuristicSolution {
        public final int nodesVisited;
        public final State solutionState;
        
        public HeuristicSolution(int nodes, State state) {
            nodesVisited = nodes;
            solutionState = state;
        }
    }
}
