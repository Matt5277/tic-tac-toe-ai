import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AI {

    private static String aiLogFileName = "ai-log.txt";

    private enum WinLose {
        WIN, LOSE
    }

    public static void startLearning(Player ai1, Player ai2, int numbGames) {
        GameBoard gameBoard = new GameBoard();
        Map<String, Integer> filteredGameHistory = new HashMap<>(), gameHistory = new HashMap<>();
        boolean gameError;
        String gameRecord;
        for (int i = 0; i < numbGames; i++) {
            if (i % 500 == 0) {
                System.out.println(i);
            }
            gameError = false;
            gameBoard.resetBoard();
            gameRecord = "";
            filteredGameHistory.clear();
            filteredGameHistory.putAll(gameHistory);
            while (!gameError) {
                try {
                    filteredGameHistory = preFilterGameHistory(gameRecord, filteredGameHistory);
                    int nextMoveAI1 = getNextBestMove(gameRecord, filteredGameHistory, gameBoard);
                    gameRecord += nextMoveAI1 + "";
                    boolean ai1Win = gameBoard.makeMove(nextMoveAI1, ai1);
                    if (gameBoard.getAvailablePositions().isEmpty()) {
                        if (ai1Win) {
                            gameRecord += 'W';
                        } else {
                            gameRecord += 'T';
                        }
                        break;
                    } else if (ai1Win) {
                        gameRecord += 'W';
                        break;
                    }
                    filteredGameHistory = preFilterGameHistory(gameRecord, filteredGameHistory);
                    int nextMoveAI2 = getNextBestMove(gameRecord, filteredGameHistory, gameBoard);
                    gameRecord += nextMoveAI2 + "";
                    boolean ai2Win = gameBoard.makeMove(nextMoveAI2, ai2);
                    if (gameBoard.getAvailablePositions().isEmpty()) {
                        if (ai2Win) {
                            gameRecord += 'L';
                        } else {
                            gameRecord += 'T';
                        }
                        break;
                    } else if (ai2Win) {
                        gameRecord += 'L';
                        break;
                    }

                } catch (Error e) {
                    System.out.println("Not Possible Move");
                    gameRecord += "NA";
                    gameError = true;
                }
            }

            Integer numb = gameHistory.get(gameRecord);
            if (numb == null) {
                gameHistory.put(gameRecord, 1);
            } else {
                gameHistory.put(gameRecord, numb + 1);
            }
        }
        storeResults(gameHistory);
    }

    private static void storeResults(Map<String, Integer> gameHistory) {
        try {
            PrintStream out = new PrintStream(new File(aiLogFileName));
            for (Map.Entry<String, Integer> entry : gameHistory.entrySet()) {
                out.println(entry.getKey() + "-" + entry.getValue());
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getNextBestMove(String gameRecord, Map<String, Integer> gameHistory, GameBoard gameBoard) {
        int gameRecordLength = gameRecord.length();
        if (gameRecordLength == 0) {
            return getPositionFromArray(gameBoard.getAvailablePositions(), gameBoard);
        }
        List<Integer> possibleMoves = (ArrayList<Integer>) gameBoard.getAvailablePositions().clone();
        Map<String, Integer> lossMap, winMap;
        if (gameRecordLength % 2 != 0) {
            // win = bad lose = good
            lossMap = extractRelevantGameData(gameHistory, gameRecord, WinLose.WIN);
            winMap = extractRelevantGameData(gameHistory, gameRecord, WinLose.LOSE);
        } else {
            // win = good lose = bad
            lossMap = extractRelevantGameData(gameHistory, gameRecord, WinLose.LOSE);
            winMap = extractRelevantGameData(gameHistory, gameRecord, WinLose.WIN);
        }

        for (String key : lossMap.keySet()) {
            char lastChar = key.charAt(key.length() - 1);
            String winKey = key.substring(0, key.length() - 1) + (lastChar == 'W' ? 'L' : 'W');
            if (winMap.get(winKey) == null) {
                possibleMoves.remove((Integer) Integer.parseInt(key.charAt(gameRecordLength) + ""));
            }
        }

        int min = 12;
        for (String key : winMap.keySet()) {
            char lastChar = key.charAt(key.length() - 1);
            String lossKey = key.substring(0, key.length() - 1) + (lastChar == 'W' ? 'L' : 'W');
            Integer lossKeyValue = lossMap.get(lossKey);
            Integer winKeyValue = winMap.get(key);
            if ((lossKeyValue == null || winKeyValue >= lossKeyValue) && key.length() < min) {
                min = key.length();
            }
        }

        List<Integer> bestPositions = new ArrayList<>();
        for (String key : winMap.keySet()) {
            int value = Integer.parseInt(key.charAt(gameRecordLength) + "");
            if (key.length() == min && possibleMoves.contains(value)) {
                bestPositions.add(value);
            }
        }

        return getPositionFromArray(bestPositions.isEmpty() ? possibleMoves : bestPositions, gameBoard);
    }

    private static Map<String, Integer> extractRelevantGameData(Map<String, Integer> gameHistory, String gameRecord,
            WinLose winOrLoseData) {
        return gameHistory.entrySet().stream()
                .filter(map -> map.getKey().startsWith(gameRecord)
                        && map.getKey().endsWith(winOrLoseData == WinLose.WIN ? "W" : "L"))
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
    }

    private static Map<String, Integer> preFilterGameHistory(String gameRecord,
            Map<String, Integer> filteredGameHistory) {
        if (gameRecord.length() == 0) {
            return filteredGameHistory;
        }
        return filteredGameHistory.entrySet().stream().filter(map -> map.getKey().startsWith(gameRecord))
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
    }

    private static int getPositionFromArray(List<Integer> possibilities, GameBoard gameBoard) {
        int rand;
        if (possibilities.isEmpty()) {
            ArrayList poss = gameBoard.getAvailablePositions();
            rand = new Random().nextInt(poss.size());
            return (int) poss.get(rand);
        }
        rand = new Random().nextInt(possibilities.size());
        return possibilities.get(rand);
    }

    public static Map<String, Integer> extractAIDataFromFile() {
        Map<String, Integer> returnMap = new HashMap<>();
        try {
            Scanner scan = new Scanner(new File(aiLogFileName));
            String line;
            String[] list;
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                list = line.split("-");
                returnMap.put(list[0], Integer.parseInt(list[1]));
            }
            scan.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnMap;
    }
}