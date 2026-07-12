package ai.connect4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameSearchConfigurator;
import sac.game.GameState;
import sac.game.GameStateImpl;

public class connect4 extends GameStateImpl {

    public static final int M = 6; // rows
    public static final int N = 7; // columns

    public static final boolean X_PLAYER_AI = true;
    public static final boolean O_PLAYER_AI = false;

    public static final byte O = -1; // minimizing player
    public static final byte E = 0;  // empty
    public static final byte X = 1;  // maximizing player

    private static final String[] SYMBOLS = {"O", ".", "X"};

    private byte[][] board;

    private int iLast = -1;
    private int jLast = -1;

    public connect4() {
        board = new byte[M][N];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = E;
            }
        }

        setMaximizingTurnNow(true);
    }

    public connect4(connect4 toCopy) {
        board = new byte[M][N];

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                board[i][j] = toCopy.board[i][j];
            }
        }

        iLast = toCopy.iLast;
        jLast = toCopy.jLast;

        setMaximizingTurnNow(toCopy.isMaximizingTurnNow());
    }

    public byte[][] getBoard() {
        return board;
    }

    public int getILast() {
        return iLast;
    }

    public int getJLast() {
        return jLast;
    }

    public boolean isBoardEmpty() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] != E) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();

        txt.append("\n");

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                txt.append("|");
                txt.append(SYMBOLS[board[i][j] + 1]);
            }
            txt.append("|\n");
        }

        for (int j = 0; j < N; j++) {
            txt.append(" ");
            txt.append(j);
        }

        txt.append("\n");

        return txt.toString();
    }

    @Override
    public List<GameState> generateChildren() {
        List<GameState> children = new ArrayList<>();

        if (checkWin() || checkDraw()) {
            return children;
        }

        for (int j = 0; j < N; j++) {
            connect4 child = new connect4(this);

            if (child.makeMove(j)) {
                child.setMoveName(Integer.toString(j));
                children.add(child);
            }
        }

        return children;
    }

    public boolean makeMove(int j) {
        if (j < 0 || j >= N) {
            return false;
        }

        int i = M - 1;

        while (i >= 0 && board[i][j] != E) {
            i--;
        }

        if (i < 0) {
            return false;
        }

        board[i][j] = isMaximizingTurnNow() ? X : O;

        iLast = i;
        jLast = j;

        setMaximizingTurnNow(!isMaximizingTurnNow());

        return true;
    }

    public boolean checkDraw() {
        for (int j = 0; j < N; j++) {
            if (board[0][j] == E) {
                return false;
            }
        }

        return true;
    }

    public boolean checkWin() {
        if (iLast < 0 || jLast < 0) {
            return false;
        }

        byte token = board[iLast][jLast];

        if (token == E) {
            return false;
        }

        return countDirection(iLast, jLast, 0, 1, token) >= 4 ||
                countDirection(iLast, jLast, 1, 0, token) >= 4 ||
                countDirection(iLast, jLast, 1, 1, token) >= 4 ||
                countDirection(iLast, jLast, 1, -1, token) >= 4;
    }

    private int countDirection(int i, int j, int di, int dj, byte token) {
        int count = 1;

        int r = i + di;
        int c = j + dj;

        while (r >= 0 && r < M && c >= 0 && c < N && board[r][c] == token) {
            count++;
            r += di;
            c += dj;
        }

        r = i - di;
        c = j - dj;

        while (r >= 0 && r < M && c >= 0 && c < N && board[r][c] == token) {
            count++;
            r -= di;
            c -= dj;
        }

        return count;
    }

    @Override
    public int hashCode() {
        byte[] flatBoard = new byte[M * N + 1];

        int k = 0;

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                flatBoard[k++] = board[i][j];
            }
        }

        flatBoard[k] = (byte) (isMaximizingTurnNow() ? 1 : -1);

        return Arrays.hashCode(flatBoard);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof connect4)) {
            return false;
        }

        connect4 other = (connect4) obj;

        if (this.isMaximizingTurnNow() != other.isMaximizingTurnNow()) {
            return false;
        }

        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (this.board[i][j] != other.board[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        connect4 c4 = new connect4();
        Scanner scanner = new Scanner(System.in);
        System.out.println(c4);

        connect4.setHFunction(new Connect4Evaluation());

        GameSearchAlgorithm algo = new AlphaBetaPruning();

        GameSearchConfigurator conf = new GameSearchConfigurator();

        // Use 3.0 or 4.0. 5.5 is too slow on many computers.
        conf.setDepthLimit(5.5);

        algo.setConfigurator(conf);

        while (true) {
            if (X_PLAYER_AI) {
                System.out.println("AI THINKING...");

                if (c4.isBoardEmpty()) {
                    int firstMove = N / 2;
                    c4.makeMove(firstMove);
                    System.out.println("BEST MOVE: " + firstMove);
                } else {
                    algo.setInitial(c4);
                    algo.execute();

                    System.out.println("TIME [ms]: " + algo.getDurationTime());
                    System.out.println("CLOSED STATES: " + algo.getClosedStatesCount());
                    System.out.println("DEPTH REACHED: " + algo.getDepthReached());
                    System.out.println("MOVES SCORES: " + algo.getMovesScores());

                    String bestMove = algo.getFirstBestMove();
                    System.out.println("BEST MOVE: " + bestMove);

                    c4.makeMove(Integer.valueOf(bestMove));
                }
            } else {
                boolean moveLegal = false;

                do {
                    System.out.print("PLAYER X MAKE YOUR MOVE: ");
                    int j = scanner.nextInt();
                    moveLegal = c4.makeMove(j);
                } while (!moveLegal);
            }

            System.out.println(c4);

            if (c4.checkWin()) {
                System.out.println("X WINS!");
                break;
            }

            if (c4.checkDraw()) {
                System.out.println("GAME ENDS WITH A DRAW.");
                break;
            }

            if (O_PLAYER_AI) {
                System.out.println("AI THINKING...");

                algo.setInitial(c4);
                algo.execute();

                System.out.println("TIME [ms]: " + algo.getDurationTime());
                System.out.println("CLOSED STATES: " + algo.getClosedStatesCount());
                System.out.println("DEPTH REACHED: " + algo.getDepthReached());
                System.out.println("MOVES SCORES: " + algo.getMovesScores());

                String bestMove = algo.getFirstBestMove();
                System.out.println("BEST MOVE: " + bestMove);

                c4.makeMove(Integer.valueOf(bestMove));
            } else {
                boolean moveLegal = false;

                do {
                    System.out.print("PLAYER O MAKE YOUR MOVE: ");
                    int j = scanner.nextInt();
                    moveLegal = c4.makeMove(j);
                } while (!moveLegal);
            }

            System.out.println(c4);

            if (c4.checkWin()) {
                System.out.println("O WINS!");
                break;
            }

            if (c4.checkDraw()) {
                System.out.println("GAME ENDS WITH A DRAW.");
                break;
            }
        }

        scanner.close();
    }
}