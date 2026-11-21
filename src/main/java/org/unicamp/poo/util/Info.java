package org.unicamp.poo.util;

/**
 * Armazena metadados institucionais, créditos de autoria, papéis de desenvolvimento,
 * licença e versão da aplicação FT Coin.
 */
public class Info {
    /**
     * Relação de todos os desenvolvedores autores do projeto.
     */
    public static final String[] PROJECT_AUTHORS = {
            "Samuel Germiniani",
            "Miguel Barcellos",
            "Vitor Cunha",
            "Matheus Mastelini de Souza",
            "Luiza Brum Pires de Brito",
            "Gabriela Nogueira",
            "Gustavo Domingues Mancio",
            "Guilherme Kauã Batista da Silva",
    };

    /**
     * Tipo de licença de uso do software.
     */
    public static final String PROJECT_LICENSE = "MIT License";

    /**
     * Declaração de copyright contendo o ano e os nomes dos autores.
     */
    public static final String PROJECT_COPYRIGHT = "Copyright (c) 2026 " + getFormattedAuthors();

    /**
     * Faculdade ou departamento de realização do projeto acadêmico.
     */
    public static final String PROJECT_DEPARTMENT = "FT - School of Technology";

    /**
     * Nome da instituição de ensino superior promotora do projeto.
     */
    public static final String PROJECT_INSTITUTION = "Unicamp - University of Campinas";

    /**
     * Nome do projeto.
     */
    public static final String PROJECT_NAME = "FT_Coin";

    /**
     * Versão do projeto.
     */
    public static final String PROJECT_VERSION = "Ver. 1.0";

    /**
     * Construtor privado para prevenir instanciação de classe utilitária de constantes.
     */
    private Info() {
    }

    /**
     * Registro que correlaciona uma função/responsabilidade de desenvolvimento aos respectivos autores.
     *
     * @param role    a chave de localização do papel na internacionalização
     * @param authors a lista de nomes dos autores responsáveis
     */
    public record AuthorRole(String role, String[] authors) {
        /**
         * Retorna os nomes dos autores do papel concatenados por vírgula.
         *
         * @return texto formatado com os autores
         */
        public String getFormattedAuthors() {
            return String.join(", ", authors);
        }
    }

    /**
     * Divisão das responsabilidades e papéis da equipe no projeto.
     */
    public static final AuthorRole[] projectRoles = {
            new AuthorRole("help.credits.role.management", new String[]{"Miguel Barcellos"}),
            new AuthorRole("help.credits.role.architecture", new String[]{"Gabriela Nogueira"}),
            new AuthorRole("help.credits.role.viewController", new String[]{"Luiza Brum"}),
            new AuthorRole("help.credits.role.modelDao", new String[]{"Matheus Mastelini", "Vitor Cunha"}),
            new AuthorRole("help.credits.role.databaseOracle", new String[]{"Guilherme Kauã", "Gustavo Domingues", "Samuel Germiniani"}),
    };

    /**
     * Retorna a identificação consolidada de nome e versão da aplicação.
     *
     * @return texto contendo nome e versão do projeto
     */
    public static String getStamp()
    {
        return (PROJECT_NAME + " " + PROJECT_VERSION);
    }

    /**
     * Retorna a lista de todos os autores do projeto separados por vírgula.
     *
     * @return representação em {@link String} contendo os autores
     */
    public static String getFormattedAuthors() {
        return String.join(", ", PROJECT_AUTHORS);
    }
}
