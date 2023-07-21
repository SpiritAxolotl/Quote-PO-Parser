import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class WSL {
    private PO po;
    private Quote quote;
    public PO pdfToTextPO(File file, Out out, int type) throws Exception {
        this.wslSession(file, out, type);
        return this.po;
    }
    public Quote pdfToTextQuote(File file, Out out, int type) throws Exception {
        this.wslSession(file, out, type);
        return this.quote;
    }
    public void wslSession(File file, Out out, int type) throws Exception {
        try {
            // Define the WSL command to run
            String wslCommand = "wsl pdftotext \"" + file.getPath().replaceAll("\\\\", "/").replaceAll("\"", "\\\"") + "\" \"src/temp.txt\"";
            out.println("Running command \"" + wslCommand + "\"...");
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
                out.println(line);
            }
            
            // Wait for the process to complete
            int exitCode = process.waitFor();
            out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Scanner scan = new Scanner(new File("src\\temp.txt"));
        ArrayList<String> lines = new ArrayList<String>();
        int k = 0;
        while (scan.hasNextLine()) {
            String curr = scan.nextLine().strip();
            lines.add(curr);
            out.println(k + ": " + curr);
            k++;
        }
        scan.close();
    }
}
