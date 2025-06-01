Run instructions:
1. make all
2. java Main [yourTestFile.java]

During compilation you will see 2 warning from jtb for 2 deprecated methods of it, i tried resolve thme but i couldnt, i thought they are not really essential warnings.

Notes:
My program as seen in run instructions supports only one TestFile per run (sorry for extra grading time).
I decided when it reaches an error semantic or in symbolTable to exit the program with status 1 and do not continue examining the AST.

1. On comparison expressions i thought only int should be compared.
2. While arithmetic must have both int.
3. If and while can only have boolean conditions (i dont even accept int piazza @90)
4. && must have both vars as boolean
5. Print statements can only print int or booleans
