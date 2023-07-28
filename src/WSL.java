import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class WSL extends Base {
    public Quote readTables(File file, Out out, int type) throws Exception {
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
        ArrayList<String> linest = new ArrayList<String>();
        for (int k=0; scan.hasNextLine(); k++) {
            String curr = scan.nextLine().strip();
            linest.add(curr);
            out.println(k + ": " + curr);
        }
        scan.close();
        String[] lines = stringArrayListToArray(linest);
        int lineSize = lines.length;
        //Parsing the stuff in the java table
        Quote quote = new Quote();
        switch(type) {
        case 0:
            quote.setID(lines[14]);
            quote.setCustomerNum(lines[48]);
            quote.setDate(lines[68]);
            quote.setVendor(lines[17]);
            out.debug("  Quote Num - " + quote.getID());
            out.debug("CustomerNum - " + quote.getCustomerNum());
            out.debug("  Ship Date - " + quote.getDateString());
            out.debug("     Vendor - " + quote.getVendor());
            int index = 0;
            //int pages = Integer.parseInt(lines[26].substring(lines[26].length()-1));
            //do this for every page
            for (int v : instancesOf(lines, "Quotation")) {
                index = v;
                while(!lines[index].equals("DESCRIPTION") && index<lineSize) {
                    index++;
                }
                index += 2;
                ArrayList<Order> orderlist = new ArrayList<Order>();
                int count = 0;
                while(!lines[index].isBlank() && index<lineSize){
                    int sep = lines[index].indexOf(" ");
                    if(lines[index].substring(0, sep).matches("\\d+[a-z]{1,3}")){
                        orderlist.add(new Order(false));
                        orderlist.get(count).setDesc(lines[index].substring(sep+1));
                    } else {
                        orderlist.get(count).appendDesc(" | " + lines[index].substring(sep+1));
                    }
                    index++;
                }
                //skip a part that we don't need but is always the same
                index += 14;
                if (lines[index].equals("EXT PRICE")) {
                    //we need another two to keep track of the orderlist stuff
                    int[] count2 = {0,0};
                    //true = unit price, false = ext price
                    boolean toggle = true;
                    while(!lines[index].equals("Subtotal") && index<lineSize) {
                        if(lines[index].isBlank()){
                            toggle = !toggle;
                        } else if (toggle){
                            orderlist.get(count2[0]).setRate(lines[index]);
                            count2[0]++;
                        } else {
                            orderlist.get(count2[1]).setAmount(lines[index]);
                            count2[1]++;
                        }
                        index++;
                    }
                    quote.addOrders(orderlist);
                    while(lines[index].equals("Subtotal") && index<lineSize) {
                        index += 2;
                    }
                    quote.setSubtotal(lines[index]);
                    quote.setSNH(lines[index+=4]);
                    quote.setTotal(lines[index+=4]);
                    quote.findTax();
                } else {
                    // stop when line doesn't match \\d+\\.\\d+\\/[a-z]|[A-X]+
                }
                
                if(quote.getLastOrder().getDesc().isBlank()){
                    quote.removeOrder(quote.getOrders().size()-1);
                    out.debug("Removing that last one");
                }
            }
            break;
        }
        out.println();
        //return quote.isValid(out);
        return quote;
    }
}
