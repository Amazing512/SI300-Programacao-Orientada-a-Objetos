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

// Classe Controller responsável por gerenciar operações relacionadas a Carteiras.

public final class WalletController {

    // Atributos que encapsulam o Model (DAO), View e mensagens internacionalizadas
    private final WalletDAO model;
    private final WalletView view;

    private final MessageProvider messages;

    // Construtor que usa Injeção de Dependência para inicializar os componentes do controller.
    public WalletController(WalletDAO model, WalletView view, MessageProvider messages) {
        super();
        this.model = model;
        this.view = view;
        this.messages = messages;
    }

    // Ação para registrar uma nova carteira.
    private void actionAddWallet() {
        // Solicita à view para ler os dados do terminal e retornar um objeto Carteira temporário
        Wallet newWallet = view.readWalletData();

        if (newWallet != null){
            // Persiste a nova carteira através da camada DAO
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

        // Busca a carteira no repositório DAO
        Wallet wallet = model.findById(id);

        if(wallet != null) {
            // Exibe os detalhes da carteira se for encontrada
            view.displayWallet(wallet);
        }
        else{
            // Mostra uma mensagem de erro se a carteira não existir
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
                // Atualiza as informações da carteira no repositório do banco/memória
                model.update(updatedWallet);
                view.showSuccessMessage(messages.get("wallet.edit.success"));
            }
            else{
                // Tratamento de erro caso a atualização retorne nula
                view.showErrorMessage(messages.get("wallet.edit.error.cancelled"));
            }
        }
        else{
            // Mostra uma mensagem de erro se a carteira não existir no repositório
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
                // Feedback de erro se a exclusão for rejeitada
                view.showErrorMessage(messages.get("wallet.remove.error.cancelled"));
            }
        }
        else {
            // Tratamento de erro se a carteira alvo não existir no banco de dados
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

    // Inicia o loop do menu principal para o sistema de Gerenciamento de Carteiras.
    public void start()
    {
        final List<String> options = getMenuOptions();
        final Menu walletMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean loop = true;

        // Continua interagindo com o usuário até que a opção de saída (0) seja selecionada
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
