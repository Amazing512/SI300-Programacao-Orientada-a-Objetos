package org.unicamp.poo.util;

import java.util.Scanner;

public class ConsoleScanner {
    private static final Scanner scanner = new Scanner(System.in);

    private ConsoleScanner()
    {
        // empty private constructor avoids instantiation
    }

    public static Scanner getInstance()
    {
        return (scanner);
    }
}
