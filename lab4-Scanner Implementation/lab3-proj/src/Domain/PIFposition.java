package Domain;

import java.util.AbstractMap;

public class PIFposition {
    private AbstractMap.SimpleEntry<Integer,Integer> positionInST;
    private boolean hasPosInST;

    public PIFposition(AbstractMap.SimpleEntry<Integer, Integer> positionInST) {
        this.positionInST = positionInST;
        hasPosInST=true;
    }

    public PIFposition() {
        hasPosInST=false;
    }

    public boolean isHasPosInST() {
        return hasPosInST;
    }

    public Integer getPositionInPIF(){
        return 0;
    }

    public AbstractMap.SimpleEntry<Integer, Integer> getPositionInST() {
        return positionInST;
    }

    @Override
    public String toString() {
        if(hasPosInST){
            return "("+positionInST.getKey()+","+positionInST.getValue()+")";
        }
        else{
            return "0";
        }
    }
}
