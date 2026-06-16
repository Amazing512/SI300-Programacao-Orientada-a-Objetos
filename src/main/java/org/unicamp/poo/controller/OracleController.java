package org.unicamp.poo.controller;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.ReportView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Controller responsible for managing daily cryptocurrency price quote (Oracle) operations.

public class OracleController {

    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String yellow = "\u001B[33m";

    private final OracleDAO model;
    private final ReportView view;
    private final MessageProvider messages;

    // Constructor using Dependency Injection to initialize the controller components.
    public OracleController(OracleDAO model, ReportView view, MessageProvider messages) {
        super();
        this.model = model;
        this.view = view;
        this.messages = messages;
    }

    // Action to register a new daily quote.
    private void actionAddOracle() {
        Oracle newOracle = view.readOracleData();

        if (newOracle != null) {
            // Checks if a quote for the given date already exists to prevent duplication
            if (model.findByDate(newOracle.getDate()) != null) {
                view.showErrorMessage(messages.get("oracle.add.error.duplicate"));
                return;
            }

            // Persists the new quote through the DAO layer
            Oracle savedOracle = model.create(newOracle);
            if (savedOracle != null) {
                view.showSuccessMessage(messages.get("oracle.add.success"));
            } else {
                view.showErrorMessage(messages.get("oracle.add.error"));
            }
        }
    }

    // Action to look up a quote by date.
    private void actionSearchOracle() {
        Date date = view.readOracleDate();

        Oracle oracle = model.findByDate(date);

        if (oracle != null) {
            view.displayOracle(oracle);
        } else {
            view.showErrorMessage(messages.get("oracle.search.error.notFound"));
        }
    }

    // Action to list all registered quotes.
    private void actionListOracles() {
        List<Oracle> oracles = model.findAll();

        if (oracles == null || oracles.isEmpty()) {
            view.showErrorMessage(messages.get("oracle.list.empty"));
            return;
        }

        view.displayOracleList(oracles);
    }

    // Action to edit an existing quote.
    private void actionEditOracle() {
        Date date = view.readOracleDate();
        Oracle editableOracle = model.findByDate(date);

        if (editableOracle != null) {
            Oracle updatedOracle = view.readOracleUpdates(editableOracle);

            if (updatedOracle != null) {
                model.update(updatedOracle);
                view.showSuccessMessage(messages.get("oracle.edit.success"));
            } else {
                view.showErrorMessage(messages.get("oracle.edit.error.cancelled"));
            }
        } else {
            view.showErrorMessage(messages.get("oracle.search.error.notFound"));
        }
    }

    // Action to delete a quote by date.
    private void actionRemoveOracle() {
        Date date = view.readOracleDate();
        Oracle removableOracle = model.findByDate(date);

        if (removableOracle != null) {
            if (view.confirmDeletion(removableOracle)) {
                model.delete(date);
                view.showSuccessMessage(messages.get("oracle.remove.success"));
            } else {
                view.showErrorMessage(messages.get("oracle.remove.error.cancelled"));
            }
        } else {
            view.showErrorMessage(messages.get("oracle.search.error.notFound"));
        }
    }

    // Populates and retrieves the list of menu options from the MessageProvider.
    private List<String> getMenuOptions() {
        final List<String> options = new ArrayList<>();
        options.add(messages.get("oracleMenu.return"));
        options.add(messages.get("oracleMenu.addOracle"));
        options.add(messages.get("oracleMenu.searchOracle"));
        options.add(messages.get("oracleMenu.listOracles"));
        options.add(messages.get("oracleMenu.editOracle"));
        options.add(messages.get("oracleMenu.removeOracle"));
        return options;
    }

    // Starts the main menu loop for the Oracle Management system.
    public void start() {
        final List<String> options = getMenuOptions();
        final Menu oracleMenu = new Menu(ConsoleScanner.getInstance());
        boolean loop = true;

        while (loop) {
            switch (oracleMenu.getChoice(messages.get(yellow + "oracleMenu.title" + reset), options, messages.get("oracleMenu.prompt"))) {
                case 0 -> loop = false;
                case 1 -> actionAddOracle();
                case 2 -> actionSearchOracle();
                case 3 -> actionListOracles();
                case 4 -> actionEditOracle();
                case 5 -> actionRemoveOracle();
                default -> loop = false;
            }
        }
    }
}