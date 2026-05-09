package org.unicamp.poo.model.enums;

public enum OperationType {
    CASH_IN('C'),
    CASH_OUT('V');

    private final char code;

    OperationType(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }
}
