package org.unicamp.poo.view;

import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;
import org.unicamp.poo.util.ConsoleScanner;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

// View class responsible for handling console interactions regarding transactions.

public class TransactionView {

    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String green = "\u001B[32m";
    public static final String red = "\u001B[31m";

    public Transaction readTransactionData(OperationType operationType) {
        Scanner read = ConsoleScanner.getInstance();

        System.out.print("Digite o ID da carteira: ");
        int id = read.nextInt();
        read.nextLine();

        System.out.print("Digite a quantidade de moedas: ");
        double quantity = read.nextDouble();

        // Forces user to enter a positive non-zero value
        while (quantity <= 0){
            System.out.println("Digite a quantidade de moedas (Deve ser um valor positivo): ");
            quantity = read.nextDouble();
        }
        return new Transaction(id, new Date(), operationType, quantity);
    }

    public int readWalletForHistory(){
        Scanner read = ConsoleScanner.getInstance();

        System.out.println("Digite o ID da carteira para consultar o histórico: ");
        int id = read.nextInt();
        read.nextLine();

        return id;
    }

    // Iterates through a list of transactions and prints them formatted to the console.
    public void showHistory(List<Transaction> transactions){

        // Check if there are no records to display
        if (transactions == null || transactions.isEmpty()){
            System.out.println("Nenhuma transação encontrada.");
        }
        else{
            System.out.println("\n----- Histórico de Movimentações -----");
            for (int i = 0; i < transactions.size(); i++){
                Transaction t = transactions.get(i);

                System.out.println("----------------------------------------------");
                System.out.println("Movimentação "+ (i+1));
                System.out.println("Data: " + t.getOperationDate());
                System.out.print("Tipo da transação: ");

                if (t.getOperationType() == OperationType.CASH_IN){
                    System.out.println(green + "Compra" + reset);
                }
                else if (t.getOperationType() == OperationType.CASH_OUT){
                    System.out.println(red + "Venda" + reset);
                }
                System.out.println("Quantidade de moedas movimentadas: " + t.getQuantity());
            }
            System.out.println("----------------------------------------------");
        }
    }

    // Prints an error message highlighted in red color
    public void showErrorMessage(String s) {
        System.out.println(red + s + reset);
    }

    // Prints a success message highlighted in green color
    public void showSuccessMessage(String s) {
        System.out.println(green + s + reset);
    }
}