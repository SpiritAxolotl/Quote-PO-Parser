import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class WSL {
    public Quote readTables(File file, Out out, int type) throws Exception {
        Stuff s = new Stuff();
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
        int k = 0;
        while (scan.hasNextLine()) {
            String curr = scan.nextLine().strip();
            linest.add(curr);
            out.println(k + ": " + curr);
            k++;
        }
        scan.close();
        String[] lines = s.stringArrayListToArray(linest);
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
            while(!lines[index].equals("DESCRIPTION") && index<lineSize) {
                index++;
            }
            ArrayList<Order> orderlist = new ArrayList<Order>();
            //Order order = new Order(false);
            int count = 0;
            while(!lines[index].isBlank() && index<lineSize){
                int sep = lines[index].indexOf(" ");
                if(lines[index].substring(0, sep).matches("\\d+[a-z]{1,3}")){
                    orderlist.add(new Order(false));
                    orderlist.get(count).setDesc(lines[index].substring(sep+1));
                } else {
                    orderlist.get(count).appendDesc(" | " + lines[index].substring(sep+1));
                }
            }
            //skip a part that we don't need but is always the same
            index += 16;
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
            /*out.debug("Description - " + order.getDesc());
            out.debug("   Quantity - " + order.getQuantity() + order.getQtyUnit());
            out.debug(" Unit Price - " + order.getRate() + "/" + order.getRateUnit());
            out.debug("  Ext Price - " + order.getAmount());*/
            if(quote.getLastOrder().getDesc().isBlank()){
                quote.removeOrder(quote.getOrders().size()-1);
                out.debug("Removing that last one");
            }
            //out.debug("     Amount - " + quote.getTotal());
            break;
        }
        out.println();
        //return quote.isValid(out);
        return quote;
    }
}
