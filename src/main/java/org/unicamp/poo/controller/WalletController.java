package org.unicamp.poo.controller;

import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.WalletView;

import java.util.ArrayList;
import java.util.List;

// Controller class responsible for managing operations related to Wallets.

public final class WalletController {

    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String yellow = "\u001B[33m";

    // Attributes encapsulating the Model (DAO), View, and internationalized messages
    private final WalletDAO model;
    private final WalletView view;

    private final MessageProvider messages;

    // Constructor using Dependency Injection to initialize the controller components.
    public WalletController(WalletDAO model, WalletView view, MessageProvider messages) {
        super();
        this.model = model;
        this.view = view;
        this.messages = messages;
    }

    // Action to register a new wallet.
    private void actionAddWallet() {
        // Requests the view to read data from the terminal and return a temporary Wallet object
        Wallet newWallet = view.readWalletData();

        if (newWallet != null){
            // Checks if a wallet with the given ID already exists to prevent duplication
            if(model.findById(newWallet.getId()) != null){
                view.showErrorMessage(messages.get("wallet.add.error.duplicate"));
                return;
            }

            // Persists the new wallet through the DAO layer
            Wallet saveWallet = model.create(newWallet);
            if (saveWallet != null) {
                view.showSuccessMessage(messages.get("wallet.add.success"));
            }
            else{
                view.showErrorMessage(messages.get("wallet.add.error"));
            }
        }
    }

    // Action to look up a wallet by its ID
    private void actionSearchWallet() {
        // Gets the ID to be searched from the view
        int id = view.readWalletId();

        // Searches for the wallet in the DAO repository
        Wallet wallet = model.findById(id);

        if(wallet != null) {
            // Displays the wallet details if found
            view.displayWallet(wallet);
        }
        else{
            // Shows an error message if the wallet does not exist
            view.showErrorMessage(messages.get("wallet.search.error.notfound"));
        }

    }

    // Action to edit an existing wallet's details
    private void actionEditWallet() {

        // Gets the ID of the wallet to be edited
        int id = view.readWalletId();
        Wallet editableWallet = model.findById(id);

        if(editableWallet != null) {
            // Requests the view to prompt the user for the updated details
            Wallet updatedWallet = view.readWalletUpdates(editableWallet);

            if (updatedWallet != null) {
                // Updates the wallet information in the database/memory repository
                model.update(updatedWallet);
                view.showSuccessMessage(messages.get("wallet.edit.success"));
            }
            else{
                // Error handling in case the update returns null
                view.showErrorMessage(messages.get("wallet.edit.error.cancelled"));
            }
        }
    }

    // Action to delete a wallet.
    private void actionRemoveWallet() {
        // Gets the ID of the wallet to be removed
        int id = view.readWalletId();
        Wallet removableWallet = model.findById(id);

        if (removableWallet != null){
            // Asks the user for a deletion confirmation through the view layer
            if (view.confirmDeletion(removableWallet)) {
                // Removes the wallet from the DAO repository
                model.delete(id);
                view.showSuccessMessage(messages.get("wallet.remove.success"));
            }
            else {
                // Error feedback or fallback if deletion is rejected/not found
                view.showErrorMessage(messages.get("wallet.remove.error.cancelled"));
            }
        }
    }

    //Populates and retrieves the list of menu options from the MessageProvider
    private List<String> getMenuOptions()
    {
        final List<String> options = new ArrayList<String>();
        options.add(messages.get("walletMenu.return"));
        options.add(messages.get("walletMenu.addWallet"));
        options.add(messages.get("walletMenu.searchWallet"));
        options.add(messages.get("walletMenu.editWallet"));
        options.add(messages.get("walletMenu.removeWallet"));
        return (options);
    }

    // Starts the main menu loop for the Wallet Management system.
    public void start()
    {
        final List<String> options = getMenuOptions();
        final Menu walletMenu = new Menu(ConsoleScanner.getInstance());
        boolean loop = true;

        // Keeps interacting with the user until the exit option (0) is selected
        while (loop) {
            String yellowTitle = yellow + messages.get("walletMenu.title") + reset;

            switch (walletMenu.getChoice(yellowTitle, options, messages.get("walletMenu.prompt")))
            {
                case 0 -> loop = false;
                case 1 -> actionAddWallet();
                case 2 -> actionSearchWallet();
                case 3 -> actionEditWallet();
                case 4 -> actionRemoveWallet();
                default -> loop = false;
            }
        }
    }
}
