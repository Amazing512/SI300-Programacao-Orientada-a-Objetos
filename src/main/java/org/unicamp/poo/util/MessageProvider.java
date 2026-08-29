package org.unicamp.poo.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {
    private ResourceBundle resourceBundle;

    public MessageProvider(String bundleFileName, String lang, String country) {
        changeLanguage(bundleFileName, lang, country);
    }

    public void changeLanguage (String bundleFileName, String lang, String country){
        Locale newLocale = Locale.of(lang, country);
        this.resourceBundle = ResourceBundle.getBundle(bundleFileName, newLocale);
    }

    public String get(String key) {
        return (resourceBundle.containsKey(key) ? resourceBundle.getString(key) : ("!!! " + key + "!!!"));
    }
}
