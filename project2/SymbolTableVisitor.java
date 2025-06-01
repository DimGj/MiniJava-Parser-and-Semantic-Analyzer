import syntaxtree.*;
import visitor.*;
import symbolTable.*;

//i dont show the node rules for each override like in demo because i wanted smaller file size
//but you can find it thrugh my override parameter name

public class SymbolTableVisitor extends GJDepthFirst<Void, Void> { //took the overriding logic it from myVisitor in project2 demo
    public final SymbolTable symbolTable = new SymbolTable();

    private String currentClass = null;
    private String currentMethod = null;
    private final TypeExtractor typeExtractor = new TypeExtractor(); //did it to extract types from nodes, extended logic from demo project

    @Override
    public Void visit(MainClass n, Void arg) {
        String className = n.f1.f0.toString();
        boolean added = symbolTable.addClass(className, null);
        if (!added) {
            System.err.println("Error: Duplicate main class " + className);
            System.exit(1);
        }
        currentClass = className;

        ClassSymbol cls = symbolTable.getClass(className);
        cls.addMethod("main", "void");

        MethodSymbol mainMethod = cls.getMethod("main");
        mainMethod.addParameter(n.f11.f0.toString(), "String[]");
        currentMethod = "main";
        n.f14.accept(this, null);
        n.f15.accept(this, null);
        return null;
    }

    @Override
    public Void visit(ClassDeclaration n, Void arg) {
        currentMethod = null;
        String className = n.f1.f0.toString();
        boolean added = symbolTable.addClass(className, null);
        if (!added) {
            System.err.println("Error: Duplicate class " + className);
            System.exit(1);
        }
        currentClass = className;

        for (Node varDecl : n.f3.nodes) { //fields
            varDecl.accept(this, null);
        }
        for (Node methodDecl : n.f4.nodes) { //methods
            methodDecl.accept(this, null);
        }

        return null;
    }

    @Override
    public Void visit(ClassExtendsDeclaration n, Void arg) {
        String className = n.f1.f0.toString();
        String parentName = n.f3.f0.toString();
        boolean added = symbolTable.addClass(className, parentName);
        if (!added) {
            System.err.println("Error: Duplicate class " + className);
            System.exit(1);
        }
        currentClass = className;

        for (Node varDecl : n.f5.nodes) {
            varDecl.accept(this, null);
        }
        for (Node methodDecl : n.f6.nodes) {
            methodDecl.accept(this, null);
        }

        return null;
    }
    
    @Override
    public Void visit(VarDeclaration n, Void arg) {
        String varType = n.f0.accept(typeExtractor, null);
        String varName = n.f1.f0.toString();

        if (currentMethod != null) {
            //only local variables into methods
            MethodSymbol method = symbolTable.getClass(currentClass).getMethod(currentMethod);
            if (method == null) {
                System.err.println("Error: Could not find method symbol for '" + currentMethod + "' in class '" + currentClass + "'");
                System.exit(1);
            }
            if (!method.addLocal(varName, varType)) {
                System.err.println("Error: Duplicate local variable '" + varName + "' in method '" + currentMethod + "'");
                System.exit(1);
            }
        } else {
            // class fields
            ClassSymbol c = symbolTable.getClass(currentClass);
            if (!c.addField(varName, varType)) {
                System.err.println("Error: Duplicate field '" + varName + "' in class '" + currentClass + "'");
                System.exit(1);
            }
        }

        return null;
    }

