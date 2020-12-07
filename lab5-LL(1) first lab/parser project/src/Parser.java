import java.util.*;
import java.util.stream.Collectors;

public class Parser {

    private Grammar grammar;
    private List<Map<String, List<String>>> firsts;
    private List<Map<String, List<String>>> follows;

    public Parser() {
        grammar = new Grammar();
        firsts = new ArrayList<>();
        follows = new ArrayList<>();
    }

    void constructTable() {
        Map<Map.Entry<String, String>, Map.Entry<List<String>, Integer>> table = new HashMap<>();
        List<String> rows = grammar.getNonTerminals();
        rows.addAll(grammar.getTerminals());
        rows.add("$");
        List<String> columns = grammar.getTerminals();
        columns.add("$");
        for (String row : rows) {
            for (String col : columns) {
                if (grammar.isTerminal(row)) {
                    Map.Entry<String, List<String>> production = grammar.getProductionsForNonTerminal(row).get(0);
                    List<String> firsts = getFirstForNonTerminal(production.getValue().get(0));
                    if (!firsts.contains("epsilon")) {
                        // to be done
                    }
                } else if (row.equals(col)) {
                    table.put(Map.entry(row, col), Map.entry(Arrays.asList("pop"), 0));
                } else if (row.equals("$") && row.equals("$")) {
                    table.put(Map.entry(row, col), Map.entry(Arrays.asList("acc"), 0));
                } else {
                    table.put(Map.entry(row, col), Map.entry(Arrays.asList("err"), 0));
                }
            }
        }
    }

    List<String> getFirstForNonTerminal(String nonTerminal) {
        Map<String, List<String>> first = firsts.get(firsts.size() - 1);
        return first.get(nonTerminal);
    }

