package database;

import model.LanguageCharacterSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WordSetDaoTest {

    @InjectMocks
    WordSetDao wordSetDao;

    @Mock
    ApplicationContext resourceLoader;


    @Test
    public void testEnglish()  {
        FileSystemResource file = new FileSystemResource("src/main/resources/dictionaries/en_dictionary.txt");
        when(resourceLoader.getResource(Mockito.anyString())).thenReturn(file);

        List<String> words = wordSetDao.getWordsForLanguage(LanguageCharacterSet.ENGLISH, 0, 99);

        Assertions.assertEquals(4444, words.size());//fun that its 4444 but thats actually how many words were generated hahaha
    }
}
