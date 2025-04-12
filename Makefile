all: compile run

compile:
	javac Calculator.java

run:
	java Calculator

clean:
	rm -f *.class *~