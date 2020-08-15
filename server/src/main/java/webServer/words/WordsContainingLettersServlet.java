package webServer.words;

import model.LanguageCharacterSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/words")
public class WordsContainingLettersServlet {

    final
    WordsContainingLettersService wordsContainingLettersService;

    public WordsContainingLettersServlet(WordsContainingLettersService wordsContainingLettersService) {
        this.wordsContainingLettersService = wordsContainingLettersService;
    }

    @GetMapping("")
    public String wordsHome() {
        return "words/wordsLanding";
    }

    @GetMapping(path = "find")
    public String getWords(@RequestParam String language, @RequestParam String input, @RequestParam(defaultValue = "0") int minLength, Model model) {
        List<String> words = wordsContainingLettersService.findWordsPossibleWithInputSequence(language, input.replaceAll("\\s","").toLowerCase(), minLength);

        List<List<String>> groupedWords =
            new ArrayList<>(words
                                .stream()
                                .collect(
                                    Collectors.groupingBy(
                                        String::length, HashMap::new, Collectors.toList())
                                )
                                .values());

        // sort groups by the size want list of longer words first
        groupedWords.sort((lista, listb) -> Integer.compare(listb.get(0).length(),lista.get(0).length()));

        model.addAttribute("language", LanguageCharacterSet.getLanguageCharacterSetFromLanguageCode(language).get().getFullName());
        model.addAttribute("input", input);
        model.addAttribute("minLength", minLength);
        model.addAttribute("words", groupedWords);


        return "words/wordsSearch";
    }

    @GetMapping(path = "findcombos")
    public String getCombosOfWords(@RequestParam String language, @RequestParam String input, @RequestParam(defaultValue = "0") int minLength, @RequestParam(defaultValue = "50") int expectedResultSize) {
        List<List<String>> words = wordsContainingLettersService.findWordCombinationsPossibleWithInputSequence(language, input, minLength, expectedResultSize);


        return "words/wordsLanding";
    }
}