    @Override
    public Void visit(MethodDeclaration n, Void arg) {
        String returnType = n.f1.accept(typeExtractor, null);
        String methodName = n.f2.f0.toString();

        ClassSymbol c = symbolTable.getClass(currentClass);
        boolean added = c.addMethod(methodName, returnType);
        if (!added) {
            System.err.println("Error: Duplicate method '" + methodName + "' in class '" + currentClass + "'");
            System.exit(1);
        }

        currentMethod = methodName;
        MethodSymbol method = c.getMethod(currentMethod);
        if (method == null) {
            System.err.println("Error: Could not find method symbol for '" + methodName + "' in class '" + currentClass + "'");
            System.exit(1);
        }

        //args
        if (n.f4.present()) {
            FormalParameterList paramList = (FormalParameterList) n.f4.node;
            FormalParameter firstParam = paramList.f0; //first param always present
            String firstType = firstParam.f0.accept(typeExtractor, null);
            String firstName = firstParam.f1.f0.toString();

            if (!method.addParameter(firstName, firstType)) {
                System.err.printf("Error: Duplicate parameter '%s' in method '%s' of class '%s'.\n", firstName, methodName, currentClass);
                System.exit(1);
            }

            //rest
            FormalParameterTail tail = paramList.f1;
            for (Node node : tail.f0.nodes) {
                FormalParameterTerm term = (FormalParameterTerm) node;
                FormalParameter param = term.f1;

                String type = param.f0.accept(typeExtractor, null);
                String name = param.f1.f0.toString();
                if (!method.addParameter(name, type)) {
                    System.err.printf("Error: Duplicate parameter '%s' in method '%s' of class '%s'.\n", name, methodName, currentClass);
                    System.exit(1);
                }
            }
        }
        
        //chcker for proper overriding method
        String parent = c.parent;
        while (parent != null) {
            ClassSymbol parentClass = symbolTable.getClass(parent);
            if (parentClass.methods.containsKey(methodName)) {
                MethodSymbol superMethod = parentClass.getMethod(methodName);

                //foo can be defined in a subclass if it has the same return type and argument types (ordered) as in the parent (apo ekfwnisi)
                if (!superMethod.returnType.equals(returnType) || superMethod.parameters.size() != method.parameters.size()) {
                    System.err.printf("Error: Method '%s' in class '%s' overloads method in superclass '%s'. Overloading not allowed.\n",methodName, currentClass, parent);
                    System.exit(1);
                }

                //check now types of args for both
                java.util.Iterator<String> parMethod = superMethod.parameters.keySet().iterator(); //ordering is safe because of LinkedHashMap (piazza @71)
                java.util.Iterator<String> subMethod = method.parameters.keySet().iterator();
                while (parMethod.hasNext() && subMethod.hasNext()) {
                    String superParamType = superMethod.parameters.get(parMethod.next()).type;
                    String subParamType = method.parameters.get(subMethod.next()).type;
                    if (!superParamType.equals(subParamType)) {
                        System.err.printf("Error: Method '%s' in class '%s' has parameter type mismatch with superclass method in '%s'.\n",methodName, currentClass, parent);
                        System.exit(1);
                    }
                }
                break;
            }
            parent = parentClass.parent;
        }

        if (n.f7.present()) 
            n.f7.accept(this, null); //method body
        if (n.f8.present()) 
            n.f8.accept(this, null); //return 

        currentMethod = null;
        return null;
    }


    public class TypeExtractor extends GJDepthFirst<String, Void> { //got the logic from lecture code, i refined it as a helping class

        @Override
        public String visit(IntegerType n, Void argu) {
            return "int";
        }

        @Override
        public String visit(BooleanType n, Void argu) {
            return "boolean";
        }

        @Override
        public String visit(ArrayType n, Void arg) {
            String baseType = n.f0.accept(this, null); //splits it to IntegerArrayType/BooleanArrayType
            return baseType;
        }

        @Override
        public String visit(IntegerArrayType n, Void arg) {
            return "int[]";
        }

        @Override
        public String visit(BooleanArrayType n, Void arg) {
            return "boolean[]";
        }

        @Override
        public String visit(Identifier n, Void argu) {
            return n.f0.toString();
        }

        @Override
        public String visit(Type n, Void arg) {
            return n.f0.accept(this, null); //find the actualy type node
        }
        
    }
}
