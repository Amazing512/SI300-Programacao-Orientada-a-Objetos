package org.unicamp.poo.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unicamp.poo.controller.ProgramController;
import org.unicamp.poo.model.enums.DatabaseSelector;
import org.unicamp.poo.util.Info;
import org.unicamp.poo.util.MessageProvider;

public final class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private final static String          lang     = "pt";
    private final static String          country  = "BR";
    private final static MessageProvider messageProvider = new MessageProvider("messages", lang, country);

    static void main(String[] args) {
        try {
            final String serverName = "WindServer";
            log.info("Iniciando aplicação FT Coin com servidor {}", serverName);

            System.out.println(Info.getStamp());

            (new ProgramController(DatabaseSelector.MEMORY, messageProvider)).start(serverName);

            log.info("Aplicação finalizada normalmente.");
        } catch(Exception ex) {
            log.error("Erro fatal na execução da aplicação: {}", ex.getMessage(), ex);
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
