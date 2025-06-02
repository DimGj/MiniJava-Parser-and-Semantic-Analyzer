import symbolTable.*;
import java.util.*;

public class offsetPrinter {

    public static void print(SymbolTable symbolTable) {
        Set<String> printed = new HashSet<>(); //hash i use for not reprinting classes

        while (printed.size() < symbolTable.getClasses().keySet().size()) {
            for (String className : symbolTable.getClasses().keySet()) {
                if (printed.contains(className)) 
                    continue;

                ClassSymbol c = symbolTable.getClass(className);
                if (c.parent != null && !printed.contains(c.parent)) //make sure parent is printed first
                    continue;

                classOffsets(c, symbolTable);
                printed.add(className);
            }
        }
    }

    private static void classOffsets(ClassSymbol c, SymbolTable symbolTable) {
        int fieldOffset = 0;
        int methodOffset = 0;
        Map<String, Integer> methodNameToOffset = new LinkedHashMap<>();
        Set<String> inheritedMethods = new HashSet<>();

        if (c.parent != null) { //need to continue counting from parent
            ClassSymbol parent = symbolTable.getClass(c.parent);
            fieldOffset = fieldOffset(parent, symbolTable);
            methodOffset = methodOffset(parent, symbolTable);
        }

        if (c.parent != null) {
            ClassSymbol parent = symbolTable.getClass(c.parent);
            inheritedMethods.addAll(parent.methods.keySet());
            int offset = 0;
            for (String name : parent.methods.keySet()) {
                methodNameToOffset.put(name, offset);
                offset += 8;
            }
        }

        //vars
        for (VariableSymbol field : c.fields.values()) {
            System.out.printf("%s.%s : %d\n", c.name, field.name, fieldOffset);
            fieldOffset += sizeOf(field.type);
        }

        //methods
        for (MethodSymbol method : c.methods.values()) {
            if (methodNameToOffset.containsKey(method.name)) //overridden or inherited method, not counting
                continue;
            System.out.printf("%s.%s : %d\n", c.name, method.name, methodOffset);
            methodOffset += 8;
        }
    }

    private static int fieldOffset(ClassSymbol c, SymbolTable symbolTable) {
        int offset = 0;
        if (c.parent != null) {
            offset = fieldOffset(symbolTable.getClass(c.parent), symbolTable);
        }
        for (VariableSymbol var : c.fields.values()) {
            offset += sizeOf(var.type);
        }
        return offset;
    }

    private static int methodOffset(ClassSymbol c, SymbolTable symbolTable) {
        Set<String> tracked = new HashSet<>(); //i keep track of methods already counted
        int offset = 0;
        if (c.parent != null) {
            offset = methodOffset(symbolTable.getClass(c.parent), symbolTable);
            tracked.addAll(symbolTable.getClass(c.parent).methods.keySet());
        }
        for (String name : c.methods.keySet()) {
            if (!tracked.contains(name)) {
                offset += 8;
            }
        }
        return offset;
    }

    private static int sizeOf(String type) {
        switch (type) {
            case "int": return 4;
            case "boolean": return 1;
            default: return 8; //class types, arr, methods according to ekfwnisi
        }
    }
}
