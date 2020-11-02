package Domain;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {
    private List<AbstractMap.SimpleEntry<String,PIFposition>> pairList=new ArrayList<>();

    public ProgramInternalForm() {
    }




    public void addInPIFInteger(String token){
        this.pairList.add(new AbstractMap.SimpleEntry<>(token, new PIFposition()));
    }

    public void addInPIFPair(String token, PIFposition pair){
        this.pairList.add(new AbstractMap.SimpleEntry<>(token,pair));
    }

    @Override
    public String toString() {
        StringBuilder result= new StringBuilder("Program Internal Form:\n Token \t\t\t Position in ST\n\n");
        for (AbstractMap.SimpleEntry<String, PIFposition> pair : this.pairList) {
            result.append(pair.getKey()).append("\t\t\t\t\t\t").append(pair.getValue()).append("\n");
        }
        return result.toString();
    }
}
