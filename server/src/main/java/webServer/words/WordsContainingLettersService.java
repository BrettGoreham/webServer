package webServer.words;

import database.WordSetDao;
import model.LanguageCharacterSet;
import model.ListOfWordSetsResultSet;
import model.WordSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebInputException;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WordsContainingLettersService {

    private WordSetDao wordSetDao;
    public WordsContainingLettersService(WordSetDao wordSetDao) {
        this.wordSetDao = wordSetDao;
    }

    public List<List<String>> findWordCombinationsPossibleWithInputSequence(String language, String input, int minLength, int numResultsExpected) {
        LanguageCharacterSet validCharacterSet = getLanguage(language);
        validateInputForCharacterSet(validCharacterSet, input);

        int[] inputRepresentation = getInputCharacterSetArrayForString(validCharacterSet, input);

        List<WordSet> wordSets = createWordSetsFromWordDictionary(validCharacterSet, minLength); //combine dictionary into like words. as they are treated the same.
        List<WordSet> baseWordSet = getPossibleWordsFromAvailableWordRepresentation(inputRepresentation, wordSets);

        List<ListOfWordSetsResultSet> combinationsOfWordSetsPossible = combineBaseWordSetToMakeAllCombinationsPossible(inputRepresentation, baseWordSet);

        combinationsOfWordSetsPossible.sort(Comparator.comparing(ListOfWordSetsResultSet::getOrCalculateNumberOfRemainingLetters));
        
        return getFinalResultList(combinationsOfWordSetsPossible, numResultsExpected);
    }

    private List<List<String>> getFinalResultList(List<ListOfWordSetsResultSet> combinationsOfWordSetsPossible, int numResultsExpected) {
        List<ListOfWordSetsResultSet> output = new ArrayList<>();

        int indexInWords = 0;
        int count = 0;
        while (count < numResultsExpected && indexInWords < combinationsOfWordSetsPossible.size()) {
            ListOfWordSetsResultSet resultSet = combinationsOfWordSetsPossible.get(indexInWords);
            count += resultSet.calculateNumberOfResultsFromWordSets();
            output.add(resultSet);
            indexInWords++;
        }

        return output
            .stream()
            .map(ListOfWordSetsResultSet::getCombinationOfAllPossible)
            .flatMap(Collection::stream)
            .limit(numResultsExpected)
            .collect(Collectors.toList());

    }


    public List<String> findWordsPossibleWithInputSequence(String language, String input, int minLength) {
        LanguageCharacterSet validCharacterSet = getLanguage(language);
        validateInputForCharacterSet(validCharacterSet, input);

        int[] inputRepresentation = getInputCharacterSetArrayForString(validCharacterSet, input);

        List<WordSet> wordSets = createWordSetsFromWordDictionary(validCharacterSet, minLength);
        List<WordSet> possibleWordSets = getPossibleWordsFromAvailableWordRepresentation(inputRepresentation, wordSets);

        return possibleWordSets
            .stream()
            .flatMap(wordSet -> wordSet.getWordsInSet().stream())
            .sorted(Comparator.comparing(String::length))
            .collect(Collectors.toList());
    }

    private List<ListOfWordSetsResultSet> combineBaseWordSetToMakeAllCombinationsPossible(int[] inputRepresentation, List<WordSet> baseWordSet) {
        List<ListOfWordSetsResultSet> combinationsRemainingCharacterMap =
                createBaseMapOfWordSetRemainingCharacterSet(inputRepresentation, baseWordSet);

        List<ListOfWordSetsResultSet> resultSet = new ArrayList<>();

        while (!combinationsRemainingCharacterMap.isEmpty()) {
            ListOfWordSetsResultSet wordSetsAndRemainingCombination = combinationsRemainingCharacterMap.remove(0);

            boolean newComboCreated = false;
            for (WordSet wordSet : baseWordSet) {
                boolean possibleToCombine = true;
                int[] newRemainingCharacters = new int[inputRepresentation.length];

                for(int i = 0; i < inputRepresentation.length; i++) {
                    int letterAmountRemaining = wordSetsAndRemainingCombination.getRemainingCharacters()[i] - wordSet.getRepresentativeLetterArray()[i];

                    if (letterAmountRemaining >= 0) {
                        newRemainingCharacters[i] = letterAmountRemaining;
                    }
                    else {
                        possibleToCombine = false;
                        break;
                    }
                }

                if (possibleToCombine) {
                    newComboCreated = true;

                    List<WordSet> newWordSetList = new ArrayList<>(wordSetsAndRemainingCombination.getWordSets());
                    newWordSetList.add(wordSet);

                    ListOfWordSetsResultSet newWordSetAndRemainingCombination = new ListOfWordSetsResultSet(newWordSetList, newRemainingCharacters);

                    if (!combinationsRemainingCharacterMap.contains(newWordSetAndRemainingCombination)) {
                        combinationsRemainingCharacterMap.add(newWordSetAndRemainingCombination);
                    }


                }
            }

            if (!newComboCreated) {
                resultSet.add(wordSetsAndRemainingCombination);
            }
        }
        
        return resultSet;
    }

    private List<ListOfWordSetsResultSet> createBaseMapOfWordSetRemainingCharacterSet(int[] inputRepresentation, List<WordSet> baseWordSet) {
        List<ListOfWordSetsResultSet> combinationsRemainingCharacters = new ArrayList<>();

        for (WordSet wordSet : baseWordSet) {
            int[] remainingChars = new int[inputRepresentation.length];

            for(int i = 0; i<inputRepresentation.length; i++) {
                remainingChars[i] = inputRepresentation[i] - wordSet.getRepresentativeLetterArray()[i];
            }

            combinationsRemainingCharacters.add(new ListOfWordSetsResultSet(List.of(wordSet), remainingChars));
        }

        return combinationsRemainingCharacters;

    }

    private List<WordSet> createWordSetsFromWordDictionary(LanguageCharacterSet languageCharacterSet, int minLength) {
        List<WordSet> wordSets = new ArrayList<>();

        List<String> words = wordSetDao.getWordsForLanguage(languageCharacterSet, minLength);

        for (String word : words) {
            //apostrophes need to be ignored as they are not part of spelling.
            String wordWithoutApostrophes = word.toLowerCase().replaceAll("'", "");

            char[] ar = wordWithoutApostrophes.toCharArray();
            Arrays.sort(ar);
            String sorted = String.valueOf(ar);

            Optional<WordSet> existingWordSet = wordSets.stream()
                .filter(wordSet -> wordSet.getOrderedCharacters().equals(sorted))
                .findFirst();

            if (existingWordSet.isPresent()) {
                existingWordSet.get().getWordsInSet().add(word);
            }
            else {
                wordSets.add(
                    new WordSet(
                        sorted,
                        getInputCharacterSetArrayForString(languageCharacterSet, sorted),
                        word
                    )
                );
            }
        }
        return wordSets;
    }

    private List<WordSet> getPossibleWordsFromAvailableWordRepresentation(
            int[] inputRepresentation,
            List<WordSet> wordSets) {

        List<WordSet> possibleWordSets = new ArrayList<>();
        for (WordSet wordSet :  wordSets) {

            boolean isWordPossible = true;
            int[] wordRepresentation = wordSet.getRepresentativeLetterArray();
            for (int i =0; i < wordRepresentation.length ; i++) {
                if (inputRepresentation[i] - wordRepresentation[i] < 0) {
                    isWordPossible = false;
                    break;
                }
            }
            if (isWordPossible) {
                possibleWordSets.add(wordSet);
            }
        }
        return possibleWordSets;
    }

    private int[] getInputCharacterSetArrayForString(LanguageCharacterSet languageCharacterSet, String string){
        int min = languageCharacterSet.getCharacterSet().stream().min(Character::compareTo).get();
        int max = languageCharacterSet.getCharacterSet().stream().max(Character::compareTo).get();

        int[] characterSetArray = new int[max-min + 1];

        for(int i = 0; i < string.length(); i++) {
            characterSetArray[string.charAt(i) - min]++;
        }

        return characterSetArray;
    }

    private void validateInputForCharacterSet(LanguageCharacterSet validCharacterSet, String input) {
        for(int i = 0; i <input.length(); i++) {
            if (!validCharacterSet.getCharacterSet().contains(input.charAt(i))) {
                throw new ServerWebInputException("character: "+ input.charAt(i) +" Not in character set for language :"+ validCharacterSet.getLangCode());
            };
        }
    }

    public LanguageCharacterSet getLanguage(String language) {
        Optional<LanguageCharacterSet> languageCharacterSet = LanguageCharacterSet.getLanguageCharacterSetFromLanguageCode(language);

        if (languageCharacterSet.isPresent()) {
            return languageCharacterSet.get();
        } else {
            throw new ServerWebInputException("languageCodeNotFound");
        }
    }
}
