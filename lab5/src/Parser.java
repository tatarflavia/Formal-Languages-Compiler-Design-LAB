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


    void first() {
        //TODO

        // adding FIRST of terminal
        Map<String,List<String>> values = new HashMap<>();

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
        //done with F0
        firsts.add(values);
        index++;
        boolean found=false;

        while(!found){
            Map<String,List<String>> nextFirst=new HashMap<>();
            for (String nonTerminal:grammar.getNonTerminals()){
                //get non terminal to start calculation for F index (nonTerm)
                List<Map.Entry<String, List<String>>> productions = grammar.getProductionsForNonTerminal(nonTerminal);
                //this will the list where we compute the first for this non term
                List<String> concatenatedFinal=new ArrayList<>();
                for (Map.Entry<String, List<String>> production: productions) {
                    //for each production of non Term see if we can add something to the first
                    List<String> nonTerminals=production.getValue();
                    List<String> concatenatedFirstForNonT=concatenate(nonTerminals);
                    concatenatedFinal.addAll(concatenatedFirstForNonT);
                }
                //at the end we do the union with previosly first calculated for this non term
                concatenatedFinal.addAll(firsts.get(index-1).get(nonTerminal));
                concatenatedFinal=concatenatedFinal.stream().distinct().collect(Collectors.toList());
                nextFirst.put(nonTerminal,concatenatedFinal);
            }
            firsts.add(nextFirst);
            //System.out.println(firsts.get(firsts.size()-1));

            if(index>=1){
                if(firsts.get(index).equals(firsts.get(index-1))){
                    found=true;
                }
            }
            index++;

        }
        for (String terminal: grammar.getTerminals()) {
            firsts.get(index-1).put(terminal, Collections.singletonList(terminal));
        }
        System.out.println(firsts.get(index-1));


        //continue with the algorithm

    }

    private List<String> concatenate(List<String> nonTerminals) {
        List<String> concatenatedList=new ArrayList<>();
        if(grammar.isTerminal(nonTerminals.get(0))){
            return concatenatedList;
        }
        for(String nonTerminal:nonTerminals){
            if(!grammar.isTerminal(nonTerminal)){
                if(firsts.get(firsts.size()-1).get(nonTerminal).size()==0){
                    concatenatedList.clear();
                    return concatenatedList;
                }
                else{
                    concatenatedList.addAll(firsts.get(firsts.size()-1).get(nonTerminal));
                    concatenatedList=concatenatedList.stream().distinct().collect(Collectors.toList());
                    break;
                }
            }
        }
        return concatenatedList;
    }


    void follow() {
        //init for anything besides S
        HashMap<String, List<String>> follow0=new HashMap<>();
        for (String nonTerminal: grammar.getNonTerminals()){
               follow0.put(nonTerminal,new ArrayList<>()) ;
        }
        //init for s
        follow0.get(grammar.getStartingSymbol()).add("epsilon");

        //add to whole list
        follows.add(follow0);

        //alg for every non term
        boolean found=false;
        while(!found){
            Map<String,List<String>> nextFollow=new HashMap<>();
            for (String nonTerminal:grammar.getNonTerminals()){
                //get rhs productions
                List<String> followPrimeForNT=new ArrayList<>();
                for(Map.Entry<String, List<String>> production: grammar.getRightHandSideProductionsForNonTerminal(nonTerminal)){
                    String y="";
                    String A=production.getKey();
                    for(int i=0;i<production.getValue().size();i++){
                        if(production.getValue().get(i).equals(nonTerminal)){
                            //verify last pos
                            if(i==production.getValue().size()-1){
                                y="epsilon";
                                break;
                            }
                            else{
                                y=production.getValue().get(++i);
                                break;
                            }
                        }
                    }
                    if(y.equals("epsilon")|| isEpsInFirst(firsts.get(firsts.size()-1).get(y))){
                        //first case
                        followPrimeForNT.addAll(follows.get(follows.size()-1).get(nonTerminal));
                        followPrimeForNT.addAll(follows.get(follows.size()-1).get(A));
                        followPrimeForNT=followPrimeForNT.stream().distinct().collect(Collectors.toList());
                    }
                    else{
                        //next case
                        followPrimeForNT.addAll(follows.get(follows.size()-1).get(nonTerminal));
                        followPrimeForNT.addAll(firsts.get(firsts.size()-1).get(y));
                        followPrimeForNT=followPrimeForNT.stream().distinct().collect(Collectors.toList());
                    }
                }
                nextFollow.put(nonTerminal,followPrimeForNT);
            }
            //add to follows
            follows.add(nextFollow);
            //verify
            if(follows.size()>1){
                if(follows.get(follows.size()-1).equals(follows.get(follows.size()-2))){
                    found=true;
                }
            }
        }
        System.out.println(follows.get(follows.size()-1));
    }



    boolean isEpsInFirst(List<String> strings){
        return strings.contains("epsilon");
    }

}
