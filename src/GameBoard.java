import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameBoard {
    private ArrayList<Integer> availableMoves;
    private Map<Integer, Player> board;

    public GameBoard() {
        resetBoard();
    }

    public void resetBoard() {
        availableMoves = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        board = new HashMap<>();
    }

    private boolean checkWin(int start, int incrementor) {
        try {
            return board.get(start).getSymbol() == board.get(start + incrementor).getSymbol()
                    && board.get(start).getSymbol() == board.get(start + (incrementor * 2)).getSymbol();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean makeMove(int move, Player player) {
        if (availableMoves.contains(move)) {
            board.put(move, player);
            availableMoves.remove((Integer) move);
            return checkWin(0, 1) || checkWin(0, 3) || checkWin(0, 4) || checkWin(1, 3) || checkWin(2, 3)
                    || checkWin(2, 2) || checkWin(3, 1) || checkWin(6, 1);
        } else {
            throw new Error("Move not available");
        }
    }

    public ArrayList<Integer> getAvailableMoves() {
        return availableMoves;
    }

    public void printBoard() {
        int start;
        ArrayList<Character> list;
        Player player;
        for (int i = 0; i < 3; i++) {
            start = i * 3;
            list = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                player = board.get(start + j);
                list.add(player == null ? ' ' : player.getSymbol());
            }
            System.out.println(list);
        }
    }
}
