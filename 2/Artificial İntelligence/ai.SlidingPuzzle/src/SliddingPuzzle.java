package ai.SlidingPuzzle;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

import java.util.ArrayList;
import java.util.List;

public class SliddingPuzzle extends GraphStateImpl {
    public static byte nr = 3;
    private byte[][] puzzle;
    private int zeroI;
    private int zeroJ;
    
    private static java.util.Random rand = new java.util.Random(123);
    
    public SliddingPuzzle() {  
    	byte k = 0;
        puzzle = new byte[nr][nr];
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nr; j++) {
                puzzle[i][j] = k++;
            }
        }           
        zeroI = 0;
        zeroJ = 0;
    }
    
    public SliddingPuzzle(SliddingPuzzle toCopy) {
        puzzle = new byte[nr][nr];
        for (int i = 0; i < nr; i++)
            for (int j = 0; j < nr; j++)
                puzzle[i][j] = toCopy.puzzle[i][j];
        zeroI = toCopy.zeroI;
        zeroJ = toCopy.zeroJ;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nr; j++) {
                if (puzzle[i][j] == 0) {
                    sb.append("   "); // Leave zero empty for better visualization
                } else {
                    sb.append(String.format("%2d ", puzzle[i][j]));
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
    public void generateShuffledBoard(int mixingMoves) {
   

   

        // Mix randomly
        for (int m = 0; m < mixingMoves; m++) {
            int move = rand.nextInt(4) + 1; // 1=UP, 2=DOWN, 3=LEFT, 4=RIGHT
            int oldI = zeroI;
            int oldJ = zeroJ;

            if (move == 1) zeroI--; // UP
            else if (move == 2) zeroI++; // DOWN
            else if (move == 3) zeroJ--; // LEFT
            else if (move == 4) zeroJ++; // RIGHT

            // Check if move is valid
            if (zeroI >= 0 && zeroI < nr && zeroJ >= 0 && zeroJ < nr) {
                // Swap zero with target tile
                byte temp = puzzle[zeroI][zeroJ];
                puzzle[zeroI][zeroJ] = 0;
                puzzle[oldI][oldJ] = temp;
            } else {
                // Invalid move, revert and repeat
                zeroI = oldI;
                zeroJ = oldJ;
                m--;
            }
        }
    }

    @Override
    public boolean isSolution() {
        return getNrWrongs() == 0;
    }

    public int getDistanceAllPieces() { // Changed to int for safety
        int count = 0;

        for (int i = 0; i < nr; i++)      // 'i' is the row (Y)
            for (int j = 0; j < nr; j++) {   // 'j' is the column (X)

                byte currentPiece = puzzle[i][j];

                if (currentPiece != 0) {
                    // X
                    count += Math.abs(getPositionX(currentPiece) - j);

                    // Y
                    count += Math.abs(getPositionY(currentPiece) - i);
                }
            }

        return count;
    }

 // Fix getNrWrongs() — new goal: 0,1,2 / 3,4,5 / 6,7,8
    public byte getNrWrongs() {
        byte count = 0;
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nr; j++) {
                if (puzzle[i][j] == 0) {
                    // 0 must be at (0,0)
                     // if (i != 0 || j != 0) count++;
                    continue;
                }
                // Tile value should equal i*nr + j
                if (puzzle[i][j] != (i * nr + j)) {
                    count++;
                }
            }
        }
        return count;
    }
    
    

    // Fix getPositionX() — goal column of tile n is n % nr
    private int getPositionX(byte number) {
        return  (number % nr);
    }

    // Fix getPositionY() — goal row of tile n is n / nr
    private int getPositionY(byte number) {
        return  (number / nr);
    }

    @Override
    public List<GraphState> generateChildren(){
        List<GraphState> children = new ArrayList<>();
        int i = 0, j = 0;
        zeroFinder:
        for (i = 0; i < nr; i++)
            for (j = 0; j < nr; j++)
                if(puzzle[i][j] == 0)
                    break zeroFinder;
        if(i == nr)
            return children;

       play(i,j,children);
        return children;
        }

    private void play(int i, int j, List<GraphState> children) {

            if (isLegal(i,j +1)) {
                SliddingPuzzle child = new SliddingPuzzle(this);
                child.puzzle[i][j] = child.puzzle[i][j + 1];
                child.puzzle[i][j+1] = 0;
                children.add(child);
                child.setMoveName("r");
            }

        if (isLegal(i,j -1)) {
            SliddingPuzzle child = new SliddingPuzzle(this);
            child.puzzle[i][j] = child.puzzle[i][j - 1];
            child.puzzle[i][j-1] = 0;
            children.add(child);
            child.setMoveName("l");
        }

        if (isLegal(i + 1,j)) {
            SliddingPuzzle child = new SliddingPuzzle(this);
            child.puzzle[i][j] = child.puzzle[i+1][j];
            child.puzzle[i+1][j] = 0;
            children.add(child);
            child.setMoveName("u");
        }

        if (isLegal(i - 1,j)) {
            SliddingPuzzle child = new SliddingPuzzle(this);
            child.puzzle[i][j] = child.puzzle[i-1][j];
            child.puzzle[i-1][j] = 0;
            children.add(child);
            child.setMoveName("d");
        }

    }

    private boolean isLegal(int x, int y) {
        if (x < 0 || x >= nr || y < 0 || y >= nr) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.deepHashCode(puzzle);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SliddingPuzzle that = (SliddingPuzzle) obj;
        return java.util.Arrays.deepEquals(this.puzzle, that.puzzle);
    }
    
    

    static void main() {

    }

}