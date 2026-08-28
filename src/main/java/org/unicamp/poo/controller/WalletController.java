package org.unicamp.poo.controller;

import java.util.ArrayList;
import java.util.List;

import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Wallet;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.WalletView;

// Controller responsável por gerenciar operações relacionadas a Carteiras.
public final class WalletController {

    private final WalletDAO model;
    private final WalletView view;

    private final MessageProvider messages;

    public WalletController(WalletDAO model, WalletView view, MessageProvider messages) {
        super();
        this.model = model;
        this.view = view;
        this.messages = messages;
    }

    // Ação para registrar nova carteira.
    private void actionAddWallet() {
        // Solicita o objeto Carteira pela View
        Wallet newWallet = view.readWalletData();

        if (newWallet != null){
            Wallet saveWallet = model.create(newWallet);
            if (saveWallet != null) {
                view.showSuccessMessage(messages.get("wallet.add.success") + " ID: " + saveWallet.getId());
            }
            else{
                view.showErrorMessage(messages.get("wallet.add.error"));
            }
        }
    }

    // Ação para buscar uma carteira pelo seu ID
    private void actionSearchWallet() {
        // Obtém da view o ID a ser buscado
        int id = view.readWalletId();

        Wallet wallet = model.findById(id);

        if(wallet != null) {
            view.displayWallet(wallet);
        }
        else{
            view.showErrorMessage(messages.get("wallet.search.error.notfound"));
        }

    }

    // Ação para editar os detalhes de uma carteira existente
    private void actionEditWallet() {

        // Obtém o ID da carteira a ser editada
        int id = view.readWalletId();

        Wallet editableWallet = model.findById(id);

        if(editableWallet != null) {
            // Solicita à view que peça os detalhes atualizados ao usuário
            Wallet updatedWallet = view.readWalletUpdates(editableWallet);

            if (updatedWallet != null) {
                // Atualiza as informações da carteira
                model.update(updatedWallet);
                view.showSuccessMessage(messages.get("wallet.edit.success"));
            }
            else{
                view.showErrorMessage(messages.get("wallet.edit.error.cancelled"));
            }
        }
        else{
            view.showErrorMessage(messages.get("wallet.search.error.notfound"));
        }
    }

    // Ação para remover uma carteira.
    private void actionRemoveWallet() {
        // Obtém o ID da carteira a ser removida
        int id = view.readWalletId();
        
        Wallet removableWallet = model.findById(id);

        if (removableWallet != null){
            // Solicita uma confirmação de exclusão do usuário através da camada view
            if (view.confirmDeletion()) {
                // Remove a carteira do repositório DAO
                model.delete(id);
                view.showSuccessMessage(messages.get("wallet.remove.success"));
            }
            else {
                view.showErrorMessage(messages.get("wallet.remove.error.cancelled"));
            }
        }
        else {
            view.showErrorMessage(messages.get("wallet.search.error.notfound"));
        }
    }

    private List<String> getMenuOptions()
    {
        final List<String> options = new ArrayList<>();
        options.add(messages.get("walletMenu.return"));
        options.add(messages.get("walletMenu.addWallet"));
        options.add(messages.get("walletMenu.searchWallet"));
        options.add(messages.get("walletMenu.editWallet"));
        options.add(messages.get("walletMenu.removeWallet"));
        return (options);
    }

    // loop do menu principal das carteiras.
    public void start()
    {
        final List<String> options = getMenuOptions();
        final Menu walletMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean loop = true;

        while (loop) {
            String yellowTitle = YELLOW + messages.get("walletMenu.title") + RESET;

            switch (walletMenu.getChoice(yellowTitle, options, messages.get("walletMenu.prompt")))
            {
                case 0 -> loop = false;
                case 1 -> {
                    ConsoleScanner.clearScreen();
                    actionAddWallet();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 2 -> {
                    ConsoleScanner.clearScreen();
                    actionSearchWallet();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 3 -> {
                    ConsoleScanner.clearScreen();
                    actionEditWallet();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 4 -> {
                    ConsoleScanner.clearScreen();
                    actionRemoveWallet();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                default -> loop = false;
            }
        }
    }
}
