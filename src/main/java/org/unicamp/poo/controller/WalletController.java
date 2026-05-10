package org.unicamp.poo.controller;

import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.WalletView;

import java.util.ArrayList;
import java.util.List;

public final class WalletController {
    WalletDAO model;
    WalletView view;

    MessageProvider messages;

    public WalletController(WalletDAO model, WalletView view, MessageProvider messages) {
        super();
        this.model = model;
        this.view = view;
        this.messages = messages;
    }

    private void actionAddWallet() {

    }

    private void actionSearchWallet() {

    }

    private void actionEditWallet() {

    }

    private void actionRemoveWallet() {

    }

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

    public void start()
    {
        final List<String> options      = getMenuOptions();
        final Menu walletMenu = new Menu(ConsoleScanner.getInstance());
        boolean            loop         = true;

        while (loop)
        {
            switch (walletMenu.getChoice(messages.get("walletMenu.title"), options, messages.get("walletMenu.prompt")))
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
