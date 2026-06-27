package org.unicamp.poo.view;

import java.util.Scanner;

import org.unicamp.poo.model.Wallet;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/*
Classe View responsável por lidar com operações de entrada e saída de terminal
referentes ao módulo Carteira.
 */

public class WalletView {
    private final MessageProvider messages;

    // Construtor que fornece Injeção de Dependência para mensagens internacionalizadas
    public WalletView(MessageProvider messages) {
        this.messages = messages;
    }

    // Lê todos os campos requeridos para instanciar e registrar uma nova Carteira
    public Wallet readWalletData() {
        // Usa a instância centralizada e compartilhada de Scanner dos utilitários do projeto
        Scanner read = ConsoleScanner.getInstance();

        System.out.print(messages.get("wallet.view.prompt.holder"));
        String holder = read.nextLine();

        System.out.print(messages.get("wallet.view.prompt.broker"));
        String broker = read.nextLine();

        return new Wallet(holder, broker);
    }

    // Imprime uma mensagem de erro destacada na cor VERMELHA
    public void showErrorMessage(String s) {
        System.out.println(RED + s + RESET);
    }

    // Imprime uma mensagem de sucesso destacada na cor VERDE
    public void showSuccessMessage(String s) {
        System.out.println(GREEN + s + RESET);
    }

    // Solicita ao usuário um ID de Carteira
    public int readWalletId() {
        return ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));
    }

    // Exibe os detalhes atuais de uma determinada instância de Carteira formatada no console
    public void displayWallet(Wallet wallet) {

        System.out.println(messages.get("wallet.view.header"));
        System.out.println(messages.get("wallet.view.id") + " " + wallet.getId());
        System.out.println(messages.get("wallet.view.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("wallet.view.broker") + " " + wallet.getBroker());
    }

    // Solicita ao usuário detalhes atualizados de titular e corretora, mantendo o ID original da entidade
    public Wallet readWalletUpdates(Wallet editableWallet) {
        Scanner read = ConsoleScanner.getInstance();

        System.out.print(messages.get("wallet.view.prompt.newHolder"));
        String holder = read.nextLine();

        System.out.print(messages.get("wallet.view.prompt.newBroker"));
        String broker = read.nextLine();

        // Cria uma nova instância usando o mesmo ID da carteira editável alvo
        Wallet wallet = new Wallet(editableWallet.getId(), holder, broker);
        return wallet;
    }

    // Solicita uma escolha de confirmação para executar ações de exclusão irreversíveis de forma segura
    public boolean confirmDeletion(Wallet removableWallet) {
        // Loop até que uma opção de decisão válida (1 ou 2) seja fornecida pelo usuário
        while (true) {
            System.out.println(messages.get("wallet.view.prompt.confirmDelete"));
            System.out.println("1 - " + messages.get("generic.confirmYes"));
            System.out.println("2 - " + messages.get("generic.confirmNo"));

            Integer option = ConsoleScanner.readIntOrNull();
            if (option == null) {
                System.out.println(messages.get("generic.confirmInvalid"));
                continue;
            }

            switch (option) {
                case 1 -> {
                    return true;
                }
                case 2 -> {
                    return false;
                }
                default -> System.out.println(messages.get("generic.confirmInvalid"));
            }
        }
    }
}