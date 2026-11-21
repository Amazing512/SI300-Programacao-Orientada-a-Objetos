package org.unicamp.poo.model.enums;

/**
 * Enum que define o tipo de movimentação financeira na carteira.
 * Identifica compras e vendas de moedas.
 */
public enum OperationType {
    /**
     * Operação de compra de moedas.
     */
    CASH_IN('C'),

    /**
     * Operação de venda de moedas.
     */
    CASH_OUT('V');

    private final char code;

    OperationType(char code) {
        this.code = code;
    }

    /**
     * Obtém o código da operação.
     *
     * @return o código ('C' para compra, 'V' para venda)
     */
    public char getCode() {
        return code;
    }

    /**
     * Mapeia um código para a constante correspondente de {@link OperationType}.
     *
     * @param code o caractere identificador da operação ('C' ou 'V')
     * @return o tipo de operação correspondente
     * @throws IllegalArgumentException se o código não corresponder a nenhum tipo válido
     */
    public static OperationType fromCode(char code) {
        for (OperationType type : values()) {
            if (type.code == code) {
                return type;
            }
        }

        throw new IllegalArgumentException("Código inválido: " + code);
    }
}