package ai.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.IdentifierType;
import sac.graph.BestFirstSearch;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

public class Sudoku extends GraphStateImpl {
	public static final int n = 3;
	public static final int n2 = n * n;
	
	private byte[][] board;
	private int zerosCounter = n2 * n2;
	
	public Sudoku() {
		board = new byte[n2][n2];
		for (int i = 0; i < n2; i++)
			for (int j = 0; j < n2; j++)
				board[i][j] = 0;
	}

	public Sudoku(Sudoku toCopy) {
		board = new byte[n2][n2];
		for (int i = 0; i < n2; i++)
			for (int j = 0; j < n2; j++)
				board[i][j] = toCopy.board[i][j];
		zerosCounter = toCopy.zerosCounter;
	}
	
	private void refreshZerosCounter() {
		zerosCounter = 0;
		for (int i = 0; i < n2; i++)
			for (int j = 0; j < n2; j++)
				if (board[i][j] == 0)
					zerosCounter++;
	}	
	
	@Override
	public String toString() {
//		String txt = "";
//		for (int i = 0; i < n2; i++) {
//			for (int j = 0; j < n2; j++)
//				txt += board[i][j] + ",";
//			txt += "\n";
//		}
//		return txt;
		
		StringBuilder txt = new StringBuilder(256);
		for (int i = 0; i < n2; i++) {
			for (int j = 0; j < n2; j++) {
				txt.append(board[i][j]);
				txt.append(",");
			}
			txt.append("\n");
		}
		return txt.toString();		
	}
	
	public void fromString(String txt) {
		int k = 0;
		for (int i = 0; i < n2; i++) 
			for (int j = 0; j < n2; j++) {
				board[i][j] = Byte.valueOf(txt.substring(k, k + 1));
				k++;
			}
		refreshZerosCounter();
	}
	
	public boolean isLegal() {
		byte[] group = new byte[n2];
		// check rows
		for (int i = 0; i < n2; i++) {
			for (int j = 0; j < n2; j++) 
				group[j] = board[i][j];
			if (!isGroupLegal(group))
				return false;
		}
		
		// check columns
		for (int i = 0; i < n2; i++) {
			for (int j = 0; j < n2; j++) 
				group[j] = board[j][i];
			if (!isGroupLegal(group))
				return false;
		}		
		
		// check subsquares		
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				int q = 0;
				for (int k = 0; k < n; k++)
					for (int l = 0; l < n; l++) 
						group[q++] = board[i * n + k][j * n + l];
				if (!isGroupLegal(group))
					return false;					
			}
		
		return true;
	}
	
	private boolean isGroupLegal(byte[] group) {
		boolean[] visited = new boolean[n2 + 1];
		for (int i = 1; i < visited.length; i++)
			visited[i] = false;		
		for (int i = 0; i < group.length; i++) {
			if (group[i] == 0)
				continue;			
			if (visited[group[i]])
				return false;
			visited[group[i]] = true;
		}
		return true;
	}
	
	@Override
	public List<GraphState> generateChildren() {
		List<GraphState> children = new ArrayList<GraphState>();
		int i = 0, j = 0;
		zeroFinder:
		for (i = 0; i < n2; i++) // n2 == 9
			for (j = 0; j < n2; j++)
				if (board[i][j] == 0)
					break zeroFinder;
		if (i == n2)
			return children;
		for (int k = 1; k <= n2; k++) {
			Sudoku child = new Sudoku(this);
			child.board[i][j] = (byte) k;
			if (child.isLegal()) {
				children.add(child);
				child.zerosCounter--;
			}
		}
		return children;
	}

	@Override
	public boolean isSolution() {		
		return ((zerosCounter == 0) && (isLegal()));
	}

	@Override
	public int hashCode() {
		// ver. 1
		//return toString().hashCode(); 
		
		// ver. 2
		byte[] flatSudoku = new byte[n2 * n2];
		int k = 0;
		for (int i = 0; i < n2; i++)
			for (int j = 0; j < n2; j++)
				flatSudoku[k++] = board[i][j]; // we are investing time to make a copy (bad...)
		return Arrays.hashCode(flatSudoku);
		
		// ver. 3
		//return Arrays.deepHashCode(board);
	}
	
	public int getZerosCounter() {
		return zerosCounter;
	}
	
	public static void main(String[] args) {
		System.out.println("Hello world. Sudoku solver!");
		//String sudokuAsTxt = "003020600900305001001806400008102900700000008006708200002609500800203009005010300";
		String sudokuAsTxt = "000000000000305001001806400008102900700000008006708200002609500800203009005010300";
		Sudoku s = new Sudoku();
		s.fromString(sudokuAsTxt);
		System.out.println(s);
		Sudoku.setHFunction(new NaiveHeuristic());
		
		GraphSearchConfigurator conf = new GraphSearchConfigurator();
		conf.setWantedNumberOfSolutions(Integer.MAX_VALUE);
		conf.setIdentifierType(IdentifierType.STRING);
		GraphSearchAlgorithm algo = new BestFirstSearch(s, conf);
		algo.execute(); // "algo is happening here"
		List<GraphState> solutions = algo.getSolutions();
		for (GraphState sol : solutions) {
			System.out.println("---");
			System.out.println(sol);
		}
		
		System.out.println("Time [ms]: " + algo.getDurationTime());
		System.out.println("Closed states: " + algo.getClosedStatesCount());
		System.out.println("Open states: " + algo.getOpenSet().size());
		System.out.println("Solutions: " + solutions.size());
	}	
}
