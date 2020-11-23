public class Main {



    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        System.out.println(grammar.getNonTerminals());
        System.out.println(grammar.getTerminals());
        System.out.println(grammar.getStartingSymbol());
        System.out.println(grammar.getProductions());
        System.out.println(grammar.getProductionsForNonTerminal("A"));
    }
}
