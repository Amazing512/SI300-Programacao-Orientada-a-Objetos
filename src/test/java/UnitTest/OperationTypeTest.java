package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.model.enums.OperationType;

class OperationTypeTest {

    @Test
    void convertsValidCodes() {
        assertEquals(OperationType.CASH_IN, OperationType.fromCode('C'));
        assertEquals(OperationType.CASH_OUT, OperationType.fromCode('V'));
    }

    @Test
    void rejectsInvalidCode() {
        assertThrows(IllegalArgumentException.class, () -> OperationType.fromCode('X'));
    }
}