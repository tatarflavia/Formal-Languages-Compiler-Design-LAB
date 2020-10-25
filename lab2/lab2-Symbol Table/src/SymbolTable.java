import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SymbolTable {

    private List<List<String>> hashTable;

    // Current capacity of hashes list
    private int numBuckets;



    // Constructor (Initializes capacity, size and
    // empty chains.
    public SymbolTable()
    {
        hashTable = new ArrayList<>();
        numBuckets = 256;

        // Create empty chains
        for (int i = 0; i < numBuckets; i++)
            hashTable.add(null);
    }


    //returns position in hash list aka hash value from hash function
    private int getHashValue(String tokenName){
        int asciiSum = 0;
        for (int i = 0; i < tokenName.length(); i++){
            asciiSum += (int)tokenName.charAt(i);
        }
        return (int)(asciiSum%256);
    }

    //returns position in symbol table
    public AbstractMap.SimpleEntry<Integer,Integer> position(String tokenName){
        int hashValue=this.getHashValue(tokenName);

        if(this.hashTable.get(hashValue)==null){
            //this means we are adding in the head of the linked list of this hash value, cause there is no member yet here added
            ArrayList<String> linkedList=new ArrayList<>();
            linkedList.add(tokenName);
            this.hashTable.set(hashValue, linkedList);
            return new AbstractMap.SimpleEntry<Integer,Integer>(hashValue, 0);
        }
        else{
            //this means that there is already a hash here, so we put it in the link
            List<String> linkedList=this.hashTable.get(hashValue);
            linkedList.add(tokenName);
            this.hashTable.set(hashValue,linkedList);
            return new AbstractMap.SimpleEntry<Integer,Integer>(hashValue, linkedList.size()-1);
        }
    }



}


