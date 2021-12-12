package webServer.words;

import model.words.LanguageCharacterSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String wordsHome(Model model) {
        model.addAttribute("languages", LanguageCharacterSet.values());
        return "words/wordsLanding";
    }

    @GetMapping(path = "find")
    public String getWords(@RequestParam String language, @RequestParam String input, @RequestParam(defaultValue = "0") int minLength, RedirectAttributes attributes, Model model) {
        List<String> words;
        try {
             words = wordsContainingLettersService.findWordsPossibleWithInputSequence(language, input.replaceAll("\\s","").toLowerCase(), minLength);

        }
        catch (ServerWebInputException e) {
            attributes.addFlashAttribute("errorMessage", e.getReason());
            return "redirect:/words";
        }

        List<List<String>> groupedWords =
            new ArrayList<>(words
                                .stream()
                                .collect(
                                    Collectors.groupingBy(
                                        a -> a.replace("'", "").length(),
                                        HashMap::new,
                                        Collectors.toList())
                                )
                                .values());

        // sort groups by the size want list of longer words first
        groupedWords.sort(
            (lista, listb) ->
                Integer.compare(
                    listb.get(0).replace("'", "").length(),
                    lista.get(0).replace("'", "").length()
                )
        );

        for(List<String> wordsInGroup : groupedWords) {
            wordsInGroup.sort(
                Comparator.comparing(stringA -> stringA.replace("'", ""))
                );
        }

        model.addAttribute("languages", LanguageCharacterSet.values());
        model.addAttribute("selectedLanguage", LanguageCharacterSet.getLanguageCharacterSetFromLanguageCode(language).get().getFullName());
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
