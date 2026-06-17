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

    public static OperationType fromCode(char code) {
        for (OperationType type : values()) {
            if (type.code == code) {
                return type;
            }
        }

        throw new IllegalArgumentException("Código inválido: " + code);
    }
}