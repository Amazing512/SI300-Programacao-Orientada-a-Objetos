package org.unicamp.poo.main;

import org.unicamp.poo.controller.ProgramController;
import org.unicamp.poo.model.enums.DatabaseSelector;
import org.unicamp.poo.util.MessageProvider;

public final class Main {
    private final static String          lang     = "pt";
    private final static String          country  = "BR";
    private final static MessageProvider messageProvider = new MessageProvider("messages", lang, country);

    public static void main() {
        try {
            final String serverName = "WindServer";

            (new ProgramController(DatabaseSelector.MEMORY, messageProvider)).start(serverName);


        } catch(Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
