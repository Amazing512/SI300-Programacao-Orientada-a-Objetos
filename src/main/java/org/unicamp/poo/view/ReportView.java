package org.unicamp.poo.view;

import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.util.ConsoleScanner;

import java.util.List;
import java.util.Scanner;

/*  View class responsible for interacting with the user and formatting console
    layouts for financial and ranking reports. */

public class ReportView {

    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String green = "\u001B[32m";
    public static final String red = "\u001B[31m";

    public int readWalletIdReport(){
        Scanner read = ConsoleScanner.getInstance();

        System.out.print("Digite o ID da carteira para gerar o relatório financeiro: ");
        int id = read.nextInt();
        read.nextLine();

        return id;
    }

    // Renders a financial panel block showing metrics and net values.

    public void showFinancialReport(int walletId, double totalCashIn, double totalCashOut, double result) {

        System.out.println("\n----------------------------------");
        System.out.println("       RELATÓRIO FINANCEIRO       ");
        System.out.println("----------------------------------");
        System.out.println("ID da carteira: " + walletId);
        System.out.printf("Total de entrada de moedas: %.2f%n", totalCashIn);
        System.out.printf("Total de saídas de moedas: %.2f%n", totalCashOut);
        System.out.println("----------------------------------");
        System.out.print("Resultado Líquido: ");

        if (result < 0){
            System.out.println(red + String.format("%.2f", result) + reset);
        }
        else {
            System.out.println(green + String.format("%.2f", result) + reset);
        }
        System.out.println("--------------------------------");

    }

    // Formats and prints a multi-column aligned dashboard showcasing the wallets ranking.

    public void showSortedWalletsReport(List<Wallet> wallets, double[] sortedBalances) {
        System.out.println("\n---------------------------------");
        System.out.println("       CARTEIRAS POR SALDO        ");
        System.out.println("---------------------------------");

        System.out.printf("%-10s | %-25s | %-15s%n", "ID", "PROPRIETÁRIO", "SALDO ATUAL");
        System.out.println("---------------------------------");

        // Iterates and flushes formatted rows to the console
        for(int i = 0; i < wallets.size(); i++){
            Wallet w = wallets.get(i);
            double balance = sortedBalances[i];

            System.out.printf("%-10d | %-25s | %.2f%n", w.getId(), w.getHolder(), balance);
        }
        System.out.println("---------------------------------");
    }

    // Prints an error message highlighted in red color
    public void showErrorMessage(String s) {
        System.out.println(red + s + reset);
    }
}
