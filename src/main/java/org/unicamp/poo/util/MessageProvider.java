package org.unicamp.poo.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {
    private final ResourceBundle resourceBundle;

    public MessageProvider(String bundleFileName, String lang, String country)
    {
        //noinspection deprecation
        Locale.setDefault(new Locale(lang, country));
        this.resourceBundle = ResourceBundle.getBundle(bundleFileName, Locale.getDefault());
    }

    public String get(String key)
    {
        return (resourceBundle.containsKey(key) ? resourceBundle.getString(key) : ("!!! " + key + "!!!"));
    }
}
