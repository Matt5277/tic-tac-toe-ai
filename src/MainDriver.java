import java.util.Scanner;

public class MainDriver {
    public static void main(String[] args) {
        restartGame();
        // Player test1 = new Player('X');
        // Player test2 = new Player('O');
        // gameBoard.makeMove(0, test1);
        // gameBoard.makeMove(1, test2);
        // gameBoard.makeMove(8, test1);
        // gameBoard.makeMove(4, test1);
        // gameBoard.printBoard();
    }

    private static void restartGame() {
        int input = 0;
        while (input != 1 && input != 2) {
            input = InputProvider.getNumberInput("Do you want to play single player (1) or multiplayer (2)? ");
        }
        if (input == 1) {
            playSinglePlayer();
        } else {
            playMultiPlayer();
        }
    }

}
