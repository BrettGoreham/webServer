package webServer.words;

import database.WordSetDao;
import model.words.LanguageCharacterSet;
import model.words.ListOfWordSetsResultSet;
import model.words.WordSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebInputException;

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

        List<WordSet> baseWordSet = getAllPossibleWordSetsFromWordDictionary(validCharacterSet, input, minLength, input.length(), 0); //combine dictionary into like words. as they are treated the same.

        baseWordSet = groupLikeWordSets(baseWordSet);

        int[] inputRepresentation = WordSet.getInputCharacterSetArrayForString(validCharacterSet, input);
        List<ListOfWordSetsResultSet> combinationsOfWordSetsPossible = combineBaseWordSetToMakeAllCombinationsPossible(validCharacterSet, inputRepresentation, baseWordSet);

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

        int wildcards = (int) input.chars().filter(ch -> ch == '?').count();
        input = input.replace("?", "");
        validateInputForCharacterSet(validCharacterSet, input);

        List<WordSet> possibleWordSets = getAllPossibleWordSetsFromWordDictionary(validCharacterSet, input,  minLength, input.length() + wildcards, wildcards);

        return possibleWordSets
            .stream()
            .flatMap(wordSet -> wordSet.getWordsInSet().stream())
            .collect(Collectors.toList());
    }

    private List<ListOfWordSetsResultSet> combineBaseWordSetToMakeAllCombinationsPossible(LanguageCharacterSet languageCharacterSet, int[] inputRepresentation, List<WordSet> baseWordSet) {
        List<ListOfWordSetsResultSet> combinationsRemainingCharacterMap =
                createBaseMapOfWordSetRemainingCharacterSet(languageCharacterSet, inputRepresentation, baseWordSet);

        List<ListOfWordSetsResultSet> resultSet = new ArrayList<>();

        while (!combinationsRemainingCharacterMap.isEmpty()) {
            ListOfWordSetsResultSet wordSetsAndRemainingCombination = combinationsRemainingCharacterMap.remove(0);

            boolean newComboCreated = false;
            for (WordSet wordSet : baseWordSet) {
                boolean possibleToCombine = true;
                int[] newRemainingCharacters = new int[inputRepresentation.length];

                for(int i = 0; i < inputRepresentation.length; i++) {
                    int letterAmountRemaining = wordSetsAndRemainingCombination.getRemainingCharacters()[i] - wordSet.createOrGetRepresentativeLetterArray(languageCharacterSet)[i];

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

    private List<ListOfWordSetsResultSet> createBaseMapOfWordSetRemainingCharacterSet(LanguageCharacterSet languageCharacterSet, int[] inputRepresentation, List<WordSet> baseWordSet) {
        List<ListOfWordSetsResultSet> combinationsRemainingCharacters = new ArrayList<>();

        for (WordSet wordSet : baseWordSet) {
            int[] remainingChars = new int[inputRepresentation.length];

            for(int i = 0; i<inputRepresentation.length; i++) {
                remainingChars[i] = inputRepresentation[i] - wordSet.createOrGetRepresentativeLetterArray(languageCharacterSet)[i];
            }

            combinationsRemainingCharacters.add(new ListOfWordSetsResultSet(List.of(wordSet), remainingChars));
        }

        return combinationsRemainingCharacters;

    }

    private List<WordSet> getAllPossibleWordSetsFromWordDictionary(LanguageCharacterSet languageCharacterSet, String input, int minLength, int maxLength, int wildcards) {
        List<WordSet> wordSets = new ArrayList<>();

        char[] inputArray = input.toCharArray();
        Arrays.sort(inputArray);
        input = String.valueOf(inputArray);

        List<String> words = wordSetDao.getWordsForLanguage(languageCharacterSet, minLength, maxLength);

        for (String word : words) {
            //apostrophes need to be ignored as they are not part of spelling.
            String wordWithoutApostrophes = word.toLowerCase().replace("'", "");

            char[] ar = wordWithoutApostrophes.toCharArray();
            Arrays.sort(ar);
            String sorted = String.valueOf(ar);

            if (isWordPossible(sorted, input, wildcards)) {
                wordSets.add(
                    new WordSet(
                        sorted,
                        word
                    )
                );
            }
        }

        return wordSets;
    }

    private List<WordSet> groupLikeWordSets(List<WordSet> wordSets) {
        HashMap<String, List<WordSet>> collect = wordSets
            .stream()
            .collect(
                Collectors.groupingBy(
                    WordSet::getOrderedCharacters,
                    HashMap::new,
                    Collectors.toList()
                )
            );

        List<WordSet> groupWordSet = new ArrayList<>();
        for (List<WordSet> set : collect.values()) {
            WordSet grouped = new WordSet(
                set.get(0).getOrderedCharacters(),
                set.stream()
                    .flatMap(wordSet -> wordSet.getWordsInSet().stream())
                    .collect(Collectors.toList())
            );

            groupWordSet.add(grouped);

        }

        return groupWordSet;

    }

    //ensure input is sorted before getting here
    private boolean isWordPossible(String wordToCheck, String input, int wildcards) {

        int wordSetCount = 0;
        int inputCount = 0;
        while (inputCount < input.length()) {
            char inputChar = input.charAt(inputCount);
            char wordSetChar = wordToCheck.charAt(wordSetCount);
            if( inputChar == wordSetChar) {
                inputCount++;
                wordSetCount++;
                if(wordSetCount == wordToCheck.length()) {
                    return true;
                }
            } else if (inputChar < wordSetChar){
                inputCount++;
            }
            else if (wildcards > 0) {
                wildcards--;
                wordSetCount++;
                if(wordSetCount == wordToCheck.length()) {
                    return true;
                }
            }
            else {
                return false;
            }
        }

        return wildcards >= wordToCheck.length() - wordSetCount;
    }

    private void validateInputForCharacterSet(LanguageCharacterSet validCharacterSet, String input) {
        for(int i = 0; i <input.length(); i++) {
            if (!validCharacterSet.getCharacterSet().contains(input.charAt(i))) {
                throw new ServerWebInputException("Character "+ input.charAt(i) +" Not in character set for language :"+ validCharacterSet.getFullName());
            };
        }
    }

    public LanguageCharacterSet getLanguage(String language) {
        Optional<LanguageCharacterSet> languageCharacterSet = LanguageCharacterSet.getLanguageCharacterSetFromLanguageCode(language);

        if (languageCharacterSet.isPresent()) {
            return languageCharacterSet.get();
        } else {
            throw new ServerWebInputException("Language " + language + " is not found in language set");
        }
    }
}
