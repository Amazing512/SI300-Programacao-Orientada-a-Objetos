package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.util.MessageProvider;

class MessageProviderTest {

    @Test
    void returnsExistingMessageAndFallbackForUnknownKey() {
        MessageProvider messages = new MessageProvider("messages", "pt", "BR");

        assertEquals("!!! unit.test.missing!!!", messages.get("unit.test.missing"));
        assertTrue(messages.get("walletMenu.title").contains("CARTEIRA"));
    }

    @Test
    void changesLanguage() {
        MessageProvider messages = new MessageProvider("messages", "pt", "BR");

        messages.changeLanguage("messages", "es", "ES");

        assertTrue(messages.get("walletMenu.title").contains("BILLETERA"));
    }
}