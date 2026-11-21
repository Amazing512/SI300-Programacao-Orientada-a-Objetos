package org.unicamp.poo.view;

import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.WalletFactory;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RED;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/**
 * Classe responsável pelas interações relativas a carteiras.
 * 
 * Realiza leituras de dados, exibições formatadas e diálogos de confirmação para criação, busca,
 * edição e exclusão de instâncias de {@link Wallet}.
 */
public class WalletView {
    private final MessageProvider messages;

    /**
     * Constrói a visão de carteiras com o provedor de mensagens para internacionalização.
     *
     * @param messages o provedor de mensagens da aplicação
     */
    public WalletView(MessageProvider messages) {
        this.messages = messages;
    }

    /**
     * Solicita e lê via console o nome do titular e da corretora para criar uma nova carteira.
     *
     * @return a nova carteira {@link Wallet} instanciada com os dados informados
     */
    public Wallet readWalletData() {
        String holder = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.holder"),
                messages.get("validation.required")
        );

        String broker = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.broker"),
                messages.get("validation.required")
        );

        return WalletFactory.create(holder, broker);
    }

    /**
     * Imprime uma mensagem de erro em vermelho no console.
     *
     * @param message o texto da mensagem de erro
     */
    public void showErrorMessage(String message) {
        System.out.println(RED + message + RESET);
    }

    /**
     * Imprime uma mensagem de sucesso em verde no console.
     *
     * @param message o texto da mensagem de sucesso
     */
    public void showSuccessMessage(String message) {
        System.out.println(GREEN + message + RESET);
    }

    /**
     * Solicita e lê o ID numérico de uma carteira a partir da entrada padrão.
     *
     * @return o identificador da carteira
     */
    public int readWalletId() {
        return ConsoleScanner.readInt(messages.get("wallet.view.prompt.id"), messages.get("generic.confirmInvalid"));
    }

    /**
     * Exibe no console os detalhes estruturados de uma carteira informada.
     *
     * @param wallet a carteira cujos dados serão apresentados
     */
    public void displayWallet(Wallet wallet) {

        System.out.println(messages.get("wallet.view.header"));
        System.out.println(messages.get("wallet.view.id") + " " + wallet.getId());
        System.out.println(messages.get("wallet.view.holder") + " " + wallet.getHolder());
        System.out.println(messages.get("wallet.view.broker") + " " + wallet.getBroker());
    }

    /**
     * Solicita ao usuário novos valores para titular e corretora de uma carteira existente.
     *
     * @param editableWallet a carteira original a ser modificada
     * @return uma nova instância de {@link Wallet} com os dados atualizados
     */
    public Wallet readWalletUpdates(Wallet editableWallet) {
        String holder = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.newHolder"),
                messages.get("validation.required")
        );

        String broker = ConsoleScanner.readRequiredString(
                messages.get("wallet.view.prompt.newBroker"),
                messages.get("validation.required")
        );

        return WalletFactory.withUpdatedData(editableWallet.getId(), holder, broker);
    }

    /**
     * Solicita confirmação explícita do usuário (Sim/Não) para exclusão de uma carteira.
     *
     * @return {@code true} se o usuário confirmou a exclusão; {@code false} caso contrário
     */
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