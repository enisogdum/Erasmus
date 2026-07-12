package ai.sudoku;

import sac.State;
import sac.StateFunction;

public class NaiveHeuristic extends StateFunction {
	@Override
	public double calculate(State state) {
		Sudoku s = (Sudoku) state;
		return s.getZerosCounter();
	}
}
