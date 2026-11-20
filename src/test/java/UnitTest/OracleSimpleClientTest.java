package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.oracle.OracleSimpleClient;

class OracleSimpleClientTest {

    @Test
    void initializesFromConfiguration() {
        OracleSimpleClient client = new OracleSimpleClient();
        assertNotNull(client.getHost(), "Host do servidor deve ser carregado");
        assertTrue(client.getPort() > 0, "Porta do servidor deve ser válida");
    }

    @Test
    void allowsServerOverride() {
        OracleSimpleClient client = new OracleSimpleClient("oracle.example", 4321);

        assertEquals("oracle.example", client.getHost());
        assertEquals(4321, client.getPort());
    }
}
