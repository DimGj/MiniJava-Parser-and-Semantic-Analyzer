import java_cup.runtime.*;
import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("input.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        
        Scanner scanner = new Scanner(bufferedReader);
        Parser parser = new Parser(scanner);
        parser.parse();
        parser.writeIRToFile();
    }
}
