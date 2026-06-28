package org.unicamp.poo.view;

import java.util.List;
import java.util.Scanner;

import org.unicamp.poo.util.ConsoleColors;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

public final class Menu {
    private final Scanner scanner;
    private final MessageProvider messages;

    public Menu(Scanner scanner, MessageProvider messages) {
        this.scanner = scanner;
        this.messages = messages;
    }

    public int getChoice(String title, List<String> options, String prompt)
    {
        boolean waiting = true;
        int     choice  = -1;
        String errorMessage = null;

        while (waiting)
        {
            ConsoleScanner.clearScreen();
            if (errorMessage != null) {
                // Imprime a mensagem de erro em vermelho
                System.out.println(ConsoleColors.RED + errorMessage + ConsoleColors.RESET);
                
                // Limpa para a próxima exibição
                errorMessage = null; 
            }
            System.out.println(title);
            for (int count = 0; count < options.size(); count++)
            {
                System.out.println(count + " - " + options.get(count));
            }
            System.out.println(prompt);
            try {
                String input = this.scanner.nextLine().trim();
                choice = Integer.parseInt(input);
                waiting = (choice < 0) || (choice >= options.size());
                if (waiting) {
                    errorMessage = messages.get("menu.invalidInput") + " " + (options.size() - 1) + ".";
                }
            }
            catch (NumberFormatException exception) {
                errorMessage = messages.get("menu.invalidInput") + " " + (options.size() - 1) + ".";
                choice = -1;
            }
        }
        return (choice);
    }
}
