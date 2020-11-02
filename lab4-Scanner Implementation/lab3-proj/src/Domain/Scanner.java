package Domain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {
    private ProgramInternalForm PIF;
    private SymbolTable SymbolTable;
    private String programToAnalyse;
    private List<String> tokensFromFile;
    private boolean error;


    public Scanner(ProgramInternalForm PIF, Domain.SymbolTable symbolTable,String programToAnalyse, String tokensFromFile) {
        this.PIF = PIF;
        this.SymbolTable = symbolTable;
        this.programToAnalyse = programToAnalyse;
        this.tokensFromFile = this.parseTokensFromFileTokenIn(tokensFromFile);
        error=false;
    }


    //a string of letters aka an identifier
    private boolean isIdentifier(String token){
        Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }



    //a string of letters aka an integer
    private boolean isInteger(String token){
        Pattern pattern = Pattern.compile("0|^(\\+|\\-)?[1-9]+[0-9]*$|MAX|MIN");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }

    //a character
    private boolean isChar(String token){
        Pattern pattern = Pattern.compile("^“[a-zA-Z0-9]”$");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }

    //a boolean
    private boolean isBoolean(String token){
        Pattern pattern = Pattern.compile("^(True|False)$");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }

    //a string
    private boolean isString(String token){
        Pattern pattern = Pattern.compile("^“([a-zA-Z0-9 .-:]+)+”$");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }

    //is constant
    private boolean isConstant(String token){
        return isInteger(token) || isBoolean(token) || isString(token) || isChar(token);
    }

    private boolean isReservedWord(String token){
        Pattern pattern = Pattern.compile("^(list|int|bool|Initialise|Read|Do|While|If|char|string|Print|Else|For|Return)$");
        Matcher m = pattern.matcher(token);
        return m.matches()&&tokensFromFile.contains(token);
    }

    private boolean isOperator(String token){
        Pattern pattern = Pattern.compile("^(\\+|\\-|\\*|\\/|\\<|\\<\\=|\\=\\=|\\>\\=|sqrt|\\&\\&|not|\\>|=)$");
        Matcher m = pattern.matcher(token);
        return m.matches()&&tokensFromFile.contains(token);
    }
    private boolean isSeparator(String token){
        Pattern pattern = Pattern.compile("^(\\(|\\)|\\{|\\}|\\;|\\[|\\])$");
        Matcher m = pattern.matcher(token);
        return m.matches()&&tokensFromFile.contains(token);
    }


    private List<String> parseTokensFromFileTokenIn(String tokens){
        //parse tokens from token.in to use in the regex classifier functions written above
        String[] arrOfStr = tokens.split("\\r?\\n", 0);
        return new ArrayList<>(Arrays.asList(arrOfStr));
    }

    private void writeToPIFfile(){
        File PIFfile = new File("PIF.out");
        PIFfile.delete();
        File PIFout = new File("PIF.out");
        try {
            FileWriter myWriter = new FileWriter("PIF.out");
            myWriter.write(PIF.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the PIF.out file.");
            e.printStackTrace();
        }
    }

    private void writeToSTfile(){
        File STfile = new File("ST.out");
        STfile.delete();
        File STout = new File("ST.out");
        try {
            FileWriter myWriter = new FileWriter("ST.out");
            myWriter.write(SymbolTable.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the ST.out file.");
            e.printStackTrace();
        }
    }



    private boolean classifyToken(String token){
        //algorithm of classification for the given token:
        if(isReservedWord(token)||isOperator(token)||isSeparator(token)){
            this.PIF.addInPIFInteger(token);
            return true;
        }
        else{
            if(isIdentifier(token)||isConstant(token)){
                AbstractMap.SimpleEntry<Integer,Integer> positionInST=this.SymbolTable.position(token);
                PIFposition pair=new PIFposition(positionInST);
                if(isConstant(token))
                    this.PIF.addInPIFPair("0",pair);
                else
                    this.PIF.addInPIFPair("1",pair);
                return true;
            }
            else return false;
        }
    }




    private List<String> separateTokens(String line){
        //we get a line and we split it to get the list of tokens to return it afterwards
        String[] tokens = line.split("(\\s)|(?<=;)|(?=;)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\\{)|(?=\\{)" +
                "|(?<=\\})|(?=\\})|(?<=\\+)|(?=\\+)|(?<=-)|(?=-)|(?<=\\*)|(?=\\*)|(?<=/)|(?=/)|(?<=<)|(?=<)|(?<=<=)|(?=<=)" +
                "|(?<=>)|(?=>)|(?<=>=)|(?=>=)|(?<=&&)|(?=&&)|(?<==)|(?==)|(?<=\\[)|(?=\\[)|(?<=\\])|(?=\\])");
        List<String> array=Arrays.asList(tokens);
        List<String> modifiedArray=new ArrayList<>();
        int i=0;
        //in case of blank space to be removed or  “” or <=,==,>=
        while(i<array.size()){
            if(array.get(i).equals(" ")||array.get(i).isEmpty()) {
                i++;
            }
            else{
                if(array.get(i).contains("“")){
                    StringBuilder newString= new StringBuilder();
                    newString.append(array.get(i)).append(" ");
                    i++;
                    while(!array.get(i).contains("”")&&i<array.size()){
                        newString.append(array.get(i)).append(" ");
                        i++;
                    }
                    newString.append(array.get(i));
                    modifiedArray.add(newString.toString());
                }
                else{
                    if(array.get(i).equals("<")||array.get(i).equals("=")||array.get(i).equals(">")){
                        if(array.get(i+1).equals("=")){
                            StringBuilder newStr=new StringBuilder();
                            newStr.append(array.get(i)).append(array.get(i+1));
                            modifiedArray.add(newStr.toString());
                            i+=1;
                        }
                        else{
                            modifiedArray.add(array.get(i));
                        }
                    }
                    else{
                        modifiedArray.add(array.get(i));
                    }
                }
                i++;
            }
        }
        return modifiedArray;
    }


    public void scan(){
        //first split line by line
        String[] aEach = programToAnalyse.split("\\r?\\n");
        ArrayList<String> lines=new ArrayList<>();
        Collections.addAll(lines, aEach);
        for(int i=0;i<lines.size();i++){
            //then for each line split it to get the tokens
            List<String> tokensFromLine=separateTokens(lines.get(i));
            for(String token:tokensFromLine){
                //after getting the tokens we classify each and every one of them
                if(!classifyToken(token)){
                    int k=i+1;
                    System.out.println("lexical error at line "+k+" token: "+token);
                    error=true;
                }
            }
        }
        if(!error){
            System.out.println("lexically correct");
        }

        //write output to files
        writeToPIFfile();
        writeToSTfile();


    }


}
