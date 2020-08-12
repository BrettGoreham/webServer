package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListOfWordSetsResultSet {
    public ListOfWordSetsResultSet(List<WordSet> wordSets, int[] remainingCharacters) {
        this.wordSets =wordSets;
        this.remainingCharacters = remainingCharacters;
    }
    private final List<WordSet> wordSets;
    private final int[] remainingCharacters;
    private Integer numberOfRemainingLetters;

    public List<WordSet> getWordSets() {
        return wordSets;
    }

    public int[] getRemainingCharacters() {
        return remainingCharacters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListOfWordSetsResultSet that = (ListOfWordSetsResultSet) o;
        return wordSets.containsAll(that.wordSets);
    }

    //this method exists for sorting lists of these lists so you dont have to recalculate every comparison.
    //reduces calculation time by a lot.
    public int getOrCalculateNumberOfRemainingLetters() {
        if (numberOfRemainingLetters == null) {
            numberOfRemainingLetters = calculateNumberOfRemainingLetters();
        }
        return numberOfRemainingLetters;

    }

    public int calculateNumberOfRemainingLetters() {
        int count = 0;

        for (int value : remainingCharacters) {
            count += value;
        }

        return count;
    }

    public int calculateNumberOfResultsFromWordSets() {
        int results = 1;
        for(WordSet wordSet : wordSets) {
            results *= wordSet.getWordsInSet().size();
        }

        return results;
    }
    public List<List<String>> getCombinationOfAllPossible() {
        List<List<String>> allCombinations = null;

        for (WordSet wordSet : wordSets) {
            if (allCombinations == null) {
                allCombinations = initializeAllCombinations(wordSet);
            }
            else if (wordSet.getWordsInSet().size() == 1) {
                addWordToAllCombinations(allCombinations, wordSet.getWordsInSet().get(0));
            }
            else {
                allCombinations = addAllWordsInSetToAllCombinations(allCombinations, wordSet);
            }
        }

        return allCombinations;
    }

    private List<List<String>> initializeAllCombinations(WordSet wordSet) {
        List<List<String>> allCombinations = new ArrayList<>();

        for (String s :  wordSet.getWordsInSet()) {
            List<String> stringList = new ArrayList<>();
            stringList.add(s);
            allCombinations.add(stringList);
        }

        return allCombinations;
    }

    private void addWordToAllCombinations(List<List<String>> allCombinations, String s) {
        for(List<String> combination : allCombinations) {
            combination.add(s);
        }
    }

    private List<List<String>> addAllWordsInSetToAllCombinations(List<List<String>> allCombinations, WordSet wordSet) {
        List<List<String>> newResultList = new ArrayList<>();

        for (String s : wordSet.getWordsInSet()) {
            List<List<String>> allCombinationsCopy = getShallowCopyOfAllCombinations(allCombinations);

            for(List<String> list : allCombinationsCopy) {
                list.add(s);
            }

            newResultList.addAll(allCombinationsCopy);
        }

        return newResultList;
    }

    private List<List<String>> getShallowCopyOfAllCombinations(List<List<String>> allCombinations) {
        List<List<String>> copy = new ArrayList<>();

        for (List<String> comboList : allCombinations) {
            copy.add(new ArrayList<>(comboList));
        }

        return copy;
    }
}
