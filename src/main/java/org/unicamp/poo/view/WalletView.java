package org.unicamp.poo.view;

import org.unicamp.poo.model.Wallet;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/* View responsável por lidar com operações referentes a Carteira. */
public class WalletView {
    private final MessageProvider messages;

    public WalletView(MessageProvider messages) {
        this.messages = messages;
    }

    // Lê todos os campos requeridos para instanciar e registrar uma nova Carteira
    public Wallet readWalletData() {
        String holder = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.holder"),
                messages.get("validation.required")
        );

        String broker = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.broker"),
                messages.get("validation.required")
        );

        return new Wallet(holder, broker);
    }

    // Imprime uma mensagem de erro destacada
    public void showErrorMessage(String s) {
        System.out.println(RED + s + RESET);
    }

    // Imprime uma mensagem de sucesso destacada
    public void showSuccessMessage(String s) {
        System.out.println(GREEN + s + RESET);
    }

    // Solicita ao usuário um ID de Carteira
    public int readWalletId() {
        return ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));
    }

    // Exibe os detalhes atuais de uma Carteira
    public void displayWallet(Wallet wallet) {

        System.out.println(messages.get("wallet.view.header"));
        System.out.println(messages.get("wallet.view.id") + " " + wallet.getId());
        System.out.println(messages.get("wallet.view.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("wallet.view.broker") + " " + wallet.getBroker());
    }

    // Solicita ao usuário detalhes atualizados de titular e corretora, mantendo o ID original
    public Wallet readWalletUpdates(Wallet editableWallet) {
        String holder = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.newHolder"),
                messages.get("validation.required")
        );

        String broker = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.newBroker"),
                messages.get("validation.required")
        );

        // Cria uma nova instância usando o mesmo ID da carteira editada
        return new Wallet(editableWallet.getId(), holder, broker);
    }

    // Solicita uma confirmação para executar ações de exclusão
    public boolean confirmDeletion() {
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