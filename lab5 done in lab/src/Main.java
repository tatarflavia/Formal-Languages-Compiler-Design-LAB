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
    }


    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        Parser parser=new Parser();
        printMenu();
        Scanner scan = new Scanner(System.in);
        int num=9;
        while(num!=0){
            System.out.print("Enter any number: ");
            num = scan.nextInt();
            if(num==1){
                System.out.println("Set of nonterminals:\n"+grammar.getNonTerminals());
            }
            else{
                if(num==2){
                    System.out.println("Set of terminals:\n"+grammar.getTerminals());
                }
                else{
                    if(num==3){
                        System.out.println("Starting symbol:\n"+grammar.getStartingSymbol());
                    }
                    else{
                        if(num==4){
                            System.out.println("Productions:\n"+grammar.getProductions());
                        }
                        else{
                            if(num==5){
                                System.out.println("Enter the nonterminal:");
                                String nonTerm=scan.next();
                                System.out.println("Productions for given nonterminal:\n"+grammar.getProductionsForNonTerminal(nonTerm));
                            }
                            else{
                                if(num==6){
                                    System.out.println("First:");
                                    parser.first();
                                }
                                else{
                                    if(num==7){
                                        System.out.println("Follow:");
                                        parser.follow();
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }
    }
}
