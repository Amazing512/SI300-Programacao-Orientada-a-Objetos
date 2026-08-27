package org.unicamp.poo.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {
    private final String bundleFileName;
    private ResourceBundle resourceBundle;

    public MessageProvider(String bundleFileName, String lang, String country){
        this.bundleFileName = bundleFileName;
        setLanguage(lang, country);
    }

    public void setLanguage(String lang, String country) {
        Locale newLocale = Locale.of(lang, country);
        Locale.setDefault(newLocale);
        this.resourceBundle = ResourceBundle.getBundle(this.bundleFileName, newLocale);
    }

    public String get(String key)
    {
        return (resourceBundle.containsKey(key) ? resourceBundle.getString(key) : ("!!! " + key + "!!!"));
    }
}
