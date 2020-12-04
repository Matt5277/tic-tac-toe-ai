import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameBoard {
    private ArrayList<Integer> availablePositions;
    private HashMap<Integer, Player> board;

    public void resetBoard() {
        availablePositions = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        board = new HashMap<>();
    }

    public GameBoard() {
        resetBoard();
    }

    private boolean checkWin(int start, int incrementor) {
        return board.get(start).getSymbol() == board.get(start + incrementor).getSymbol()
                && board.get(start).getSymbol() == board.get(start + (incrementor * 2)).getSymbol();
    }

    public boolean makeMove(int position, Player player) {
        if (availablePositions.contains(position)) {
            board.put(position, player);
            availablePositions.remove((Integer) position);
            return checkWin(0, 1) || checkWin(0, 3) || checkWin(0, 4) || checkWin(1, 3) || checkWin(2, 3)
                    || checkWin(2, 2) || checkWin(3, 1) || checkWin(6, 1);
        } else {
            throw new Error("Position not available");
        }
    }
}
