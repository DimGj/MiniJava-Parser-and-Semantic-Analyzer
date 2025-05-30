import syntaxtree.*;
import visitor.*;
import symbolTable.*;


//i dont show the node rules for each override like in demo because i wanted smaller file size
//but you can find it thrugh my override parameter name

public class myVisitor extends GJDepthFirst<Void, Void> { //took the overriding logic it from myVisitor in project2 demo
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
        n.f15.accept(this, null);
        return null;
    }

    @Override
    public Void visit(ClassDeclaration n, Void arg) {
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
        String type = n.f0.accept(typeExtractor, null);
        String varName = n.f1.f0.toString();

        ClassSymbol c = symbolTable.getClass(currentClass);
        if (currentMethod == null) {
            boolean ok = c.addField(varName, type);
            if (!ok) {
                System.err.println("Error: Duplicate field '" + varName + "' in class " + currentClass);
                System.exit(1);
            }
        } else {
            MethodSymbol method = c.getMethod(currentMethod);
            boolean ok = method.addLocal(varName, type);
            if (!ok) {
                System.err.println("Error: Variable '" + varName + "' redeclared in method " + currentMethod);
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
        boolean ok = c.addMethod(methodName, returnType);
        if (!ok) {
            System.err.println("Error: Duplicate method '" + methodName + "' in class " + currentClass);
            System.exit(1);
        }

        currentMethod = methodName;
        MethodSymbol method = c.getMethod(methodName);

        //get args of the method
        if (n.f4.present()) {
            NodeSequence paramList = (NodeSequence) n.f4.node;
            Node firstParam = paramList.elementAt(0);
            NodeListOptional tailParams = (NodeListOptional) paramList.elementAt(1);

            NodeSequence paramSeq = (NodeSequence) firstParam;
            //caution i get the first arg alone becuase of rule: FormalParameterList ::= FormalParameter ( "," FormalParameter )
            String firstType = paramSeq.elementAt(0).accept(typeExtractor, null);

            String firstName = ((Identifier) paramSeq.elementAt(1)).f0.toString();

            if (!method.addParameter(firstName, firstType)) {
                System.err.println("Error: Duplicate parameter '" + firstName + "' in method " + methodName);
                System.exit(1);
            }

            for (Node tail : tailParams.nodes) { //iterate through the rest of the parameters
                NodeSequence pair = (NodeSequence) ((NodeSequence) tail).elementAt(1); //problem with comma after each var due to grammar, i skip it
                String type = pair.elementAt(0).accept(typeExtractor, null);
                String name = ((Identifier) pair.elementAt(1)).f0.toString();

                if (!method.addParameter(name, type)) {
                    System.err.println("Error: Duplicate parameter '" + name + "' in method " + methodName);
                    System.exit(1);
                }
            }
        }

        for (Node varDecl : n.f7.nodes) {
            varDecl.accept(this, null);
        }

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
    }
}
