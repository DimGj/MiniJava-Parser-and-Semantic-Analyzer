package project2.symbolTable;
import java.util.Map;
import java.util.LinkedHashMap; //as it was said on piazza @71 

public class ClassSymbol {
    public final String name;    //class bane
    public final String parent;  //null if no inheritance,parent class name otherwise

    public final Map<String, VariableSymbol> fields = new LinkedHashMap<>(); //vars

    public final Map<String, MethodSymbol> methods = new LinkedHashMap<>(); //methods

    public ClassSymbol(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    public boolean addField(String name, String type) { //setter for fields
        if (fields.containsKey(name)) 
            return false; //no duplicate vars
        fields.put(name, new VariableSymbol(name, type));
        return true;
    }

    public boolean addMethod(String name, String returnType) { //setter for methods
        if (methods.containsKey(name)) 
            return false; //no duplicate methods
        methods.put(name, new MethodSymbol(name, returnType));
        return true;
    }

    public VariableSymbol getField(String name) { //getter for fields
        return fields.get(name);
    }

    public MethodSymbol getMethod(String name) { //getter for methods
        return methods.get(name);
    }
}
