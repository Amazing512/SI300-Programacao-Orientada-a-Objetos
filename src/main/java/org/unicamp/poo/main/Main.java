package org.unicamp.poo.main;

import org.unicamp.poo.controller.ProgramController;
import org.unicamp.poo.model.enums.DatabaseSelector;
import org.unicamp.poo.util.MessageProvider;

public final class Main {
    private final static String          lang     = "en";
    private final static String          country  = "US";
    private final static MessageProvider messageProvider = new MessageProvider("messages", "pt", "BR");

    static void main() {
        try {
            final String serverName = "WindServer";

            (new ProgramController(DatabaseSelector.MARIADB, messageProvider)).start(serverName);


        } catch(Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
