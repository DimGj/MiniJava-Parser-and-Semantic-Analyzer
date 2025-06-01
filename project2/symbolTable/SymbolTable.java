package symbolTable;
import java.util.Map;
import java.util.LinkedHashMap; //as it was said on piazza @71 
import java.util.HashSet;
import java.util.Set;

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

    public void checkForInheritanceCycles() {
        for (String className : classes.keySet()) {
            Set<String> visited = new HashSet<>();
            String curr = className;

            while (curr != null) {
                if (visited.contains(curr)) {
                    System.err.printf("Error: Circular inheritance detected involving class '%s'\n", className);
                    System.exit(1);
                }
                visited.add(curr);
                ClassSymbol c = classes.get(curr);
                if (c == null) break;
                curr = c.parent;
            }
        }
    }
}
