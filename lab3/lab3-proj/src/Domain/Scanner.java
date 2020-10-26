package Domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scanner {
    private ProgramInternalForm PIF;
    private SymbolTable SymbolTable;
    private List<String> Tokens=new ArrayList<>();

    public Scanner(ProgramInternalForm PIF, Domain.SymbolTable symbolTable) {
        this.PIF = PIF;
        SymbolTable = symbolTable;
    }


    //a string of letters aka an identifier
    private boolean isIdentifier(String token){
        Pattern pattern = Pattern.compile("^[a-zA-Z]+$");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }



    //a string of letters aka an integer
    private boolean isInteger(String token){
        Pattern pattern = Pattern.compile("0|^(\\+|\\-)?[1-9][0-9]+$");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }

    //a character
    private boolean isChar(String token){
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
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
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9]+)+$");
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
        return m.matches();
    }

    private boolean isOperator(String token){
        Pattern pattern = Pattern.compile("^(\\+|\\-|\\*|\\/|\\<|\\<\\=|\\=\\=|\\>\\=|sqrt|\\&\\&|not)$");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }
    private boolean isSeparator(String token){
        Pattern pattern = Pattern.compile("^(\\(|\\)|\\{|\\}|\\;)$");
        Matcher m = pattern.matcher(token);
        return m.matches();
    }


    private void readTokensFromFile(){
        List<String> result = null;
        try (Stream<String> lines = Files.lines(Paths.get("token.in"))) {
            result = lines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Tokens.addAll(result);
    }



    private boolean clasiffyToken(String token){
        if(isReservedWord(token)||isOperator(token)||isSeparator(token)){
            this.PIF.addInPIF(token);
            return true;
        }
        else{
            if(isIdentifier(token)||isConstant(token)){
                AbstractMap.SimpleEntry<Integer,Integer> positionInST=this.SymbolTable.position(token);
                this.PIF.addInPIF(token);
                return true;
            }
            else return false;
        }
    }

    private void detectTokens(){
        //read from programm file and detect tokens
        String detectedToken=readFromFile;
        if(!(clasiffyToken(detectedToken))){
            System.out.println("Error at line"+line+" from "+detectedToken);
        }
    }
}
