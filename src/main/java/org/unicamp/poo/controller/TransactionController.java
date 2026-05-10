package org.unicamp.poo.controller;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.TransactionView;
import org.unicamp.poo.view.WalletView;

import java.util.ArrayList;
import java.util.List;

public class TransactionController {
    TransactionDAO model;
    TransactionView view;

    MessageProvider messages;

    public TransactionController(TransactionDAO model, TransactionView view, MessageProvider messages) {
        super();
        this.model = model;
        this.view = view;
        this.messages = messages;
    }

    private void actionBuyCoin() {

    }

    private void actionSellCoin() {

    }

    private List<String> getMenuOptions()
    {
        final List<String> options = new ArrayList<String>();
        options.add(messages.get("transactionMenu.return"));
        options.add(messages.get("transactionMenu.buyCoin"));
        options.add(messages.get("transactionMenu.sellCoin"));
        return (options);
    }

    public void start()
    {
        final List<String> options      = getMenuOptions();
        final Menu transactionMenu = new Menu(ConsoleScanner.getInstance());
        boolean            loop         = true;

        while (loop)
        {
            switch (transactionMenu.getChoice(messages.get("transactionMenu.title"), options, messages.get("transactionMenu.prompt")))
            {
                case 0 -> loop = false;
                case 1 -> actionBuyCoin();
                case 2 -> actionSellCoin();
                default -> loop = false;
            }
        }
    }
}
