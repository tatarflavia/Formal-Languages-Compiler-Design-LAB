import java.util.Arrays;
import java.util.Queue;
import java.util.Scanner;

public class Main {

    private static void printMenu(){
        System.out.println("Chose:");
        System.out.println("1.set of nonterminals");
        System.out.println("2.set of terminals");
        System.out.println("3.starting symbol");
        System.out.println("4.productions");
        System.out.println("5.get the productions for a given nonterminal");
        System.out.println("6.first");
        System.out.println("7.follow");
        System.out.println("8.table");
        System.out.println("9.production string for given sequence");
    }


    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        Parser parser=new Parser();
        printMenu();
        Scanner scan = new Scanner(System.in);
        int num=9;
        do {
            System.out.print("Enter any number: ");
            num = scan.nextInt();
            switch (num) {
                case 1:
                    System.out.println("Set of nonterminals:\n" + grammar.getNonTerminals());
                    break;
                case 2:
                    System.out.println("Set of terminals:\n" + grammar.getTerminals());
                    break;
                case 3:
                    System.out.println("Starting symbol:\n" + grammar.getStartingSymbol());
                    break;
                case 4:
                    System.out.println("Productions:\n" + grammar.getProductions());
                    break;
                case 5:
                    System.out.println("Enter the nonterminal:");
                    String nonTerm=scan.next();
                    System.out.println("Productions for given nonterminal:\n"+grammar.getProductionsForNonTerminal(nonTerm));
                    break;
                case 6:
                    System.out.println(parser.getFirst());
                    break;
                case 7:
                    System.out.println(parser.getFollow());
                    break;
                case 8:
                    System.out.println(parser.getTable());
                    break;
                case 9:
                    Queue<String> par=parser.parse(Arrays.asList("a","*","(","a","+","a",")"));
                    String rez="";
                    for(String str:par){
                        rez+=str;
                    }
                    System.out.println("result should be: 1485714862486363");
                    System.out.println("the calculated result is equal with the one above:"+rez.equals("1485714862486363"));
                    System.out.println("The calc one is: "+par);
            }
        }while(num!=0);




//        while(num!=0){
//            System.out.print("Enter any number: ");
//            num = scan.nextInt();
//            if(num==1){
//                System.out.println("Set of nonterminals:\n"+grammar.getNonTerminals());
//            }
//            else{
//                if(num==2){
//                    System.out.println("Set of terminals:\n"+grammar.getTerminals());
//                }
//                else{
//                    if(num==3){
//                        System.out.println("Starting symbol:\n"+grammar.getStartingSymbol());
//                    }
//                    else{
//                        if(num==4){
//                            System.out.println("Productions:\n"+grammar.getProductions());
//                        }
//                        else{
//                            if(num==5){
//                                System.out.println("Enter the nonterminal:");
//                                String nonTerm=scan.next();
//                                System.out.println("Productions for given nonterminal:\n"+grammar.getProductionsForNonTerminal(nonTerm));
//                            }
//                            else{
//                                if(num==6){
//                                    System.out.println("First:");
//                                    parser.first();
//                                    firstDone=true;
//                                }
//                                else{
//                                    if(num==7){
//                                        if(firstDone){
//                                            System.out.println("Follow:");
//                                            parser.follow();
//                                            parser.constructTable();
//                                        }
//                                        else{
//                                            System.out.println("Please do first before!");
//                                        }
//
//                                    }
//                                    else{
//                                        if(num==8){
//                                            Queue<String> par=parser.parse(Arrays.asList("a","*","(","a","+","a",")"));
//                                            String rez="";
//                                            for(String str:par){
//                                                rez+=str;
//                                            }
//                                            System.out.println(rez.equals("1485714862486363"));
//                                            //System.out.println(parser.parse(Arrays.asList("a","*","(","a","+","a",")")));
//                                        }
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//
//                }
//            }
//        }
    }
}
