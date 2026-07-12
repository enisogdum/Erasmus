package ai.SlidingPuzzle;

import sac.graph.GraphSearchAlgorithm;
import sac.graph.AStar;
import sac.graph.GraphState;
import sac.StateFunction;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== EXPERIMENT 1: n = 3, 100 puzzles, 1000 mixing moves ===");
        SliddingPuzzle.nr = 3;
        runExperiment(100, 30);

        System.out.println("\n=== EXPERIMENT 2: n = 4, 10 puzzles, 30 mixing moves ===");
        SliddingPuzzle.nr = 4;
        runExperiment(10, 30);
    }

    private static void runExperiment(int numPuzzles, int mixingMoves) {
        StateFunction manhattan = new ManhattanDistanceHeuristic();
        StateFunction misplaced = new MisplacedTilesHeuristic();

        List<SliddingPuzzle> puzzles = new ArrayList<>();
        // 1. Generate puzzles
        for (int i = 0; i < numPuzzles; i++) {
            SliddingPuzzle p = new SliddingPuzzle();
            p.generateShuffledBoard(mixingMoves);
            puzzles.add(p);
        }

        // 2. Test Manhattan
        System.out.println("Testing Manhattan Heuristic...");
        SliddingPuzzle.setHFunction(manhattan);
        runHeuristicTest(puzzles);

        // 3. Test Misplaced Tiles
        System.out.println("Testing Misplaced Tiles Heuristic...");
        SliddingPuzzle.setHFunction(misplaced);
        runHeuristicTest(puzzles);
    }

    private static void runHeuristicTest(List<SliddingPuzzle> puzzles) {
        long totalTime = 0;
        long totalClosed = 0;
        long totalOpen = 0;
        long totalPathLength = 0;
        int solvedCount = 0;

        for (SliddingPuzzle puzzle : puzzles) {
            // Need a fresh copy for search so we don't carry over states if they mutate,
            // though AStar wrapper handles State internally without mutating the start state
            GraphSearchAlgorithm algorithm = new AStar(puzzle);
            algorithm.execute();

            List<GraphState> solutions = algorithm.getSolutions();
            if (!solutions.isEmpty()) {
                solvedCount++;
                GraphState solution = solutions.get(0);
                totalTime += algorithm.getDurationTime();
                totalClosed += algorithm.getClosedSet().size();
                totalOpen += algorithm.getOpenSet().size();
                totalPathLength += solution.getPath().size() - 1; // path includes start state
            }
        }

            System.out.printf("Average Time: %.2f ms\n", (double) totalTime / solvedCount);
            System.out.printf("Average Closed States: %.2f\n", (double) totalClosed / solvedCount);
            System.out.printf("Average Open States: %.2f\n", (double) totalOpen / solvedCount);
            System.out.printf("Average Path Length: %.2f\n", (double) totalPathLength / solvedCount);
            System.out.printf("solved: "+ solvedCount);
            
            
        
    }
}

