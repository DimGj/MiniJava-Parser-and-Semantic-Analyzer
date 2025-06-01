import syntaxtree.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java Main <MiniJavaFile.java>");
            System.exit(1);
        }

        for (String fileName : args) { //extend it to print also the file name
            try {
                System.out.println("Processing: " + fileName);
                FileInputStream fis = new FileInputStream(fileName);

                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();

                SymbolTableVisitor visitor = new SymbolTableVisitor();
                root.accept(visitor, null);
                System.out.println("Symbol table built successfully for: " + fileName);

                TypeCheckerVisitor typeChecker = new TypeCheckerVisitor(visitor.symbolTable);
                root.accept(typeChecker, null);
                System.out.println("type checking completed for: " + fileName);
                
            } catch (ParseException e) {
                System.err.println("Parse error in file: " + fileName);
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + fileName);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("FINISHED");
        }
    }
}
