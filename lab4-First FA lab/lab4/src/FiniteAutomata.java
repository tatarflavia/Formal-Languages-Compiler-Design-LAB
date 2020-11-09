
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FiniteAutomata {
    private List<String> setOfStates=new ArrayList<>();
    private String filename;
    private String initialState;
    private List<String> setOfFinalStates=new ArrayList<>();
    private List<String> alphabet=new ArrayList<>();
    private HashMap<AbstractMap.SimpleEntry<String,String>,List<String>> transitionFunction=new HashMap();

    public FiniteAutomata(String filename) {
        this.filename = filename;
        readFromFile();
    }


    public String getSetOfStates() {
        return setOfStates.toString();
    }

    public String getInitialState() {
        return initialState;
    }

    public String getSetOfFinalStates() {
        return setOfFinalStates.toString();
    }

    public String getAlphabet() {
        return alphabet.toString();
    }

    public String getTransitionFunction() {
        StringBuilder str=new StringBuilder();
        for (Map.Entry<AbstractMap.SimpleEntry<String,String>,List<String>> entry : transitionFunction.entrySet()) {
            str.append("d(").append(entry.getKey().getKey()).append(",").append(entry.getKey().getValue()).append(")={");
            for (String fromList:entry.getValue()){
                str.append(fromList).append(",");
            }
            str.append("}\n");
        }
        return str.toString();
    }

    private void setSetOfStates(String line){
        String[] tokens = line.split(",");
        List<String> array= Arrays.asList(tokens);
        setOfStates=  array;
    }

    private void setAlphabet(String line){
        String[] tokens = line.split(",");
        List<String> array= Arrays.asList(tokens);
        alphabet=  array;
    }

    private void setSetOfFinalStates(String line){
        String[] tokens = line.split(",");
        List<String> array= Arrays.asList(tokens);
        setOfFinalStates=  array;
    }

    private void addEntryToTransitionFct(String line){
        String[] tokens=line.split("=");
        String[] pairFromEntry=tokens[0].split(",");
        String[] listFromEntry=tokens[1].split(",");
        List<String> results=new ArrayList<>();
        Collections.addAll(results, listFromEntry);
        transitionFunction.put(new AbstractMap.SimpleEntry<>(pairFromEntry[0],pairFromEntry[1]),results);
    }




    private void readFromFile(){
        try {
            String program = readFileAsString(filename);
            String[] aEach = program.split("\\r?\\n");
            List<String> lines=new ArrayList<>();
            Collections.addAll(lines, aEach);
            for(int i=0;i<lines.size();i++){
                if(i==0){
                    setSetOfStates(lines.get(i));
                }
                else{
                    if(i==1){
                        setAlphabet(lines.get(i));
                        }
                    else{
                        if(i==2){
                            setSetOfFinalStates(lines.get(i));
                        }
                        else{
                            if(i==3){
                                initialState=lines.get(i).toString();
                                //System.out.println(initialState);
                            }
                            else{
                                addEntryToTransitionFct(lines.get(i));
                            }
                        }
                    }
                    }
                }

            }

        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String readFileAsString(String fileName)throws Exception
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }
}
