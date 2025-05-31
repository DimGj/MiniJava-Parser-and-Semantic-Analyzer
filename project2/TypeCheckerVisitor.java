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
    public String visit(AndExpression n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(CompareExpression n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(PlusExpression n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(MinusExpression n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(TimesExpression n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(ArrayLookup n, String arg) {
        return n.f0.accept(this, arg); // base array
    }

    @Override
    public String visit(ArrayLength n, String arg) {
        return "int";
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
            paramList.f0.f0.accept(this, "type"); //get first param due to grammar

            NodeListOptional tail = paramList.f1.f0; //rest params
            for (int i = 0; i < tail.size(); i++) {
                NodeSequence paramSeq = (NodeSequence) tail.elementAt(i);
                FormalParameter param = (FormalParameter) paramSeq.elementAt(1);
                param.f0.accept(this, "type");
            }
        }

        n.f8.accept(this, null); //decl inside the method
        String actualReturnType = n.f10.accept(this, null);    // actual return expression

        String expectedReturnType = symbolTable.getClass(currentClass).getMethod(currentMethod).returnType;
        if (!expectedReturnType.equals(actualReturnType)) {
            System.err.printf(
                "Error: Return type mismatch in method '%s' of class '%s'. Expected '%s' but got '%s'.\n",currentMethod, currentClass, expectedReturnType, actualReturnType
            );
            System.exit(1);
        }

        return null;
    }

    @Override
    public String visit(Statement n, String arg) {
        return n.f0.accept(this, arg);
    }

    @Override
    public String visit(AllocationExpression n, String arg) {
        return n.f1.f0.toString(); 
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

        if (!varType.equals(exprType)) {
            System.err.printf("Error: Cannot assign '%s' to variable '%s' of type '%s' in method '%s'.\n",exprType, varName, varType, currentMethod);
            System.exit(1);
        }
        return null;
    }

    @Override
    public String visit(MessageSend n, String arg) {
        String objectType = n.f0.accept(this, null);

        ClassSymbol c = symbolTable.getClass(objectType);
        if (c == null) {
            System.err.printf("Error: Type '%s' not found for method call '%s'.\n", objectType, n.f2.f0.toString());
            System.exit(1);
        }

        String methodName = n.f2.f0.toString();
        MethodSymbol method = c.getMethod(methodName);
        if (method == null) {
            System.err.printf("Error: Method '%s' not found in class '%s'.\n", methodName, objectType);
            System.exit(1);
        }

        //TODO: need to arg check also how many and what type is returned

        return method.returnType;
    }

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

        System.err.printf("Error: Variable '%s' not found in scope of method '%s' in class '%s'.\n",
                varName, currentMethod, currentClass);
        System.exit(1);
        return null;
    }
}
