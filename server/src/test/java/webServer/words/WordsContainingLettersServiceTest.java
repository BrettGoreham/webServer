package webServer.words;


import database.WordSetDao;
import model.LanguageCharacterSet;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WordsContainingLettersServiceTest {

    @InjectMocks
    WordsContainingLettersService wordsContainingLettersService;

    @Mock
    WordSetDao wordSetDao;

    List<String> words = List.of("word", "words", "word's", "one", "two", "three", "abcdefghijklmnopqrstuvwxyz", "what", "thaw");// this is here until i get a dictionary or make one

    @BeforeEach
    public void beforeEach(){
        when(wordSetDao.getWordsForLanguage(any(LanguageCharacterSet.class), anyInt())).thenReturn(words);
    }

    @Test
    public void testEnglishContainsTwo() {
        List<String> words =wordsContainingLettersService.findWordsPossibleWithInputSequence("en", "two", 0);

        Assertions.assertTrue(words.contains("two"));
    }

    //the dictionary im using as words and word's
    //at current time im treating these as two different results so should appear twice.
    @Test
    public void testEnglishContainsTwoVariantsOfWords() {
        List<String> words = wordsContainingLettersService.findWordsPossibleWithInputSequence("en", "words", 0);

        Assertions.assertTrue(words.containsAll(List.of("words", "word's")));
    }

    @Test
    public void testEnglishContainsTwoAndThree() {
        List<String> words = wordsContainingLettersService.findWordsPossibleWithInputSequence("en", "twohree", 0);

        Assertions.assertTrue(words.contains("two"));
        Assertions.assertTrue(words.contains("three"));
        Assertions.assertFalse(words.contains("what"));
        Assertions.assertFalse(words.contains("thaw"));

    }

    //this is the same as the last one but with an A so what and thaw are possible as well
    @Test
    public void testEnglishContainsTwoThreeWhatAndThaw() {
        List<String> words = wordsContainingLettersService.findWordsPossibleWithInputSequence("en", "twohreea", 0);

        Assertions.assertTrue(words.contains("two"));
        Assertions.assertTrue(words.contains("three"));
        Assertions.assertTrue(words.contains("what"));
        Assertions.assertTrue(words.contains("thaw"));
    }

    @Test
    public void testEnglishMultipleLettersInWordRequireMultipleLettersInInput() {
        List<String> words = wordsContainingLettersService.findWordsPossibleWithInputSequence("en", "thre", 0); // missing extra e for three

        Assertions.assertFalse(words.contains("three"));
    }

    @Test
    public void testEnglishCombinationsOfTwoAndThree(){
        List<List<String>> combinations = wordsContainingLettersService.findWordCombinationsPossibleWithInputSequence("en", "threetwo", 0, 50);

        Assertions.assertTrue(combinations.get(0).containsAll(List.of("two", "three")));
    }

    @Test
    public void testEnglishCombinationWhenOneOrMoreWordExistsWithSameLetters() {
        List<List<String>> combinations =
            wordsContainingLettersService.findWordCombinationsPossibleWithInputSequence("en", "twowhat", 0, 50);


        assertThatListContainsListThatContainsExactly(List.of("what", "two"), combinations);
        assertThatListContainsListThatContainsExactly(List.of("thaw", "two"), combinations);
    }

    private void assertThatListContainsListThatContainsExactly(List<String> expectedResult, List<List<String>> resultSet) {
        for (List<String> resultInResultSet : resultSet) {
            if (resultInResultSet.containsAll(expectedResult)) {
                return;
            }
        }

        fail("Expected result not contained In Result Set \n\t Expected: " + expectedResult + "\n\t ResultSet: " + resultSet);
    }
}
