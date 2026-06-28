package org.unicamp.poo.util;

public class Info {
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
    public static final String PROJECT_LICENSE = "MIT License";
    public static final String PROJECT_COPYRIGHT = "Copyright (c) 2026 " + getFormattedAuthors();
    public static final String PROJECT_DEPARTMENT = "FT - School of Technology";
    public static final String PROJECT_INSTITUTION = "Unicamp - University of Campinas";

    public static final String PROJECT_NAME = "FT_Coin";
    public static final String PROJECT_VERSION = "Ver. 1.0";

    // Representa um cargo e seus responsáveis
    public record AuthorRole(String role, String[] authors) {
        public String getFormattedAuthors() {
            return String.join(", ", authors);
        }
    }

    // Membros da equipe agrupados por cargo.
    public static final AuthorRole[] projectRoles = {
            new AuthorRole("help.credits.role.management", new String[]{"Miguel Barcellos"}),
            new AuthorRole("help.credits.role.architecture", new String[]{"Gabriela Nogueira"}),
            new AuthorRole("help.credits.role.viewController", new String[]{"Luiza Brum"}),
            new AuthorRole("help.credits.role.modelDao", new String[]{"Matheus Mastelini", "Vitor Cunha"}),
            new AuthorRole("help.credits.role.databaseOracle", new String[]{"Guilherme Kauan", "Gustavo Domingues", "Samuel Germiniani"}),
    };

    public static String getStamp()
    {
        return (PROJECT_NAME + " " + PROJECT_VERSION);
    }

    public static String getFormattedAuthors() {
        return String.join(", ", PROJECT_AUTHORS);
    }
}
