package org.unicamp.poo.view;

import org.unicamp.poo.util.MessageProvider;

import java.util.List;
import java.util.Scanner;

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

        while (waiting)
        {
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
                    System.out.println(messages.get("menu.invalidInput") + " " + (options.size() - 1) + ".");
                }
            }
            catch (NumberFormatException exception) {
                System.out.println(messages.get("menu.invalidInput") + " " + (options.size() - 1) + ".");
                choice = -1;
            }
        }
        return (choice);
    }
}
