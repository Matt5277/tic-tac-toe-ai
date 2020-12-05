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
            input = InputProvider.getNumberInput("Do you want to start AI learning mode (1) or play against AI (2)? ");
        }
        if (input == 1) {
            startLearningMode();
        } else {
            // playAgainstAI();
        }
    }

    private static void startLearningMode() {
        AI.startLearning(new Player('X'), new Player('O'),
                InputProvider.getNumberInput("How many games should the AI play? "));
    }

    // private static void playAgainstAI() {
    // Player player = new Player(InputProvider.getCharInput("What character do you
    // wish to be? "));
    // Player ai = new Player((player.getSymbol() == 'X' || player.getSymbol() ==
    // 'x') ? 'O' : 'X');
    // boolean gameOver = false;
    // while (!gameOver) {

    // }
    // }

}
