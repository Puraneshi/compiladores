import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Check if the input file name is provided as a command-line argument
        if (args.length != 1) {
            System.err.println("Usage: java Main <input_file>");
            System.exit(1);
        }
        
        // Get the input file path from the command-line argument
        String filePath = args[0];
        
        // Read the input file
        String inputText = readFile(filePath);
        
        // Instantiate the LexicalAnalyzer
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(inputText);
        try{
	    lexicalAnalyzer.analyze();
        // Analyze the input text
            //Token token = lexicalAnalyzer.analyze();
            //if(token == null){
             //throw new Exception("Nenhum token identificado.");
            //}
        } catch (Exception e){
	    System.out.println("");
            System.out.println(e.getMessage()+" ❌ 🤦‍♀️");
        }
    }
    
    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
