import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private Grammar grammar;
    private List<Map<String, List<String>>> firsts;
    private List<Map<String, List<String>>> follows;
    private Map<Map.Entry<String, String>, Map.Entry<List<String>, Integer>> table;

    public Parser() throws MyException {
        grammar = new Grammar();
        firsts = new ArrayList<>();
        first();
        follows = new ArrayList<>();
        follow();
        table = constructTable();
    }

    public Map<String, List<String>> getFirst() {
        return firsts.get(firsts.size() - 1);
    }

    public Map<String, List<String>> getFollow() {
        return follows.get(follows.size() - 1);
    }

    public Map<Map.Entry<String, String>, Map.Entry<List<String>, Integer>> getTable() {
        return table;
    }

    //function that constructs the table of LL(1) parser
    Map<Map.Entry<String, String>, Map.Entry<List<String>, Integer>> constructTable() throws MyException {
        //here we will hold the calculated table
        Map<Map.Entry<String, String>, Map.Entry<List<String>, Integer>> table = new HashMap<>();
        //for the rows we add all symbols from grammar plus $
        List<String> rows = grammar.getNonTerminals();
        rows.addAll(grammar.getTerminals());
        rows.add("$");
        //for the columns we add all the terminals plus $
        List<String> columns = grammar.getTerminals();
        columns.add("$");
        //we start going cell by cell in the table to fill it
        for (String row : rows) {
            for (String col : columns) {
                //at first we add err in this cell and then we replace when convenient
                table.put(Map.entry(row, col), Map.entry(Arrays.asList("err"), 0));
                if (!grammar.isTerminal(row)) {
                    //we are in first rule, where the row is a nonTerm=> M(A, a)=
                    //we get all its productions for this nonTerm A
                    List<Map.Entry<String, List<String>>> nonTermProductions = grammar.getProductionsForNonTerminal(row);
                    //for each prod we calculate first of RHS, alpha
                    for (Map.Entry<String, List<String>> production : nonTermProductions) {
                        //first of RHS, alpha
                        List<String> firstOfRHSProd = firsts.get(firsts.size() - 1).get(production.getValue().get(0));
                        //if eps is not in the res, then first case and add to the cell
                        if (!firstOfRHSProd.contains("epsilon")) {
                            //for every a term from the first of RHS of A we add (alpha, i) to table
                            for (String first : firstOfRHSProd) {
                                if (table.get(Map.entry(row, first)) != null) {
                                    if (!table.get(Map.entry(row, first)).equals(Map.entry(Arrays.asList("err"), 0))) {
                                        if (!table.get(Map.entry(row, first)).equals(Map.entry(production.getValue(), grammar.getProductions().indexOf(production) + 1)))
                                            throw new MyException("Conflict 1 " + row + " " + first);
                                    }

                                }
                                table.replace(Map.entry(row, first), Map.entry(production.getValue(), grammar.getProductions().indexOf(production) + 1));
                            }
                        } else {
                            //second case where eps is in first => the col is given by follow(A)
                            List<String> followOfNOnTerm = follows.get(follows.size() - 1).get(row);
                            //for every b term from the follow of A we add (alpha, i) to table
                            for (String follow : followOfNOnTerm) {
                                if (follow.equals("epsilon")) {
                                    follow = "$";
                                }
                                if (table.get(Map.entry(row, follow)) != null) {
                                    if (!table.get(Map.entry(row, follow)).equals(Map.entry(Arrays.asList("err"), 0))) {
                                        if (!table.get(Map.entry(row, follow)).equals(Map.entry(production.getValue(), grammar.getProductions().indexOf(production) + 1)))
                                            throw new MyException("Conflict 2");
                                    }
                                }
                                table.replace(Map.entry(row, follow), Map.entry(production.getValue(), grammar.getProductions().indexOf(production) + 1));

                            }
                        }
                    }
                    //second case where $,$ => acc
                } else if (row.equals("$") && col.equals(row)) {
                    table.replace(Map.entry(row, col), Map.entry(Arrays.asList("acc"), 0));
                }//third case where a,a => pop
                else if (row.equals(col)) {
                    table.replace(Map.entry(row, col), Map.entry(Arrays.asList("pop"), 0));
                } else {
                    //last case where err otherwise
                    table.replace(Map.entry(row, col), Map.entry(Arrays.asList("err"), 0));
                }
            }
        }
        //System.out.println(table);
        return table;
    }

    //given a sequence, we verify by parsing if it is accepted by the grammar
    public Queue<String> parse(List<String> sequence) {
        //initial config: alpha = w$, beta= S$, pi=eps
        Stack<String> alpha = new Stack<>();
        Stack<String> beta = new Stack<>();
        Queue<String> pi = new LinkedList<>();
        //init config for alpha
        alpha.push("$");
        for (int i = sequence.size() - 1; i >= 0; i--) {
            alpha.push(sequence.get(i));
        }
        //init config for beta
        beta.push("$");
        beta.push(grammar.getStartingSymbol());
        //we start the alg of LL(1) parsing for the given sequence
        while (true) {
            String choice = "";
            //eps should be treated as $, special case
            if (beta.peek().equals("epsilon")) {
                choice = "$";
            } else choice = beta.peek();

            //first case where pop action is met in the table
            //the cell given by head of beta and head of alpha= pop
            if (table.get(Map.entry(choice, alpha.peek())).equals(Map.entry(Arrays.asList("pop"), 0))) {
                //pop case: pop beta and alpha, pi stays the same
                alpha.pop();
                beta.pop();
            }
            //second case where accept action is met in the table
            //the 2 stacks are only made of $
            //this is where the alg ends, we return the result, seq is accepted
            else if (alpha.peek().equals("$") && alpha.peek().equals(choice)) {
                writeToFileProdString(pi.toString());
                return pi;
            }
            //third case where error is met in the table
            //this means that there is not a way for this seq to be accepted
            //bad case, the alg stops
            else if (table.get(Map.entry(choice, alpha.peek())).equals(Map.entry(Arrays.asList("err"), 0))) {
                return null;
            } else {
                //last case where  the cell= (prod, number of prod)
                // we have to pop beta and push symbols of prod to beta
                String nonTerm = beta.pop();
                Map.Entry<List<String>, Integer> tableCell = table.get(Map.entry(nonTerm, alpha.peek()));
                //and we add the number of prod at the end of the pi queue
                pi.add(tableCell.getValue().toString());
                for (int i = tableCell.getKey().size() - 1; i >= 0; i--) {
                    if (!tableCell.getKey().get(i).equals("epsilon")) {
                        beta.push(tableCell.getKey().get(i));
                    }

                }
            }
            //System.out.println(alpha);
            //System.out.println(beta);
            //System.out.println(pi);

        }

    }


    void writeToFileProdString(String prodString) {
        try {
            FileWriter myWriter = new FileWriter("stringOutput.txt");
            myWriter.write(prodString);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred when writing to file the prod string.");
        }
    }

    List<String> getFirstForNonTerminal(String nonTerminal) {
        Map<String, List<String>> first = firsts.get(firsts.size() - 1);
        return first.get(nonTerminal);
    }

    void first() {
        // preparing the First of 0 instance for every nonTerm = INITIALIZATION
        Map<String, List<String>> values = new HashMap<>();

        // for every nonTerm we go through all the productions where the nonTerm is in LHS
        int index = 0;
        for (String nonTerminal : grammar.getNonTerminals()) {
            List<String> firstOfNonTerminal = new ArrayList<>();
            List<Map.Entry<String, List<String>>> productions = grammar.getProductionsForNonTerminal(nonTerminal);
            //we go through LHS productions
            for (Map.Entry<String, List<String>> production : productions) {
                List<String> elements = production.getValue();
                //if RHS starts with a terminal, add the terminal to the first 0 (nonTerm)
                if (grammar.isTerminal(elements.get(0)))
                    firstOfNonTerminal.add(elements.get(0));
            }
            values.put(nonTerminal, firstOfNonTerminal);
        }
        //initialisation is done => F0 for nonTerminals is done
        firsts.add(values);
        index++;
        boolean found = false; //when found is true, then we have 2 equal First instances => stop the alg

        while (!found) {
            //get an empty map for the current calculated first instance
            Map<String, List<String>> nextFirst = new HashMap<>();

            //alg for every nonTerminal => calculating current instance of first for every nonTerm
            for (String nonTerminal : grammar.getNonTerminals()) {
                //get all productions with this nonTerm in LHS
                List<Map.Entry<String, List<String>>> productions = grammar.getProductionsForNonTerminal(nonTerminal);
                //this will the list where we compute the first of index for this non term
                List<String> concatenatedFinal = new ArrayList<>();
                for (Map.Entry<String, List<String>> production : productions) {
                    //for each production of non Term see if we can add something to the first
                    List<String> nonTerminalsAndTerminals = production.getValue();
                    List<String> concatenatedFirstForNonT = concatenate(nonTerminalsAndTerminals);
                    concatenatedFinal.addAll(concatenatedFirstForNonT);
                }
                //at the end we do the union with previously first calculated for this non term
                concatenatedFinal.addAll(firsts.get(index - 1).get(nonTerminal));
                concatenatedFinal = concatenatedFinal.stream().distinct().collect(Collectors.toList());

                //we put in this instance of follow the result list calculated above
                nextFirst.put(nonTerminal, concatenatedFinal);
            }
            //add to list that holds all first instances aka F0,F1,F2,...
            firsts.add(nextFirst);

            //verification to see if the algorithm is done , if current first=previous first
            if (index >= 1) {
                if (firsts.get(index).equals(firsts.get(index - 1))) {
                    found = true;
                }
            }
            index++;

        }
        //at the end we add to the first for every terminal first(terminal)={terminal}
        for (String terminal : grammar.getTerminals()) {
            firsts.get(index - 1).put(terminal, Collections.singletonList(terminal));
        }
        firsts.get(index - 1).put("epsilon", Collections.singletonList("epsilon"));
        //print the result
        //System.out.println(firsts.get(index-1));


    }

    //returns a list made of first instance for this RHS of production
    private List<String> concatenate(List<String> nonTerminals) {
        //nonTerminals is the RHS of a production made of terminals and nonTerm

        List<String> concatenatedList = new ArrayList<>();
        //it has to start with a nonTerm for F1,F2 or else we don't look over it
        if (grammar.isTerminal(nonTerminals.get(0))) {
            return concatenatedList;
        }
        for (String nonTerminal : nonTerminals) {
            ////we only check for nonTerminals
            if (!grammar.isTerminal(nonTerminal)) {
                //here we have the case where among the nonTerminals in RHS we have a First (nonTerm)=empty => empty list returned
                if (firsts.get(firsts.size() - 1).get(nonTerminal).size() == 0) {
                    concatenatedList.clear();
                    return concatenatedList;
                } else {
                    //here we are in the case where we have to do concatenation of length 1 of all F's of nonTerminals found in the RHS
                    concatenatedList.addAll(firsts.get(firsts.size() - 1).get(nonTerminal));
                    concatenatedList = concatenatedList.stream().distinct().collect(Collectors.toList());
                    break;
                }
            }
        }
        return concatenatedList;
    }


    public void follow() {
        //init for anything besides S by putting empty sets for each
        HashMap<String, List<String>> follow0 = new HashMap<>();
        for (String nonTerminal : grammar.getNonTerminals()) {
            follow0.put(nonTerminal, new ArrayList<>());
        }
        //init for s by putting epsilon
        follow0.get(grammar.getStartingSymbol()).add("epsilon");

        //add to whole list Follow0 to have access to it later on
        follows.add(follow0);

        //alg for every Follow instance
        boolean found = false;
        while (!found) { //we stop when we find 2 identical follow instances
            //get an empty map for the current calculated follow instance
            Map<String, List<String>> nextFollow = new HashMap<>();

            //alg for every nonTerminal => calculating current instance of follow for every nonTerm
            for (String nonTerminal : grammar.getNonTerminals()) {
                //prepare a list where we put the follow(index) for this nonTerm in this particular index
                List<String> followPrimeForNT = new ArrayList<>();
                //at first we add follow of current nonTerm to the list as to not add it so many times
                followPrimeForNT.addAll(follows.get(follows.size() - 1).get(nonTerminal));

                //get all productions that have this nonTerm in the right hand side
                for (Map.Entry<String, List<String>> production : grammar.getRightHandSideProductionsForNonTerminal(nonTerminal)) {
                    String y = ""; //y is the symbol that resides to the right of our nonTerm in this production
                    String A = production.getKey(); //A is the nonTerm from left hand side of this production
                    //here we search for y in the RHS of the production
                    for (int i = 0; i < production.getValue().size(); i++) {
                        if (production.getValue().get(i).equals(nonTerminal)) {
                            //verify if out nonTerm is found on last pos => y is epsilon, else y is the next symbol
                            if (i == production.getValue().size() - 1) {
                                y = "epsilon";
                            } else {
                                y = production.getValue().get(++i);
                            }
                            break;
                        }
                    }

                    //here we verify if epsilon is in FIRST(y) like in the algorithm
                    if (y.equals("epsilon") || isEpsInFirst(firsts.get(firsts.size() - 1).get(y))) {
                        //true case=> we add by union to the list for follow(nonTerm) Follow of index-1 of A, aka LHS of production
                        followPrimeForNT.addAll(follows.get(follows.size() - 1).get(A));
                        //verification for particular case where the symbol after the nonTerm is the last in the prod
                        if (!y.equals("epsilon")) {
                            followPrimeForNT.addAll(firsts.get(firsts.size() - 1).get(y));
                        }


                    } else {
                        //negative case=> we add by union to the list for follow(nonTerm) First(y),where y is the symbol after the nonTerm
                        followPrimeForNT.addAll(firsts.get(firsts.size() - 1).get(y));
                    }
                }

                //after all productions for this nonTerm for Follow of index, we put in the map aka this follow instance
                followPrimeForNT = followPrimeForNT.stream().distinct().collect(Collectors.toList());
                nextFollow.put(nonTerminal, followPrimeForNT);


            }
            //add to list that holds all follow instances aka F0,F1,F2,...
            follows.add(nextFollow);
            //verify if the algorithm is done and Fcurrent=Fprevios
            if (follows.size() > 1) {
                if (follows.get(follows.size() - 1).equals(follows.get(follows.size() - 2))) {
                    found = true;
                }
            }
        }
        //print the result
        //System.out.println(follows.get(follows.size()-1));
    }


    //verifies if epsilon is in a sequence
    private boolean isEpsInFirst(List<String> strings) {
        return strings.contains("epsilon");
    }

}
