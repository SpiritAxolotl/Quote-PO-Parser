import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class App {
    static File[] dir = (new File("src\\inputs\\extracted\\")).listFiles();
    //static File[] pos = (new File("src\\inputs\\POs")).listFiles();
    //static File[] quotes = (new File("src\\inputs\\Quotes")).listFiles();
    static ArrayList<PO> poList = new ArrayList<PO>();
    static ArrayList<Quote> quoteList = new ArrayList<Quote>();
    
    public static boolean match(String regex, String match) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(match);
        return m.matches();
    }
    //PO ID, date, vendor, orders(desc, quantity, rate, job, amount), total, tracker
    //reminder: comment how everything works later.

    public static void main(String[] args) throws Exception {
        Out out = new Out("src\\outputs\\output.txt");
        PrintWriter outPOs = new PrintWriter("src\\outputs\\POs.csv");
        PrintWriter outQuotes = new PrintWriter("src\\outputs\\Quotes.csv");
        outPOs.println("ID,Date,Vendor,Description,Quantity,Rate,Job,Amount,Total,Memo,Payment Terms,Tracker");
        File temp = new File("src\\temp.txt");
        temp.deleteOnExit();
        ArrayList<String> matchList = new ArrayList<String>();
        Scanner scan = new Scanner(new File("src\\inputs\\matches.txt"));
        while (scan.hasNextLine()) {
            matchList.add(scan.nextLine());
        }
        scan.close();
        
        Tabula t = new Tabula();
        WSL wsl = new WSL();
        //iterate through the POs
        for (File folder : dir) {for (File aPDF : folder.listFiles()) {
            out.println("Current file: " + aPDF.getName());
            out.println("Filepath: " + aPDF.getPath());
            if (match(matchList.get(0), aPDF.getName())) {
                out.println("Matches!\n");
                poList.add(t.readTablesPO(aPDF, out));
            } else if (match(matchList.get(1), aPDF.getName())) {
                quoteList.add(wsl.pdfToTextQuote(aPDF, out, 1));
            } else if (match(matchList.get(2), aPDF.getName())) {
                quoteList.add(wsl.pdfToTextQuote(aPDF, out,2));
            } else if (match(matchList.get(3), aPDF.getName())) {
                quoteList.add(wsl.pdfToTextQuote(aPDF, out, 3));
            } else if (match(matchList.get(4), aPDF.getName())) {
                quoteList.add(wsl.pdfToTextQuote(aPDF, out, 4));
            } else {
                out.println("File didn't match.\n");
            }
        }}
        for (PO p : poList) {
            outPOs.println(p.toCSV());
        }

        outPOs.close();
        outQuotes.close();

        try {
            // Define the WSL command to run
            String wslCommand = "wsl ls -l"; // Replace 'ls -l' with your desired Linux command
            
            // Create a ProcessBuilder with the WSL command
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", wslCommand);
            
            // Redirect error stream to the same as the output stream
            processBuilder.redirectErrorStream(true);
            
            // Start the process
            Process process = processBuilder.start();
            
            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        out.close();
    }
}
