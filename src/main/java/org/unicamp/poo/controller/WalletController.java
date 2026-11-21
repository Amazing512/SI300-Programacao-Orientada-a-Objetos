package org.unicamp.poo.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Wallet;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.WalletView;

/**
 * controller responsável pelas operações de CRUD de carteiras (Wallet).
 * Faz a mediação entre a interface de usuário ({@link WalletView}), as regras de negócio
 * e a camada de persistência ({@link WalletDAO}).
 */
public final class WalletController {
    private static final Logger log = LoggerFactory.getLogger(WalletController.class);

    private final WalletDAO model;
    private final WalletView view;

    private final MessageProvider messages;

    /**
     * Constrói o controller de carteiras com suas respectivas dependências.
     *
     * @param model    o DAO para persistência de carteiras
     * @param view     a visão responsável pela interação com o usuário
     * @param messages o provedor de mensagens internacionalizadas
     */
    public WalletController(WalletDAO model, WalletView view, MessageProvider messages) {
        super();
        this.model = model;
        this.view = view;
        this.messages = messages;
    }

    /**
     * Conduz o fluxo de cadastro de uma nova carteira através da view.
     */
    private void actionAddWallet() {
        Wallet newWallet = view.readWalletData();

        if (newWallet != null){
            Wallet saveWallet = model.create(newWallet);
            if (saveWallet != null) {
                log.info("Carteira criada com sucesso via WalletController: ID={}, titular='{}', corretora='{}'",
                        saveWallet.getId(), saveWallet.getHolder(), saveWallet.getBroker());
                view.showSuccessMessage(messages.get("wallet.add.success") + " ID: " + saveWallet.getId());
            }
            else{
                log.warn("Erro ao cadastrar carteira para titular '{}'", newWallet.getHolder());
                view.showErrorMessage(messages.get("wallet.add.error"));
            }
        } else {
            log.debug("Ação de adicionar carteira cancelada pelo usuário.");
        }
    }

    /**
     * Conduz o fluxo de consulta de uma carteira pelo ID.
     */
    private void actionSearchWallet() {
        int id = view.readWalletId();
        log.debug("Buscando carteira por ID: {}", id);

        Wallet wallet = model.findById(id);

        if(wallet != null) {
            log.info("Carteira ID {} encontrada: titular='{}'", wallet.getId(), wallet.getHolder());
            view.displayWallet(wallet);
        }
        else{
            log.warn("Carteira ID {} não encontrada.", id);
            view.showErrorMessage(messages.get("wallet.search.error.notfound"));
        }

    }

    /**
     * Conduz o fluxo de edição de dados de uma carteira existente.
     */
    private void actionEditWallet() {

        int id = view.readWalletId();

        Wallet editableWallet = model.findById(id);

        if(editableWallet != null) {
            Wallet updatedWallet = view.readWalletUpdates(editableWallet);

            if (updatedWallet != null) {
                model.update(updatedWallet);
                log.info("Carteira ID {} atualizada via WalletController: titular='{}', corretora='{}'",
                        updatedWallet.getId(), updatedWallet.getHolder(), updatedWallet.getBroker());
                view.showSuccessMessage(messages.get("wallet.edit.success"));
            }
            else{
                log.debug("Edição da carteira ID {} cancelada pelo usuário.", id);
                view.showErrorMessage(messages.get("wallet.edit.error.cancelled"));
            }
        }
        else{
            log.warn("Tentativa de editar carteira inexistente ID {}.", id);
            view.showErrorMessage(messages.get("wallet.search.error.notfound"));
        }
    }

    /**
     * Conduz o fluxo de exclusão de uma carteira com confirmação prévia.
     */
    private void actionRemoveWallet() {
        int id = view.readWalletId();
        
        Wallet removableWallet = model.findById(id);

        if (removableWallet != null){
            if (view.confirmDeletion()) {
                model.delete(id);
                log.info("Carteira ID {} excluída via WalletController.", id);
                view.showSuccessMessage(messages.get("wallet.remove.success"));
            }
            else {
                log.debug("Exclusão da carteira ID {} cancelada pelo usuário.", id);
                view.showErrorMessage(messages.get("wallet.remove.error.cancelled"));
            }
        }
        else {
            log.warn("Tentativa de remover carteira inexistente ID {}.", id);
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

    /**
     * Inicia o menu de operações de carteira, processando as escolhas do usuário em loop.
     */
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
