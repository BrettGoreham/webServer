package database;

import model.words.LanguageCharacterSet;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//This doesnt actually use the database because i dont want to store 5000 words.
@Repository
public class WordSetDao {
    private static final String classpath = "classpath:dictionaries/";
    private static final String dictionary = "_dictionary.txt";

    private final ApplicationContext resourceLoader;

    public WordSetDao(ApplicationContext resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<String> getWordsForLanguage(LanguageCharacterSet languageCharacterSet, int minLength, int maxLength) {
        String dictionaryClassPath =
            classpath +
                languageCharacterSet.getLangCode() +
                dictionary;

        Resource resource = resourceLoader.getResource(dictionaryClassPath);

        Scanner scanner;
        try {
            scanner = new Scanner(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("No Dictionary Found For language: " + languageCharacterSet.getLangCode(), e);
        }

        List<String> dictionary = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            int wordLength = line.replace("'", "").length();
            if (!line.isBlank()
                && !line.startsWith("#")
                && wordLength <= maxLength
                && wordLength >= minLength) {

               dictionary.add(line);
            }
        }

        return dictionary;
    }
}