    void first() {
        // preparing the First of 0 instance for every nonTerm = INITIALIZATION
        Map<String,List<String>> values = new HashMap<>();

        // for every nonTerm we go through all the productions where the nonTerm is in LHS
        int index = 0;
        for (String nonTerminal: grammar.getNonTerminals()) {
            List<String> firstOfNonTerminal = new ArrayList<>();
            List<Map.Entry<String, List<String>>> productions = grammar.getProductionsForNonTerminal(nonTerminal);
            //we go through LHS productions
            for (Map.Entry<String, List<String>> production: productions) {
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
        boolean found=false; //when found is true, then we have 2 equal First instances => stop the alg

        while(!found){
            //get an empty map for the current calculated first instance
            Map<String,List<String>> nextFirst=new HashMap<>();

            //alg for every nonTerminal => calculating current instance of first for every nonTerm
            for (String nonTerminal:grammar.getNonTerminals()){
                //get all productions with this nonTerm in LHS
                List<Map.Entry<String, List<String>>> productions = grammar.getProductionsForNonTerminal(nonTerminal);
                //this will the list where we compute the first of index for this non term
                List<String> concatenatedFinal=new ArrayList<>();
                for (Map.Entry<String, List<String>> production: productions) {
                    //for each production of non Term see if we can add something to the first
                    List<String> nonTerminalsAndTerminals=production.getValue();
                    List<String> concatenatedFirstForNonT=concatenate(nonTerminalsAndTerminals);
                    concatenatedFinal.addAll(concatenatedFirstForNonT);
                }
                //at the end we do the union with previously first calculated for this non term
                concatenatedFinal.addAll(firsts.get(index-1).get(nonTerminal));
                concatenatedFinal=concatenatedFinal.stream().distinct().collect(Collectors.toList());

                //we put in this instance of follow the result list calculated above
                nextFirst.put(nonTerminal,concatenatedFinal);
            }
            //add to list that holds all first instances aka F0,F1,F2,...
            firsts.add(nextFirst);

            //verification to see if the algorithm is done , if current first=previous first
            if(index>=1){
                if(firsts.get(index).equals(firsts.get(index-1))){
                    found=true;
                }
            }
            index++;

        }
        //at the end we add to the first for every terminal first(terminal)={terminal}
        for (String terminal: grammar.getTerminals()) {
            firsts.get(index-1).put(terminal, Collections.singletonList(terminal));
        }
        //print the result
        System.out.println(firsts.get(index-1));


    }

    //returns a list made of first instance for this RHS of production
    private List<String> concatenate(List<String> nonTerminals) {
        //nonTerminals is the RHS of a production made of terminals and nonTerm

        List<String> concatenatedList=new ArrayList<>();
        //it has to start with a nonTerm for F1,F2 or else we don't look over it
        if(grammar.isTerminal(nonTerminals.get(0))){
            return concatenatedList;
        }
        for(String nonTerminal:nonTerminals){
            ////we only check for nonTerminals
            if(!grammar.isTerminal(nonTerminal)){
                //here we have the case where among the nonTerminals in RHS we have a First (nonTerm)=empty => empty list returned
                if(firsts.get(firsts.size()-1).get(nonTerminal).size()==0){
                    concatenatedList.clear();
                    return concatenatedList;
                }
                else{
                    //here we are in the case where we have to do concatenation of length 1 of all F's of nonTerminals found in the RHS
                    concatenatedList.addAll(firsts.get(firsts.size()-1).get(nonTerminal));
                    concatenatedList=concatenatedList.stream().distinct().collect(Collectors.toList());
                    break;
                }
            }
        }
        return concatenatedList;
    }


    public void follow() {
        //init for anything besides S by putting empty sets for each
        HashMap<String, List<String>> follow0=new HashMap<>();
        for (String nonTerminal: grammar.getNonTerminals()){
               follow0.put(nonTerminal,new ArrayList<>()) ;
        }
        //init for s by putting epsilon
        follow0.get(grammar.getStartingSymbol()).add("epsilon");

        //add to whole list Follow0 to have access to it later on
        follows.add(follow0);

        //alg for every Follow instance
        boolean found=false;
        while(!found){ //we stop when we find 2 identical follow instances
            //get an empty map for the current calculated follow instance
            Map<String,List<String>> nextFollow=new HashMap<>();

            //alg for every nonTerminal => calculating current instance of follow for every nonTerm
            for (String nonTerminal:grammar.getNonTerminals()){
                //prepare a list where we put the follow(index) for this nonTerm in this particular index
                List<String> followPrimeForNT=new ArrayList<>();
                //at first we add follow of current nonTerm to the list as to not add it so many times
                followPrimeForNT.addAll(follows.get(follows.size()-1).get(nonTerminal));

                //get all productions that have this nonTerm in the right hand side
                for(Map.Entry<String, List<String>> production: grammar.getRightHandSideProductionsForNonTerminal(nonTerminal)){
                    String y=""; //y is the symbol that resides to the right of our nonTerm in this production
                    String A=production.getKey(); //A is the nonTerm from left hand side of this production
                    //here we search for y in the RHS of the production
                    for(int i=0;i<production.getValue().size();i++){
                        if(production.getValue().get(i).equals(nonTerminal)){
                            //verify if out nonTerm is found on last pos => y is epsilon, else y is the next symbol
                            if(i==production.getValue().size()-1){
                                y="epsilon";
                            }
                            else{
                                y=production.getValue().get(++i);
                            }
                            break;
                        }
                    }

                    //here we verify if epsilon is in FIRST(y) like in the algorithm
                    if(y.equals("epsilon")|| isEpsInFirst(firsts.get(firsts.size()-1).get(y))){
                        //true case=> we add by union to the list for follow(nonTerm) Follow of index-1 of A, aka LHS of production
                        followPrimeForNT.addAll(follows.get(follows.size()-1).get(A));
                        //verification for particular case where the symbol after the nonTerm is the last in the prod
                        if(!y.equals("epsilon")){
                            followPrimeForNT.addAll(firsts.get(firsts.size()-1).get(y));
                        }


                    }
                    else{
                        //negative case=> we add by union to the list for follow(nonTerm) First(y),where y is the symbol after the nonTerm
                        followPrimeForNT.addAll(firsts.get(firsts.size()-1).get(y));
                    }
                }

                //after all productions for this nonTerm for Follow of index, we put in the map aka this follow instance
                followPrimeForNT=followPrimeForNT.stream().distinct().collect(Collectors.toList());
                nextFollow.put(nonTerminal,followPrimeForNT);


            }
            //add to list that holds all follow instances aka F0,F1,F2,...
            follows.add(nextFollow);
            //verify if the algorithm is done and Fcurrent=Fprevios
            if(follows.size()>1){
                if(follows.get(follows.size()-1).equals(follows.get(follows.size()-2))){
                    found=true;
                }
            }
        }
        //print the result
        System.out.println(follows.get(follows.size()-1));
    }


    //verifies if epsilon is in a sequence
    private boolean isEpsInFirst(List<String> strings){
        return strings.contains("epsilon");
    }

}
