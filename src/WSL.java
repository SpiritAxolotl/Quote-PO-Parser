import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class WSL {
    public Quote readTables(File file, Out out, int type) throws Exception {
        @SuppressWarnings("unused") Stuff s = new Stuff();
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
        int lineSize = lines.size();
        //Parsing the stuff in the java table
        Quote quote = new Quote();
        quote.setID(lines.get(5));
        quote.setDate(lines.get(4));
        quote.setVendor(lines.get(7));
        //quote.setPayTerms(lines.get(3));
        out.debug("         ID - " + quote.getID());
        out.debug("       Date - " + quote.getDateString());
        out.debug("     Vendor - " + quote.getVendor());
        //out.debug("  Pay Terms - " + quote.getPayTerms());
        //quote.setMemo(lines.get(quote.findSetTotal(lines)-1));
        if (lineSize <= 40) {
            Order order = new Order(true);
            order.setDesc(lines.get(28));
            order.setQuantity(lines.get(29));
            order.setRate(lines.get(30));
            order.setJob(lines.get(31));
            order.setAmount(lines.get(33));
            quote.addOrder(order.isValid(out));
            out.debug("Description - " + order.getDesc());
            out.debug("   Quantity - " + order.getQuantity());
            out.debug("       Rate - " + order.getRate());
            out.debug("        Job - " + order.getJob());
            out.debug("     Amount - " + order.getAmount());
            if(quote.getLastOrder().getDesc().isBlank()){
                quote.removeOrder(quote.getOrders().size()-1);
                out.debug("Removing that last one");
            }
            //out.debug("       Memo - " + quote.getMemo());
            out.debug("      Total - $" + quote.getTotal());
        }
        out.println();
        return quote.isValid(out);
    }
}
