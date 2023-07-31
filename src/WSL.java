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
            quote.setID(lines[findNextValue(lines, findTwoSpecificThing(lines, "QUOTE NUMBER", "ORDER NUMBER"), true)]);
            try {
                out.debug(file.getParentFile().getName());
                quote.setCustomerNum(Integer.parseInt(file.getParentFile().getName().substring(15,19)));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                out.debug("parse fail");
                quote.setCustomerNum(lines[findNextValue(lines, findSpecificThing(lines, "CUSTOMER NUMBER"), false)]);
            }
            quote.setDate(lines[findNextDateValue(lines, findSpecificThing(lines, "SHIP DATE"))]);
            quote.setVendor(lines[1]);
            out.debug("  Quote Num - " + quote.getID());
            out.debug("CustomerNum - " + quote.getCustomerNum());
            out.debug("  Ship Date - " + quote.getDateString());
            out.debug("     Vendor - " + quote.getVendor());
            int index = 0;
            //int pages = Integer.parseInt(lines[26].substring(lines[26].length()-1));
            int[] p = instancesOf(lines, "Quotation");
            int[] pageBounds = new int[p.length];
            if (p.length > 0) {
                for (int i=1; i<pageBounds.length; i++) {
                    pageBounds[i-1] = p[i];
                }
                pageBounds[pageBounds.length-1] = lineSize;
            } else {
                p = instancesOf(lines, "Acknowledgement");
                pageBounds = new int[p.length];
                for (int i=1; i<pageBounds.length; i++) {
                    pageBounds[i-1] = p[i];
                }
                pageBounds[pageBounds.length-1] = lineSize;
            }
            //do this for every page
            for (int pb=0; pb<p.length; pb++){
                index = p[pb];
                while(!(lines[index].equals("DESCRIPTION") || lines[index].equals("EXT PRICE")) && index<pageBounds[pb]) {
                    index++;
                }
                index += 2;
                if(lines[index].equals("UNIT PRICE")){
                    index += 2;
                }
                if(lines[index].equals("EXT PRICE")){
                    index += 2;
                }
                if(lines[index].equals("SHIPPING INSTRUCTIONS")){
                    index += 4;
                }
                ArrayList<Order> orderlist = new ArrayList<Order>();
                //keep track of the orders when we get to them
                int ordercount = -1;
                //we need another two to keep track of the orderlist stuff
                int[] ordertrack = {0,0};
                while(!lines[index].isBlank() && index<pageBounds[pb]){
                    int sep = lines[index].indexOf(" ");
                    if(sep != -1 && lines[index].substring(0, sep).matches("\\d+[a-zA-Z]{1,3}") && !lines[index].toLowerCase().matches("\\d+ft reel")){
                        ordercount++;
                        orderlist.add(new Order(false));
                        orderlist.get(ordercount).setDesc(lines[index].substring(sep+1));
                        orderlist.get(ordercount).setQuantity(lines[index].substring(0, sep));
                    } else {
                        orderlist.get(ordercount).appendDesc(" | " + lines[index]);
                    }
                    index++;
                }
                //skip a part that we don't need but is always the same
                //nvm it is not always the same
                while(!lines[index].matches("\\d+\\.\\d+(\\/[a-zA-Z]+)?") && index<pageBounds[pb]) {
                    index++;
                }
                //if (lines[index-2].equals("EXT PRICE")) {
                //true = unit price, false = ext price
                boolean toggle = true;
                //true = alt format (rare), false = main format
                boolean alt = !lines[index-2].equals("EXT PRICE");
                while(index<pageBounds[pb]) {
                    if(lines[index].isBlank()) {
                        toggle = !toggle;
                        if (alt && !toggle) {
                            index += 4;
                        } else if (alt) {
                            index++;
                            break;
                        }
                    } else if (lines[index].matches("\\d+\\.\\d+(\\/[a-zA-Z]+)?")) {
                        if (toggle) {
                            orderlist.get(ordertrack[0]).setRate(lines[index]);
                            ordertrack[0]++;
                        } else {
                            orderlist.get(ordertrack[1]).setAmount(lines[index]);
                            ordertrack[1]++;
                        }
                    } else {
                        break;
                    }
                    index++;
                }
                /*} else {
                    while (!(lines[index].matches("\\d+\\.\\d+\\/[a-zA-Z]+") || lines[index].equals(""))) {
                        orderlist.get(ordertrack).setRate(lines[index]);
                        ordertrack[0]++;
                        index++;
                    }
                    index = findThing(lines, findSpecificThing(lines, "EXT PRICE", index)+1);
                    ordertrack = 0;
                    while (!(lines[index].matches("\\d+\\.\\d+") || lines[index].equals(""))) {
                        orderlist.get(ordertrack).setAmount(lines[index]);
                        ordertrack[1]++;
                        index++;
                    }
                }*/
                quote.addOrders(orderlist);
                while(lines[index].equals("Subtotal") && index<pageBounds[pb]) {
                    index += 2;
                }
                for(Order order : quote.getOrders()) {
                    out.debug("Description - " + order.getDesc());
                    out.debug("   Quantity - " + order.getQuantity() + order.getQtyUnit());
                    out.debug(" Unit Price - " + order.getRate() + "/" + order.getRateUnit());
                    out.debug("  Ext Price - " + order.getAmount());
                }
                /*
                if(quote.getLastOrder().getDesc().isBlank()){
                    quote.removeOrder(quote.getOrders().size()-1);
                    out.debug("Removing that last one");
                }
                */
            }
            quote.setSubtotal(lines[index]);
            quote.setSNH(lines[index=findThing(lines, findSpecificThing(lines, "S&H Charges", index)+1)]);
            quote.setTotal(lines[index=findThing(lines, findSpecificThing(lines, "Amount Due", index)+1)]);
            quote.findSetTax();
            out.debug("   Subtotal - " + quote.getSubtotal());
            out.debug("        S&H - " + quote.getSNH());
            out.debug("        Tax - " + quote.getTax());
            out.debug("      Total - " + quote.getTotal());
            break;
        }
        out.println();
        //return quote.isValid(out);
        return quote;
    }
}
