package org.unicamp.poo.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provedor de suporte à internacionalização e localização da aplicação.
 * Encapsula o {@link ResourceBundle} e viabiliza a alternância dinâmica de idioma durante a execução.
 */
public class MessageProvider {
    private ResourceBundle resourceBundle;

    /**
     * Constrói o provedor carregando as mensagens do arquivo bundle para a linguagem e país especificados.
     *
     * @param bundleFileName o nome base do arquivo de recursos (ex: "messages")
     * @param lang           o código do idioma (ex: "pt", "en", "es")
     * @param country        o código do país (ex: "BR", "US", "ES")
     */
    public MessageProvider(String bundleFileName, String lang, String country) {
        changeLanguage(bundleFileName, lang, country);
    }

    /**
     * Atualiza o idioma e a localidade ativa da aplicação, recarregando o {@link ResourceBundle}.
     *
     * @param bundleFileName o nome base do arquivo de recursos
     * @param lang           o código da nova linguagem
     * @param country        o código do novo país
     */
    public void changeLanguage (String bundleFileName, String lang, String country){
        Locale newLocale = Locale.of(lang, country);
        Locale.setDefault(newLocale);
        this.resourceBundle = ResourceBundle.getBundle(bundleFileName, newLocale);
    }

    /**
     * Recupera a mensagem localizada correspondente à chave fornecida.
     * Se a chave não existir no pacote de mensagens, retorna uma marcação com o nome da chave.
     *
     * @param key a chave da propriedade no arquivo de mensagens
     * @return a mensagem traduzida correspondente
     */
    public String get(String key) {
        return (resourceBundle.containsKey(key) ? resourceBundle.getString(key) : ("!!! " + key + "!!!"));
    }
}
