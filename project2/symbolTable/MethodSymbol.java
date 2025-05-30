package project2.symbolTable;
import java.util.Map;
import java.util.LinkedHashMap; //as it was said on piazza @71 

public class MethodSymbol {
    public final String name;
    public final String returnType;

    public final Map<String, VariableSymbol> parameters = new LinkedHashMap<>(); //args

    public final Map<String, VariableSymbol> locals = new LinkedHashMap<>(); //local vars

    public MethodSymbol(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public boolean addParameter(String name, String type) {
        if (parameters.containsKey(name)) 
            return false; //no duplicate args
        parameters.put(name, new VariableSymbol(name, type));
        return true;
    }

    public boolean addLocal(String name, String type) {
        if (locals.containsKey(name) || parameters.containsKey(name)) 
            return false; //no conflicting names with args/locals, neither redeclaration
        locals.put(name, new VariableSymbol(name, type));
        return true;
    }
}
