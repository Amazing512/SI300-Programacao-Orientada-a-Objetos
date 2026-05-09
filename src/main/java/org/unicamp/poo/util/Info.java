package org.unicamp.poo.util;

public class Info {
    public static final String[] projectAuthors      = {
            "Samuel Germiniani",
            "Miguel Barcellos",
            "Vitor Cunha",
            "Matheus Mastelini de Souza",
            "Luiza Brum Pires de Brito",
            "Gabriela Nogueira",
            "Gustavo Domingues Mancio",
            "Guilherme Kauã Batista da Silva",
    };
    public static final String projectLicense   = "MIT License";
    public static final String projectCopyRight   = "Copyright (c) 2026 " + getFormattedAuthors();
    public static final String projectDepartment  = "FT - School of Technology";
    public static final String projectInstitution = "Unicamp - University of Campinas";

    public static final String projectName        = "FT_Coin";
    public static final String projectVersion     = "Ver. 1.0";

    public static String getStamp()
    {
        return (projectName + " Ver. " + projectVersion);
    }

    public static String getFormattedAuthors() {
        return String.join(", ", projectAuthors);
    }
}
