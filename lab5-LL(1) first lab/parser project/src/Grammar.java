import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Grammar {

    private List<String> nonTerminals;
    private List<String> terminals;
    private String startingSymbol;
    private List<Map.Entry<String, List<String>>> productions;


    public Grammar() {
        nonTerminals = new ArrayList<>();
        terminals = new ArrayList<>();
        startingSymbol = "";
        productions = new ArrayList<>();

        readFromFile();
    }

    private void readFromFile() {
        File grammarFile = new File("src/g1.txt");
        try {
            Scanner scanner = new Scanner(grammarFile);

            //reading the non terminals
            String line = scanner.nextLine();
            String[] nonTerminals = line.split(",");
            this.nonTerminals.addAll(Arrays.asList(nonTerminals));

            //reading the terminals
            line = scanner.nextLine();
            String[] terminals = line.split(",");
            this.terminals.addAll(Arrays.asList(terminals));

            // reading the starting symbol
            line = scanner.nextLine();
            startingSymbol = line;

            //reading the productions
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                String[] production = line.split(",");
                String key = production[0];
                String[] productions = production[1].split(" ");
                this.productions.add(Map.entry(key, Arrays.asList(productions)));
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<String> getNonTerminals() {
        return nonTerminals;
    }

    public List<String> getTerminals() {
        return terminals;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public List<Map.Entry<String, List<String>>> getProductions() {
        return productions;
    }

    public List<Map.Entry<String, List<String>>> getProductionsForNonTerminal(String nonTerminal) {

        List<Map.Entry<String, List<String>>> list = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : productions) {
            if (entry.getKey().equals(nonTerminal))
                list.add(entry);
        }

        return list;
    }

    public List<Map.Entry<String, List<String>>> getRightHandSideProductionsForNonTerminal(String nonTerminal) {

        List<Map.Entry<String, List<String>>> list = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : productions) {
            if (entry.getValue().contains(nonTerminal))
                list.add(entry);
        }

        return list;
    }



    boolean isTerminal(String terminal) {
        return terminals.contains(terminal) || terminal.equals("epsilon");
    }
}
