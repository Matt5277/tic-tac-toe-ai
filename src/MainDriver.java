import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainDriver {
    public static void main(String[] args) {
        restartGame();
    }

    private static void restartGame() {
        int input = 0;
        while (input != 1 && input != 2) {
            input = InputProvider.getNumberInput("Do you want to start AI learning mode (1) or play against AI (2)? ");
        }
        if (input == 1) {
            startLearningMode();
        } else {
            playAgainstAI();
        }
    }

    private static void startLearningMode() {
        AI.startLearning(new Player('X'), new Player('O'),
                InputProvider.getNumberInput("How many games should the AI play? "));
    }

    private static void playAgainstAI() {
        Map<String, Integer> aiGameHistory = AI.extractHistoryFromFile();
        Player player = new Player(InputProvider.getCharInput("What character do you wish to be? "));
        Player ai = new Player((player.getSymbol() == 'X' || player.getSymbol() == 'x') ? 'O' : 'X');
        GameBoard gameBoard = new GameBoard();
        ArrayList possibleMoves;
        boolean win = false;
        String gameRecord = "";
        while (true) {
            possibleMoves = gameBoard.getAvailablePositions();
            int move = -1;
            while (!possibleMoves.contains(move)) {
                System.out.println("\nThese are the possible moves:\n" + possibleMoves.toString());
                move = InputProvider.getNumberInput("Please select one: ");
            }

            win = gameBoard.makeMove(move, player);
            gameRecord += move;
            gameBoard.printBoard();
            if (gameBoard.getAvailablePositions().isEmpty()) {
                if (win) {
                    gameRecord += 'W';
                    System.out.println("\nYou win!!!");
                    break;
                } else {
                    gameRecord += 'T';
                    System.out.println("\nThe game ended in a tie :///");
                    break;
                }
            } else if (win) {
                gameRecord += 'W';
                System.out.println("\nYou win!!!");
                break;
            }

            System.out.println("\nAI move:");
            int nextMove = AI.getNextBestMove(gameRecord, aiGameHistory, gameBoard);
            win = gameBoard.makeMove(nextMove, ai);
            gameRecord += nextMove;
            gameBoard.printBoard();

            if (gameBoard.getAvailablePositions().isEmpty()) {
                if (win) {
                    gameRecord += 'L';
                    System.out.println("\nThe AI won :///");
                    break;
                } else {
                    gameRecord += 'T';
                    System.out.println("\nThe game ended in a tie :///");
                    break;
                }
            } else if (win) {
                gameRecord += 'L';
                System.out.println("\nThe AI won :///");
                break;
            }

        }
        Integer numb = aiGameHistory.get(gameRecord);
        AI.appendToLog(gameRecord + "-" + (numb == null ? 1 : numb + 1));
    }

}
