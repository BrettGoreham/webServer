package model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum LanguageCharacterSet {

    ENGLISH("en",'a','b','c','d','e','f','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z');

    private final String langCode;
    private final List<Character> characterSet;
    LanguageCharacterSet(String langCode, Character... characterSet) {
        this.langCode = langCode;
        this.characterSet = Arrays.asList(characterSet);
    }

    public List<Character> getCharacterSet() {
        return characterSet;
    }

    public String getLangCode() {
        return langCode;
    }

    public static Optional<LanguageCharacterSet> getLanguageCharacterSetFromLanguageCode(String langCode) {
        return Arrays.stream(LanguageCharacterSet.values())
            .filter(languageCharacterSet -> languageCharacterSet.langCode.equals(langCode))
            .findFirst();
    }

}
