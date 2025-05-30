all: compile

compile:
	java -jar jtb132.jar minijava.jj
	javacc jtb.out.jj
	javac Main.java visitor/*.java syntaxtree/*.java symbolTable/*.java

run:
	java Main tests/mytest1.java

clean:
	rm -rf *.class *~
