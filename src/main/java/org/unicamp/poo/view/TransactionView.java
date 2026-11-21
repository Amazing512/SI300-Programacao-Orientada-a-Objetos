package org.unicamp.poo.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.TransactionFactory;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/**
 * Classe de visualização de operações financeiras de compra e venda de moedas.
 * 
 * Responsável pela leitura de entradas numéricas e datas, exibição de saldos,
 * apresentação da cotação do Oráculo e confirmação das transações.
 */
public class TransactionView {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final MessageProvider messages;

    /**
     * Constrói a visão de transações associada ao provedor de mensagens.
     *
     * @param messages o provedor de mensagens internacionalizadas
     */
    public TransactionView(MessageProvider messages) {
        this.messages = messages;
    }

    /**
     * Solicita e lê os dados para criação de uma movimentação financeira (id da carteira, quantidade e data).
     *
     * @param operationType o tipo da operação financeira (aporte ou resgate)
     * @return a transação {@link Transaction} instanciada, ou null se cancelada com quantidade 0
     */
    public Transaction readTransactionData(OperationType operationType) {
        int id = ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));

        double quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.quantity"), messages.get("generic.confirmInvalid"));

        while (quantity <= 0){
            if(quantity == 0) {
                return null;
            }
            quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.positiveQuantity"), messages.get("generic.confirmInvalid"));
        }

        LocalDate operationDate = readOperationDate();
        return TransactionFactory.create(id, operationDate, operationType, quantity);
    }

    /**
     * Exibe uma mensagem de erro formatada em vermelho no console.
     *
     * @param message o texto do erro
     */
    public void showErrorMessage(String message) {
        System.out.println(RED + message + RESET);
    }

    /**
     * Exibe uma mensagem de sucesso formatada em verde no console.
     *
     * @param message o texto da mensagem de sucesso
     */
    public void showSuccessMessage(String message) {
        System.out.println(GREEN + message + RESET);
    }

    /**
     * Apresenta na tela as informações de cotação diária fornecida pelo Oráculo.
     *
     * @param dailyQuote a cotação obtida do Oráculo
     */
    public void displayDailyQuote(Oracle dailyQuote) {
        System.out.println("\n" + messages.get("report.oracle.display.title"));
        System.out.println(messages.get("report.oracle.display.date") + "  " + DATE_FORMAT.format(dailyQuote.getDate()));
        System.out.printf(messages.get("report.oracle.display.price") + " %.2f%n", dailyQuote.getPrice());
        System.out.println(messages.get("report.oracle.display.separator"));
    }

    /**
     * Solicita e lê o id numérico da carteira para a transação.
     *
     * @return o identificador da carteira
     */
    public int readWalletId() {
        return ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));
    }

    /**
     * Exibe o saldo atual de moedas de uma carteira.
     *
     * @param balance o total de moedas em posse da carteira
     */
    public void displayWalletBalance(double balance) {
        System.out.printf(messages.get("transaction.view.walletBalance") + " %.4f%n", balance);
    }

    /**
     * Lê uma quantidade de moedas estritamente positiva para a operação.
     *
     * @return a quantidade digitada ou null se o usuário cancelou digitando zero
     */
    public Double readQuantity() {
        double quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.quantity"), messages.get("generic.confirmInvalid"));
        while (quantity <= 0) {
            if (quantity == 0) {
                return null;
            }
            quantity = ConsoleScanner.readDouble(messages.get("transaction.view.prompt.positiveQuantity"), messages.get("generic.confirmInvalid"));
        }
        return quantity;
    }

    /**
     * Solicita confirmação ao usuário para efetivar ou rejeitar a movimentação financeira.
     *
     * @param confirmMessage a mensagem informativa com o resumo da transação
     * @return {@code true} se o usuário optar por rejeitar/cancelar a transação; {@code false} se confirmar
     */
    public boolean confirmRejectTransaction(String confirmMessage) {
        while (true) {
            System.out.println("\n" + confirmMessage);
            System.out.println("1 - " + messages.get("generic.confirmYes"));
            System.out.println("2 - " + messages.get("generic.confirmNo"));

            Integer option = ConsoleScanner.readIntOrNull();
            if (option == null) {
                System.out.println(messages.get("generic.confirmInvalid"));
                continue;
            }

            switch (option) {
                case 1 -> {
                    return false;
                }
                case 2 -> {
                    return true;
                }
                default -> System.out.println(messages.get("generic.confirmInvalid"));
            }
        }
    }

    /**
     * Lê a data informada para a operação financeira, ou retorna a data corrente caso vazia.
     *
     * @return a data correspondente à operação
     */
    public LocalDate readOperationDate() {
        return ConsoleScanner.readLocalDateOrNow(
            messages.get("transaction.view.prompt.date"),
            messages.get("generic.confirmInvalid")
        );
    }
}