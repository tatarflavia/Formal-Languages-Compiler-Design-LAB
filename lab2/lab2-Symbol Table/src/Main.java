import java.util.Map;

public class Main {



    public static void main(String[] args)
    {
        SymbolTable symbolTable=new SymbolTable();

        System.out.println(symbolTable.position("a"));
        System.out.println(symbolTable.position("b"));
        System.out.println(symbolTable.position("b"));
    }


}
