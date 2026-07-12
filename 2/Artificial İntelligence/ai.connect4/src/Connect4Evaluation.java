package ai.connect4;

import sac.State;
import sac.StateFunction;

public class Connect4Evaluation extends StateFunction {

    private static final int[][] DIRECTIONS = {
            {0, 1},   // horizontal
            {1, 0},   // vertical
            {1, 1},   // diagonal \
            {1, -1}   // diagonal /
    };

    @Override
    public double calculate(State state) {
        connect4 c4 = (connect4) state;
        byte[][] board = c4.getBoard();

        if (c4.checkWin()) {
            int iLast = c4.getILast();
            int jLast = c4.getJLast();

            byte winner = board[iLast][jLast];

            if (winner == connect4.X) {
                return Double.POSITIVE_INFINITY;
            }

            if (winner == connect4.O) {
                return Double.NEGATIVE_INFINITY;
            }
        }

        double score = 0.0;

        score += evaluateAllWindows(board);
        score += evaluateCenter(board);
        score += evaluateHeight(board);

        return score;
    }

    private double evaluateAllWindows(byte[][] board) {
        double score = 0.0;

        for (int i = 0; i < connect4.M; i++) {
            for (int j = 0; j < connect4.N; j++) {
                for (int[] direction : DIRECTIONS) {
                    int di = direction[0];
                    int dj = direction[1];

                    int endI = i + 3 * di;
                    int endJ = j + 3 * dj;

                    if (endI < 0 || endI >= connect4.M || endJ < 0 || endJ >= connect4.N) {
                        continue;
                    }

                    int xCount = 0;
                    int oCount = 0;
                    int emptyCount = 0;

                    for (int k = 0; k < 4; k++) {
                        byte field = board[i + k * di][j + k * dj];

                        if (field == connect4.X) {
                            xCount++;
                        } else if (field == connect4.O) {
                            oCount++;
                        } else {
                            emptyCount++;
                        }
                    }

                    score += evaluateWindow(xCount, oCount, emptyCount);
                }
            }
        }

        return score;
    }

    private double evaluateWindow(int xCount, int oCount, int emptyCount) {
        if (xCount > 0 && oCount > 0) {
            return 0.0;
        }

        if (xCount == 4) {
            return 1000000.0;
        }

        if (oCount == 4) {
            return -1000000.0;
        }

        if (xCount == 3 && emptyCount == 1) {
            return 100.0;
        }

        if (xCount == 2 && emptyCount == 2) {
            return 10.0;
        }

        if (xCount == 1 && emptyCount == 3) {
            return 1.0;
        }

        if (oCount == 3 && emptyCount == 1) {
            return -120.0;
        }

        if (oCount == 2 && emptyCount == 2) {
            return -12.0;
        }

        if (oCount == 1 && emptyCount == 3) {
            return -1.0;
        }

        return 0.0;
    }

    private double evaluateCenter(byte[][] board) {
        double score = 0.0;

        int centerColumn = connect4.N / 2;

        for (int i = 0; i < connect4.M; i++) {
            if (board[i][centerColumn] == connect4.X) {
                score += 6.0;
            } else if (board[i][centerColumn] == connect4.O) {
                score -= 6.0;
            }
        }

        int leftCenter = centerColumn - 1;
        int rightCenter = centerColumn + 1;

        for (int i = 0; i < connect4.M; i++) {
            if (board[i][leftCenter] == connect4.X) {
                score += 3.0;
            } else if (board[i][leftCenter] == connect4.O) {
                score -= 3.0;
            }

            if (board[i][rightCenter] == connect4.X) {
                score += 3.0;
            } else if (board[i][rightCenter] == connect4.O) {
                score -= 3.0;
            }
        }

        return score;
    }

    private double evaluateHeight(byte[][] board) {
        double score = 0.0;

        for (int i = 0; i < connect4.M; i++) {
            for (int j = 0; j < connect4.N; j++) {
                if (board[i][j] == connect4.X) {
                    score += i * 0.2;
                } else if (board[i][j] == connect4.O) {
                    score -= i * 0.2;
                }
            }
        }


        return score;
    }
}