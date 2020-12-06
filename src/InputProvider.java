import java.util.Scanner;

public class InputProvider {

    private static Scanner userIn = new Scanner(System.in);

    public static int getNumbInput(String prompt) {
        return getNumbInput(prompt, false);
    }

    private static int getNumbInput(String prompt, boolean nan) {
        try {
            if (nan) {
                System.out.println("\nPlease enter a valid number.");
            }
            System.out.print(prompt);
            return Integer.parseInt(userIn.next());
        } catch (NumberFormatException e) {
            getNumbInput(prompt, true);
        }
        return 0;
    }

    public static char getCharInput(String prompt) {
        return getCharInput(prompt, false);
    }

    private static char getCharInput(String prompt, boolean invalidResponse) {
        if (invalidResponse) {
            System.out.println("\nPlease enter a single character.");
        }
        System.out.println(prompt);
        String input = userIn.next();
        if (input.length() != 1) {
            return getCharInput(prompt, true);
        } else {
            return input.charAt(0);
        }
    }
}
