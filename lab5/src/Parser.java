import java.util.*;

public class Parser {

    private Grammar grammar;
    private List<Map<String, List<String>>> firsts;
    private List<Map<String, List<String>>> follows;

    public Parser() {
        grammar = new Grammar();
        firsts = new ArrayList<>();
        follows = new ArrayList<>();
    }


    void first() {
        //TODO

        // adding FIRST of terminal
        Map<String,List<String>> values = new HashMap<>();
        for (String terminal: grammar.getTerminals()) {
            values.put(terminal, Collections.singletonList(terminal));
        }
        // firsts.add(values);

        // adding FIRST for non terminals
        int index = 0;
        for (String nonTerminal: grammar.getNonTerminals()) {
            List<String> firstOfNonTerminal = new ArrayList<>();
            List<Map.Entry<String, List<String>>> productions = grammar.getProductionsForNonTerminal(nonTerminal);
            for (Map.Entry<String, List<String>> production: productions) {
                List<String> elements = production.getValue();
                if (grammar.isTerminal(elements.get(0)))
                    firstOfNonTerminal.add(elements.get(0));
            }
            values.put(nonTerminal, firstOfNonTerminal);
        }

    }

    void follow(String nonTerminal) {
        //TODO
    }

}
