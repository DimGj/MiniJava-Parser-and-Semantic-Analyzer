JFLEX_JAR = ../jflex-full-1.9.1.jar
CUP_JAR = ../java-cup-11b.jar
CUP_RUNTIME_JAR = ../java-cup-11b-runtime.jar

all: compile
#
compile:
	java -jar $(JFLEX_JAR) scanner.flex
	java -jar $(CUP_JAR) -parser Parser -symbols sym parser.cup
	javac -cp $(CUP_RUNTIME_JAR):. *.java

execute:
#java -cp $(CUP_RUNTIME_JAR):. Main < input.txt

clean:
	rm -f *.class *~ Parser.java sym.java Scanner.java
