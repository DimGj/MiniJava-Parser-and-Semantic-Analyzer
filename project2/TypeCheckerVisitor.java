import syntaxtree.*;
import visitor.*;
import symbolTable.*;

public class TypeCheckerVisitor extends GJDepthFirst<String, String> {
    private final SymbolTable symbolTable;
    private String currentClass = null;
    private String currentMethod = null;

    public TypeCheckerVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String visit(MainClass n, String arg) {
        currentClass = n.f1.f0.toString();
        currentMethod = "main";
        n.f14.accept(this, null);  //var decl
        n.f15.accept(this, null);  //main statements
        return null;
    }

    @Override
    public String visit(ClassDeclaration n, String arg) {
        currentClass = n.f1.f0.toString();
        if (n.f4.present()) {
            n.f4.accept(this, null); //method decl
        }
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n, String arg) {
        currentClass = n.f1.f0.toString();
        if (n.f6.present()) {
            n.f6.accept(this, null); //extened method decl
        }
        return null;
    }

    @Override
    public String visit(ThisExpression n, String arg) {
        return currentClass;
    }

    @Override
    public String visit(AndExpression n, String arg) {
        String left = n.f0.accept(this, null);
        String right = n.f2.accept(this, null);

        if (!left.equals("boolean") || !right.equals("boolean")) {
            System.err.printf("Error: Invalid operand types for '&&' in class '%s', method '%s'. Got '%s' and '%s'.\n",
                currentClass, currentMethod, left, right);
            System.exit(1);
        }
        return "boolean";
    }

    @Override
    public String visit(NotExpression n, String arg) {
        String exprType = n.f1.accept(this, null);

        if (!exprType.equals("boolean")) {
            System.err.printf("Error: Operand of '!' must be boolean, got '%s' (in class '%s', method '%s').\n",exprType, currentClass, currentMethod);
            System.exit(1);
        }

        return "boolean";
    }

    @Override
    public String visit(CompareExpression n, String arg) {
        String left = n.f0.accept(this, null);
        String right = n.f2.accept(this, null);

        if (!left.equals("int") || !right.equals("int")) { //i dont think anything else should be compared instead of int
            System.err.printf("Error: Invalid operand types for '<' in class '%s', method '%s'.\n", currentClass, currentMethod);
            System.exit(1);
        }

        return "boolean";
    }

    @Override
    public String visit(PlusExpression n, String arg) {
        String left = n.f0.accept(this, null);
        String right = n.f2.accept(this, null);

        if (!left.equals("int") || !right.equals("int")) {
            System.err.printf("Error: Invalid operand types for '+' in class '%s', method '%s'. Got '%s' and '%s'.\n",
                currentClass, currentMethod, left, right);
            System.exit(1);
        }

        return "int";
    }

    @Override
    public String visit(MinusExpression n, String arg) {
        String left = n.f0.accept(this, null);
        String right = n.f2.accept(this, null);

        if (!left.equals("int") || !right.equals("int")) {
            System.err.printf("Error: Invalid operand types for '-' in class '%s', method '%s'. Got '%s' and '%s'.\n",
                currentClass, currentMethod, left, right);
            System.exit(1);
        }

        return "int";
    }

    @Override
    public String visit(TimesExpression n, String arg) {
        String left = n.f0.accept(this, null);
        String right = n.f2.accept(this, null);

        if (!left.equals("int") || !right.equals("int")) {
            System.err.printf("Error: Invalid operand types for '*' in class '%s', method '%s'. Got '%s' and '%s'.\n",
                currentClass, currentMethod, left, right);
            System.exit(1);
        }

        return "int";
    }

