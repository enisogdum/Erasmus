package ai.SlidingPuzzle;
import sac.State;
import sac.StateFunction;

public class ManhattanDistanceHeuristic extends StateFunction {
    public double calculate(State state){
        SliddingPuzzle puzzle = (SliddingPuzzle) state;
        return puzzle.getDistanceAllPieces();
    }
} 