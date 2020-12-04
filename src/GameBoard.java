import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameBoard {
    private ArrayList<Integer> availablePossitions;
    private HashMap<Integer, Character> board;

    public GameBoard() {
        this.availablePossitions = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }
}