    @Override
    public String visit(ArrayLookup n, String arg) {
        String arrayType = n.f0.accept(this, null);
        String indexType = n.f2.accept(this, null);
        String arrayVarName = null;
        if (n.f0 instanceof PrimaryExpression) { //i checked the grammar further , i wanted to print in error also which is variable is used for wrong array assignment
            Node choice = ((PrimaryExpression) n.f0).f0.choice;
            if (choice instanceof Identifier) {
                arrayVarName = ((Identifier) choice).f0.toString();
            }
        }

        if (!arrayType.equals("int[]") && !arrayType.equals("boolean[]")) {
            System.err.printf("Error: Cannot index variable '%s' of type '%s'. Only 'int[]' or 'boolean[]' supported (in class '%s', method '%s').\n",arrayVarName, arrayType, currentClass, currentMethod);
            System.exit(1);
        }

        if (!indexType.equals("int")) {
            System.err.printf("Error: Index in array access must be 'int', got '%s' (in class '%s', method '%s').\n",indexType, currentClass, currentMethod);
            System.exit(1);
        }

        if (arrayType.equals("int[]"))
            return "int";
        else
            return "boolean";
    }

    @Override
    public String visit(ArrayLength n, String arg) {
        String arrayType = n.f0.accept(this, null);

        if (!arrayType.equals("int[]") && !arrayType.equals("boolean[]")) {
            System.err.printf("Error: .length is only valid on arrays, got '%s' (in class '%s', method '%s').\n",arrayType, currentClass, currentMethod);
            System.exit(1);
        }
        return "int";
    }

    @Override
    public String visit(ArrayAllocationExpression n, String arg) {
        return n.f0.accept(this, arg); //2 syntax tree nodes will decide,i just parse here
    }

    @Override
    public String visit(BooleanArrayAllocationExpression n, String arg) {
        String sizeType = n.f3.accept(this, null);
        if (!sizeType.equals("int")) {
            System.err.printf("Error: Boolean array size must be 'int', got '%s' (in class '%s', method '%s').\n",sizeType, currentClass, currentMethod);
            System.exit(1);
        }
        return "boolean[]";
    }

    @Override
    public String visit(IntegerArrayAllocationExpression n, String arg) {
        String sizeType = n.f3.accept(this, null);
        if (!sizeType.equals("int")) {
            System.err.printf("Error: Integer array size must be 'int', got '%s' (in class '%s', method '%s').\n",sizeType, currentClass, currentMethod);
            System.exit(1);
        }
        return "int[]";
    }

    @Override
    public String visit(ArrayAssignmentStatement n, String arg) {
        String arrName = n.f0.f0.toString();
        String arrType = resolveVariableType(arrName); //get what type is

        if (!arrType.equals("int[]") && !arrType.equals("boolean[]")) {
            System.err.printf("Error: Variable '%s' is not an array in method '%s' of class '%s'.\n", arrName, currentMethod, currentClass);
            System.exit(1);
        }

        String indexType = n.f2.accept(this, null);
        if (!indexType.equals("int")) {
            System.err.printf("Error: Array index must be of type 'int', but got '%s' for array '%s' in method '%s'.\n", indexType, arrName, currentMethod);
            System.exit(1);
        }

        String valueType = n.f5.accept(this, null);
        String expectedType;
        if (arrType.equals("int[]"))
            expectedType = "int";
        else
            expectedType = "boolean";

        if (!valueType.equals(expectedType)) {
            System.err.printf("Error: Cannot assign '%s' to element of array '%s' (expected '%s') in method '%s'.\n", valueType, arrName, expectedType, currentMethod);
            System.exit(1);
        }
        return null;
    }

