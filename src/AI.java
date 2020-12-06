import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class AI {

    private static String aiLogFileName = "ai-log.txt";

    private enum WinLose {
        WIN, LOSE
    }

    public static void startLearning(Player ai1, Player ai2, int numbGames) {
        GameBoard gameBoard = new GameBoard();
        Map<String, Integer> filteredGameHistory = new HashMap<>(), gameHistory = extractHistoryFromFile();
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
                    if (gameBoard.getAvailableMoves().isEmpty()) {
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
                    if (gameBoard.getAvailableMoves().isEmpty()) {
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
            return getBestMoveFromArray(gameBoard.getAvailableMoves(), gameBoard);
        }
        List<Integer> possibleMoves = (ArrayList<Integer>) gameBoard.getAvailableMoves().clone();
        Map<String, Integer> lossMap, winMap;
        if (gameRecordLength % 2 != 0) {
            // win = bad lose = good
            lossMap = extractRelevantGameData(gameRecord, gameHistory, WinLose.WIN);
            winMap = extractRelevantGameData(gameRecord, gameHistory, WinLose.LOSE);
        } else {
            // win = good lose = bad
            lossMap = extractRelevantGameData(gameRecord, gameHistory, WinLose.LOSE);
            winMap = extractRelevantGameData(gameRecord, gameHistory, WinLose.WIN);
        }

        Map<Integer, Integer> bestMoves = new HashMap<>();
        for (String key : lossMap.keySet()) {
            if (key.startsWith(gameRecord)) {
                int index = key.indexOf(gameRecord) + gameRecordLength;
                if (index == key.length() - 3) {
                    possibleMoves.remove((Integer) Integer.parseInt(key.charAt(index) + ""));
                }
            }
        }

        for (String key : lossMap.keySet()) {
            int index = key.lastIndexOf(gameRecord) + gameRecordLength + 1;
            if (index < key.length() - 1) {
                Integer numb = Integer.parseInt(key.charAt(index) + "");
                if (possibleMoves.contains(numb)) {
                    Integer prob = bestMoves.putIfAbsent(numb, 1);
                    if (prob != null) {
                        bestMoves.put(numb, prob + 1);
                    }
                }
            }
        }

        for (String key : winMap.keySet()) {
            int index = key.indexOf(gameRecord) + gameRecordLength;
            if (key.length() - 2 == index) {
                Integer numb = Integer.parseInt(key.charAt(index) + "");
                Integer prob = bestMoves.putIfAbsent(numb, 1);
                if (prob != null) {
                    bestMoves.put(numb, prob + 1);
                }
            }
        }

        if (bestMoves.isEmpty()) {
            return getBestMoveFromArray(possibleMoves.isEmpty() ? gameBoard.getAvailableMoves() : possibleMoves,
                    gameBoard);
        }

        return getBestMoveFromMap(bestMoves, gameBoard);
    }

    private static Map<String, Integer> extractRelevantGameData(String gameRecord, Map<String, Integer> gameHistory,
            WinLose winOrLoseData) {
        return gameHistory.entrySet().stream()
                .filter(map -> map.getKey().contains(gameRecord)
                        && map.getKey().endsWith(winOrLoseData == WinLose.WIN ? "W" : "L"))
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
    }

    private static Map<String, Integer> preFilterGameHistory(String gameRecord,
            Map<String, Integer> filteredGameHistory) {
        if (gameRecord.length() == 0) {
            return filteredGameHistory;
        }
        return filteredGameHistory.entrySet().stream().filter(map -> map.getKey().contains(gameRecord))
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
    }

    private static int getBestMoveFromArray(List<Integer> possibilities, GameBoard gameBoard) {
        int rand;
        if (possibilities.isEmpty()) {
            ArrayList poss = gameBoard.getAvailableMoves();
            rand = new Random().nextInt(poss.size());
            return (int) poss.get(rand);
        }
        rand = new Random().nextInt(possibilities.size());
        return possibilities.get(rand);
    }

    private static int getBestMoveFromMap(Map<Integer, Integer> bestMoves, GameBoard gameBoard) {
        int sum = 0;
        for (Integer value : bestMoves.values()) {
            sum += value;
        }
        int rand = new Random().nextInt(sum);
        sum = 0;
        for (Map.Entry<Integer, Integer> map : bestMoves.entrySet()) {
            sum += map.getValue();
            if (sum >= rand) {
                return map.getKey();
            }
        }
        return getBestMoveFromArray(new ArrayList<>(), gameBoard);
    }

    public static Map<String, Integer> extractHistoryFromFile() {
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

    public static void appendToLog(String gameRecord) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(aiLogFileName, true));
            out.append(gameRecord + "\n");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
