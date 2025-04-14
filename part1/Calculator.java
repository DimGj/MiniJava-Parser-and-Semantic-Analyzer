import java.io.IOException;

public class Calculator {
    private static int ch;

    public static void main(String[] args) throws IOException {
        while (true) {
            ch = skipWhitespace(System.in.read());
            if (ch == -1) break;

            try {
                int result = evaluateExpression(); // evaluate full expression

                if (ch == '\n' || ch == -1) {
                    System.out.println(result);
                } else {
                    error("Parse error: Unexpected characters after expression.");
                }

                while (ch != '\n' && ch != -1) { //skip to end of line
                    ch = System.in.read();
                }
            } catch (Exception e) {
                System.err.println("Parse error: Invalid expression.");

                while (ch != '\n' && ch != -1) { //skip to next line
                    ch = System.in.read();
                }
            }
        }
    }

    private static int skipWhitespace(int c) throws IOException {
        while (c == ' ' || c == '\t' || c == '\r') {       //as in piazza suggest just skip them
            c = System.in.read();
        }
        return c;
    }

    private static void expect(int expected) throws IOException {
        if (ch == expected) {   //double verify that the character is what we expect
            ch = skipWhitespace(System.in.read());
        } else {
            error("Parse error: Expected '" + (char) expected + "'");
        }
    }

    private static void error(String message) {
        throw new RuntimeException(message);
    }

    private static int evaluateExpression() throws IOException {
        int value = parseExponent(); //parse initially exponentiatio

        while (ch == '+' || ch == '-') { //addition and subtraction
            int op = ch;
            expect(op);
            int next = parseExponent();
            if (op == '+')
                value = value + next;
            else
                value = value - next;
            
        }

        return value;
    }

    private static int parseExponent() throws IOException {
        int base = parseElement(); //get base value

        if (ch == '*') {
            expect('*');
            if (ch == '*') {
                expect('*');
                int exponent = parseExponent(); //right-associative the exponent
                return (int) Math.pow(base, exponent);
            } else {
                error("Parse error: Single '*' is not valid.");
            }
        }

        return base;
    }

    private static int parseElement() throws IOException {
        if (ch == '(') {
            expect('(');
            int val = evaluateExpression(); //evaluate expression inside parentheses
            expect(')');
            return val;
        }

        if (Character.isDigit(ch)) { //either its a substrction or addition term either its num term
            return parseNumber(); //handle number parsing
        }

        error("Expected number or '('");
        return 0;
    }

    private static int parseNumber() throws IOException {
        int value = 0;
        while (Character.isDigit(ch)) {
            value = value * 10 + (ch - '0'); //build number from each digit (base 10)
            ch = skipWhitespace(System.in.read());
        }
        return value;
    }
}
