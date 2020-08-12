package webServer.words;

import model.ListOfWordSetsResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/words")
public class WordsContainingLettersServlet {

    @Autowired
    WordsContainingLettersService wordsContainingLettersService;

    @GetMapping("")
    public String wordsHome() {
        return "words";
    }

    @GetMapping(path = "find")
    public String getWords(@RequestParam String language, @RequestParam String input, @RequestParam(defaultValue = "0") int minLength) {
        List<String> words = wordsContainingLettersService.findWordsPossibleWithInputSequence(language, input, minLength);
        return "words";
    }

    @GetMapping(path = "findcombos")
    public String getCombosOfWords(@RequestParam String language, @RequestParam String input, @RequestParam(defaultValue = "0") int minLength, @RequestParam(defaultValue = "50") int expectedResultSize) {
        List<List<String>> words = wordsContainingLettersService.findWordCombinationsPossibleWithInputSequence(language, input, minLength, expectedResultSize);


        return "words";
    }
}
