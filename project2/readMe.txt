BUILD & RUN INSTRUCTIONS

1. To compile the project:
   make all

2. To run semantic analysis on a test file:
   java Main [path/yourTestFile.java]

Note: The program supports one test file per run (sorry for extra grading time).
Please run the command separately for each file, i also have all the tests that were used for check in 'tests' folder.

KNOWN WARNINGS

During compilation, you may see two warnings related to
deprecated methods from JTB. These are known and do not
affect the correctness of the program.

SEMANTIC ANALYSIS BEHAVIOR

- On detecting a semantic or symbol table error, the program
  prints an error message to stderr and exits immediately 
  with status 1.
- No further AST traversal is performed after the first error. (i thought it really is not needed)

SEMANTIC RULES IMPLEMENTED

1. Comparison expressions (<, ==, etc.):
   - Both operands must be of type int.

2. Arithmetic expressions (+, -, *):
   - Both operands must be of type int.

3. if and while statements:
   - Condition must be of type boolean (int is not accepted).
     (See Piazza @90 for clarification)

4. Logical AND (&&):
   - Both operands must be of type boolean.

5. Print statements (System.out.println(...)):
   - Only int and boolean types are allowed.

ADDITIONAL NOTES

- All semantic checks are performed in the TypeCheckerVisitor.
- Symbol table is constructed first using SymbolTableVisitor.
