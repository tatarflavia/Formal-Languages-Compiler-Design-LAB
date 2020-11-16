import java.util.Scanner;

public class Main {

    private static void printMenu(){
        System.out.println("Chose:");
        System.out.println("1.states");
        System.out.println("2.alphabet");
        System.out.println("3.final states");
        System.out.println("4.initial state");
        System.out.println("5.transition function");
        System.out.println("6.verify if a given sequence is accepted by the DFA");
    }


    public static void main(String[] args) {
        FiniteAutomata FA=new FiniteAutomata("FA3.in");
        System.out.println("The given FA is a DFA: "+ FA.isDFA());
        printMenu();
        Scanner scan = new Scanner(System.in);
        int num=9;
        while(num!=0){
            System.out.print("Enter any number: ");
            num = scan.nextInt();
            if(num==1){
                System.out.println("Set of states:\n"+FA.getSetOfStates());
            }
            else{
                if(num==2){
                    System.out.println("Alphabet:\n"+FA.getAlphabet());
                }
                else{
                    if(num==3){
                        System.out.println("Set of final states:\n"+FA.getSetOfFinalStates());
                    }
                    else{
                        if(num==4){
                            System.out.println("Initial state:\n"+FA.getInitialState());
                        }
                        else{
                            if(num==5){
                                System.out.println("Transitions:\n"+ FA.getTransitionFunction());
                            }
                            else{
                                if(num==6){
                                    if(FA.isDFA()){
                                        System.out.println("Enter the sequence:");
                                        String w=scan.next();
                                        System.out.println("Result:"+FA.verifyIfSequenceIsAcceptedByDFA(w));
                                    }
                                    else{System.out.println("The given FA is not a DFA!");}
                                }

                            }
                        }
                    }

                }
            }
        }
    }
}