    @Override
    public String visit(BooleanType n, String arg) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, String arg) {
        return "int";
    }

    @Override
    public String visit(IntegerArrayType n, String arg) {
        return "int[]";
    }

    @Override
    public String visit(Identifier n, String arg) {
        if ("type".equals(arg)) {
            return n.f0.toString(); //is type identifier (class object name e.g.)
        } else {
            String type = resolveVariableType(n.f0.toString()); //is expression
            return type;
        }
    }

    @Override
    public String visit(VarDeclaration n, String arg) {
        String type = n.f0.accept(this, "type"); //get the type of the variable
        String name = n.f1.f0.toString();

        //check for redeclaration and mapping
        MethodSymbol method = symbolTable.getClass(currentClass).getMethod(currentMethod);
        if (method != null && !method.locals.containsKey(name))
            method.addLocal(name, type); //need to resolve the type of the var (i know this ideally should be done in SymbolTableVisitor,but had too many bugs)

        return null;
    }

    @Override
    public String visit(Expression n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(PrimaryExpression n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(ExpressionTerm n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(MethodDeclaration n, String arg) {
        currentMethod = n.f2.f0.toString();

        //get params types
        if (n.f4.present()) {
            FormalParameterList paramList = (FormalParameterList) n.f4.node;

            // First param
            paramList.f0.f0.accept(this, "type");

            // Rest params from FormalParameterTail
            FormalParameterTail tail = paramList.f1;
            for (Node node : tail.f0.nodes) {
                FormalParameterTerm term = (FormalParameterTerm) node;
                term.f1.f0.accept(this, "type");
            }
        }

        n.f8.accept(this, null); //decl inside the method
        String actualReturnType = n.f10.accept(this, null);    // actual return expression

        String expectedReturnType = symbolTable.getClass(currentClass).getMethod(currentMethod).returnType;
        if (!isAssignable(expectedReturnType, actualReturnType)) {
            System.err.printf("Error: Return type mismatch in method '%s' of class '%s'. Expected '%s' but got '%s'.\n",currentMethod, currentClass, expectedReturnType, actualReturnType);
            System.exit(1);
        }

        currentMethod = null;
        return null;
    }

    @Override
    public String visit(Statement n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(AllocationExpression n, String arg) {
        return n.f1.accept(this, "type");
    }

    @Override
    public String visit(BracketExpression n, String arg) {
        return n.f1.accept(this, null); //the inner expr
    }

    @Override
    public String visit(IntegerLiteral n, String arg) {
        return "int";
    }

    @Override
    public String visit(TrueLiteral n, String arg) {
        return "boolean";
    }

    @Override
    public String visit(FalseLiteral n, String arg) {
        return "boolean";
    }

    @Override
    public String visit(Clause n, String arg) {
        return n.f0.accept(this, arg); // Clause → PrimaryExpression
    }

    @Override
    public String visit(AssignmentStatement n, String arg) {
        String varName = n.f0.f0.toString();
        String varType = resolveVariableType(varName);
        String exprType = n.f2.accept(this, null);

        if (!isAssignable(varType, exprType)) {
            System.err.printf("Error: Cannot assign '%s' to variable '%s' of type '%s' in method '%s'.\n",
                exprType, varName, varType, currentMethod);
            System.exit(1);
        }
        return null;
    }

    @Override
    public String visit(MessageSend n, String arg) {
        String objectType = n.f0.accept(this, null);
        ClassSymbol c = symbolTable.getClass(objectType);
        if (c == null) { //check if the class exists or object was primitive type
            System.err.printf("Error: Type '%s' not found for method call '%s'.\n", objectType, n.f2.f0.toString());
            System.exit(1);
        }

        String methodName = n.f2.f0.toString(); //search for method in class or parent ones
        MethodSymbol method = null; 
        ClassSymbol currClass = c;
        while (currClass != null) {
            if (currClass.methods.containsKey(methodName)) { //base case: method found in curr class
                method = currClass.methods.get(methodName);
                break;
            }
            if (currClass.parent == null) //base case: no further parent
                break;
            currClass = symbolTable.getClass(currClass.parent); //continue recursion (get to next parent)
        }
        if (method == null) {
            System.err.printf("Error: Method '%s' not found in class '%s'.\n", methodName, objectType);
            System.exit(1);
        }

        int expectedargNum = method.parameters.size(); //i compare now the numbers and types of args
        int actualArgNum = 0;
        if (n.f4.present()) {
            ExpressionList expr = (ExpressionList) n.f4.node;
            java.util.List<String> actualArgs = getArgTypes(expr); //get what arg types were passed
            actualArgNum = actualArgs.size(); //and how many

            if (actualArgNum != expectedargNum) {
                System.err.printf("Error: Method '%s' in class '%s' expects %d arguments but got %d (on class '%s').\n",methodName, objectType, expectedargNum, actualArgNum, currentClass);
                System.exit(1);
            }

            //type checking of each argument
            int k = 0; 
            for (String currName : method.parameters.keySet()) { //method.param store order thanks to LinkedHashMap that i did it earlier
                String expectedType = method.parameters.get(currName).type; //get the type from the declaration
                String actualType = actualArgs.get(k); //get the type from the symbolType from frist pass
                if (!isAssignable(expectedType, actualType)) {
                    System.err.printf("Error: Argument %d of method '%s' expects '%s' but got '%s' (on class '%s').\n", k + 1, methodName, expectedType, actualType, currentClass );
                    System.exit(1);
                }
                k++;
            }
        } else if (expectedargNum > 0) { //in case of 0 args but method expects some
            System.err.printf("Error: Method '%s' in class '%s' expects %d arguments but got none.\n",methodName, objectType, expectedargNum);
            System.exit(1);
        }

        return method.returnType;
    }

    @Override
    public String visit(PrintStatement n, String arg) {
        String exprType = n.f2.accept(this, "print");

        if (exprType == null) {
            System.err.println("Error: Expression in print statement could not be resolved.");
            System.exit(1);
        }

        if (!exprType.equals("int") && !exprType.equals("boolean")) {
            System.err.printf("Error: Cannot print expression of type '%s'. Only 'int' and 'boolean' are allowed.\n", exprType);
            System.exit(1);
        }

        return null;
    }

    @Override
    public String visit(IfStatement n, String arg) {
        String condition = n.f2.accept(this, null);
        if (!"boolean".equals(condition)) {
            System.err.printf("Error: Condition in 'if' statement must be boolean, got '%s' (in class '%s', method '%s').\n", condition, currentClass, currentMethod);
            System.exit(1);
        }
        n.f4.accept(this, null); //true code
        n.f6.accept(this, null); //flase code (always present)
        return null;
    }

    @Override
    public String visit(WhileStatement n, String arg) {
        String condition = n.f2.accept(this, null);
        if (!"boolean".equals(condition)) {
            System.err.printf("Error: Condition in 'while' statement must be boolean, got '%s' (in class '%s', method '%s').\n", condition, currentClass, currentMethod);
            System.exit(1);
        }
        n.f4.accept(this, null); //body
        return null;
    }


    //-------------------------------------------------------helping functions------------------------------------------------------------

    private String resolveVariableType(String varName) {
        ClassSymbol c = symbolTable.getClass(currentClass);
        MethodSymbol method = c.getMethod(currentMethod);

        if (method.locals.containsKey(varName)){  //first is local var
            return method.locals.get(varName).type;
        }
        if (method.parameters.containsKey(varName))  //second is param
            return method.parameters.get(varName).type;
        if (c.fields.containsKey(varName))  //third is class field
            return c.fields.get(varName).type;

        String parent = c.parent; //fourth: i search parent case (if we have inheritance)
        while (parent != null) {
            ClassSymbol parentClass = symbolTable.getClass(parent);
            if (parentClass.fields.containsKey(varName)) {
                return parentClass.fields.get(varName).type;
            }
            parent = parentClass.parent;
        }

        System.err.printf("Error: Variable '%s' not found in scope of method '%s' in class '%s'.\n",varName, currentMethod, currentClass);
        System.exit(1);
        return null;
    }

    private java.util.List<String> getArgTypes(ExpressionList expr) {
        java.util.List<String> types = new java.util.ArrayList<>(); //i store them in a list

        Expression first = expr.f0; //first separately extracted due to grammar rule
        types.add(first.accept(this, null));

        //rest expr
        for (Node node : expr.f1.f0.nodes) {
            ExpressionTerm term = (ExpressionTerm) node; //i saw that are like key values (',' expr)
            Expression e = term.f1;
            types.add(e.accept(this, null));
        }

        return types;
    }

    private boolean isAssignable(String to, String from) {

        if (to.equals(from)) 
            return true;
        if (from.equals("null") && !to.equals("int") && !to.equals("boolean")) 
            return true;

        ClassSymbol current = symbolTable.getClass(from);
        while (current != null) {
            if (current.name.equals(to)) 
                return true;
            current = symbolTable.getClass(current.parent);
        }

        return false;
    }

}
