JFLEX_JAR = ../jflex-full-1.9.1.jar
CUP_JAR = ../java-cup-11b.jar

all: compile

compile:
	java -jar $(JFLEX_JAR) scanner.flex
	java -jar $(CUP_JAR) -parser Parser -symbols sym parser1.cup

execute:

clean:
	rm -f *.class *~ Parser.java sym.java Scanner.java
