package org.unicamp.poo.view;

import org.unicamp.poo.util.ConsoleScanner;

import java.util.List;
import java.util.Scanner;

public final class MenuView {
    private final Scanner scanner;

    public MenuView(Scanner scanner) {
        this.scanner = scanner;
    }

    public int getChoice(String title, List<String> options, String prompt)
    {
        boolean waiting = true;
        int     choice  = -1;

        while (waiting)
        {
            System.out.println("------------------------------------------------------------");
            System.out.println(title);
            System.out.println("------------------------------------------------------------");
            for (int count = 0; count < options.size(); count++)
            {
                System.out.println(count + " - " + options.get(count));
            }
            System.out.println("------------------------------------------------------------");
            System.out.println(prompt);
            choice = this.scanner.nextInt();
            this.scanner.nextLine();
            waiting = (choice < 0) || (choice >= options.size());
        }
        return (choice);
    }
}
