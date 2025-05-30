package project2.symbolTable;
import java.util.Map;
import java.util.LinkedHashMap; //as it was said on piazza @71 

public class SymbolTable {

    //map of class names to class symbols objs
    private final Map<String, ClassSymbol> classes = new LinkedHashMap<>();

    public boolean addClass(String name, String parent) {
        if (classes.containsKey(name)) {
            return false; //error, class name already exists
        }
        classes.put(name, new ClassSymbol(name, parent)); //parent can be null if no inheritance
        return true;
    }

    public ClassSymbol getClass(String name) { //returns the class based on name
        return classes.get(name);
    }

    public boolean containsClass(String name) { //returns true if class with name exists
        return classes.containsKey(name);
    }

    public Map<String, ClassSymbol> getClasses() { //returns all classes
        return classes;
    }
}
