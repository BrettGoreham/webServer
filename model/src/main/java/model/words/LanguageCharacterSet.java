package model.words;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum LanguageCharacterSet {

    ENGLISH("en","English", 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'),
    Norwegian("no", "Norwegian", 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z', 'æ', 'ø', 'å');


    private final String langCode;
    private final String fullName;
    private final List<Character> characterSet;
    LanguageCharacterSet(String langCode, String fullName, Character... characterSet) {
        this.langCode = langCode;
        this.fullName = fullName;
        this.characterSet = Arrays.asList(characterSet);
    }

    public List<Character> getCharacterSet() {
        return characterSet;
    }

    public String getLangCode() {
        return langCode;
    }

    public String getFullName() {return fullName;}


    public static Optional<LanguageCharacterSet> getLanguageCharacterSetFromLanguageCode(String langCode) {
        return Arrays.stream(LanguageCharacterSet.values())
            .filter(languageCharacterSet -> languageCharacterSet.langCode.equals(langCode))
            .findFirst();
    }

}
