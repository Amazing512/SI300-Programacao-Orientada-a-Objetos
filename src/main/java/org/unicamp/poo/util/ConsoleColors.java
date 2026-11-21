package org.unicamp.poo.util;

/**
 * Classe que define constantes com códigos de escape ANSI utilizadas para estilização de cores no console.
 */
public final class ConsoleColors {
    /**
     * Código ANSI para redefinir a formatação e cor do terminal ao estado padrão.
     */
    public static final String RESET = "\u001B[0m";

    /**
     * Código ANSI para colorir texto em amarelo.
     */
    public static final String YELLOW = "\u001B[33m";

    /**
     * Código ANSI para colorir texto em verde.
     */
    public static final String GREEN = "\u001B[32m";

    /**
     * Código ANSI para colorir texto em vermelho.
     */
    public static final String RED = "\u001B[31m";

    private ConsoleColors() {
    }
}