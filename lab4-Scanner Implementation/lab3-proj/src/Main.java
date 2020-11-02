import Domain.ProgramInternalForm;
import Domain.Scanner;
import Domain.SymbolTable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static String readFileAsString(String fileName)throws Exception
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }
    public static void main(String[] args)
    {
        String program = null;
        try {
            program = readFileAsString("p1.in");
            String tokens =  readFileAsString("token.in");
            SymbolTable symbolTable=new SymbolTable();
            ProgramInternalForm PIF=new ProgramInternalForm();
            Scanner scanner=new Scanner(PIF,symbolTable,program,tokens);
            scanner.scan();
        } catch (Exception e) {
            e.printStackTrace();
        }





    }
}
