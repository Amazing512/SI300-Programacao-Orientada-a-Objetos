package org.unicamp.poo.view;

import java.util.List;
import java.util.Scanner;

import org.unicamp.poo.util.ConsoleColors;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;

/**
 * Classe responsável pela apresentação formatada
 * de menus de opções e validação da opção numérica escolhida pelo usuário.
 */
public final class Menu {
    private final Scanner scanner;
    private final MessageProvider messages;

    /**
     * Constrói uma nova instância de Menu vinculada ao scanner de entrada e ao provedor de mensagens.
     *
     * @param scanner  o scanner para leitura das entradas de console
     * @param messages o provedor de mensagens internacionalizadas
     */
    public Menu(Scanner scanner, MessageProvider messages) {
        this.scanner = scanner;
        this.messages = messages;
    }

    /**
     * Exibe o menu com título e lista de opções, aguardando a seleção de uma opção válida.
     * Repete a solicitação e exibe mensagem de erro caso o valor digitado não seja um número no intervalo válido.
     *
     * @param title   o texto do cabeçalho ou título do menu
     * @param options a lista de opções textuais a serem apresentadas
     * @param prompt  o texto de orientação/solicitação exibido abaixo das opções
     * @return o índice inteiro correspondente à opção selecionada
     */
    public int getChoice(String title, List<String> options, String prompt)
    {
        boolean waiting = true;
        int     choice  = -1;
        String errorMessage = null;

        while (waiting)
        {
            ConsoleScanner.clearScreen();
            if (errorMessage != null) {
                System.out.println(ConsoleColors.RED + errorMessage + ConsoleColors.RESET);
                errorMessage = null; 
            }
            System.out.println(title);
            for (int count = 0; count < options.size(); count++)
            {
                System.out.println(count + " - " + options.get(count));
            }
            System.out.println(prompt);
            try {
                String input = this.scanner.nextLine().trim();
                choice = Integer.parseInt(input);
                waiting = (choice < 0) || (choice >= options.size());
                if (waiting) {
                    errorMessage = messages.get("menu.invalidInput") + " " + (options.size() - 1) + ".";
                }
            }
            catch (NumberFormatException exception) {
                errorMessage = messages.get("menu.invalidInput") + " " + (options.size() - 1) + ".";
                choice = -1;
            }
        }
        return (choice);
    }
}
