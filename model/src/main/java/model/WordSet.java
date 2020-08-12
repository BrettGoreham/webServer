package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WordSet {

    private String orderedCharacters;
    private List<String> wordsInSet;
    private int[] representativeLetterArray;

    public WordSet(String orderedCharacters, int[] representativeLetterArray, String... wordsInSet) {
        this.orderedCharacters = orderedCharacters;
        this.representativeLetterArray = representativeLetterArray;
        this.wordsInSet = new ArrayList<>(Arrays.asList(wordsInSet));
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

    public int[] getRepresentativeLetterArray() {
        return representativeLetterArray;
    }

    public void setRepresentativeLetterArray(int[] representativeLetterArray) {
        this.representativeLetterArray = representativeLetterArray;
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
