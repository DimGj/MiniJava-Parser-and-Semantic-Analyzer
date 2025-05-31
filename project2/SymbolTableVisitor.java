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

        ClassSymbol cls = symbolTable.getClass(currentClass);
        boolean added = cls.addMethod(methodName, returnType);
        if (!added) {
            System.err.println("Error: Duplicate method '" + methodName + "' in class '" + currentClass + "'");
            System.exit(1);
        }

        currentMethod = methodName;
        MethodSymbol method = cls.getMethod(currentMethod);
        if (method == null) {
            System.err.println("Error: Could not find method symbol for '" + methodName + "' in class '" + currentClass + "'");
            System.exit(1);
        }

        //args
        if (n.f4.present()) {
            FormalParameterList paramList = (FormalParameterList) n.f4.node;

            String firstType = paramList.f0.f0.accept(typeExtractor, null); //first parameter type
            String firstName = paramList.f0.f1.f0.toString();
            if (!method.addParameter(firstName, firstType)) {
                System.err.println("Error: Duplicate parameter '" + firstName + "' in method '" + methodName + "'");
                System.exit(1);
            }

            //rest
            NodeListOptional tail = paramList.f1.f0;
            for (int i = 0; i < tail.size(); i++) {
                NodeSequence paramSeq = (NodeSequence) tail.elementAt(i);
                FormalParameter param = (FormalParameter) paramSeq.elementAt(1);
                String paramType = param.f0.accept(typeExtractor, null);
                String paramName = param.f1.f0.toString();

                if (!method.addParameter(paramName, paramType)) {
                    System.err.println("Error: Duplicate parameter '" + paramName + "' in method '" + methodName + "'");
                    System.exit(1);
                }
            }
        }

        n.f7.accept(this, null); //method body
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
        public String visit(IntegerArrayType n, Void argu) {
            return "int[]";
        }

        @Override
        public String visit(BooleanArrayType n, Void argu) {
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
