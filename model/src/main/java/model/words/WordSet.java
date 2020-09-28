package model.words;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WordSet {

    private String orderedCharacters;
    private List<String> wordsInSet;
    private int[] representativeLetterArray;

    public WordSet(String orderedCharacters, String... wordsInSet) {
        this.orderedCharacters = orderedCharacters;
        this.wordsInSet = new ArrayList<>(Arrays.asList(wordsInSet));
    }

    public WordSet(String orderedCharacters, List<String> wordsInSet) {
        this.orderedCharacters = orderedCharacters;
        this.wordsInSet = wordsInSet;
    }
    public String getOrderedCharacters() {
        return orderedCharacters;
    }

    public void setOrderedCharacters(String orderedCharacters) {
        this.orderedCharacters = orderedCharacters;
    }

    public List<String> getWordsInSet() {
        return wordsInSet;
    }

    public void setWordsInSet(List<String> wordsInSet) {
        this.wordsInSet = wordsInSet;
    }

    public int[] createOrGetRepresentativeLetterArray(LanguageCharacterSet lang) {
        if (representativeLetterArray != null) {
            return representativeLetterArray;
        }
        return getInputCharacterSetArrayForString(lang, this.orderedCharacters);
    }

    public static int[] getInputCharacterSetArrayForString(LanguageCharacterSet languageCharacterSet, String string){
        int min = languageCharacterSet.getCharacterSet().stream().min(Character::compareTo).get();
        int max = languageCharacterSet.getCharacterSet().stream().max(Character::compareTo).get();

        int[] characterSetArray = new int[max-min + 1];

        for(int i = 0; i < string.length(); i++) {
            characterSetArray[string.charAt(i) - min]++;
        }

        return characterSetArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordSet wordSet = (WordSet) o;
        return Objects.equals(orderedCharacters, wordSet.orderedCharacters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderedCharacters);
    }
}
