import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class WSL extends Base {
    private String[] lines;
    private Quote quote;
    private int index;
    private Out out;
    private int type;
    private File file;
    public WSL(Out out) {
        this.out = out;
    }
    public void clear() {
        lines = null;
        quote = null;
        file = null;
        index = 0;
        type = 0;
    }
    private String[] arrayAndPrintStuff(String filepath, Out out) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(filepath));
        ArrayList<String> lines = new ArrayList<String>();
        int linebreaks = 0;
        for (int k=0; scan.hasNextLine(); k++) {
            String line = removeWeirdChars(scan.nextLine().strip());
            //weird character remover
            if (type == 1 && line.length() == 1) {
                for (String j : new String[] {"<", ",", "$"}) {
                    if (line.equals(j)) {
                        line = "";
                        break;
                    }
                }
                if (linebreaks >= 2 && line.equals("4")) {
                    line = "";
                }
            } else if (type == 1 && line.equals("4 ,4")) {
                line = "";
            }
            //gets rid of long chains of empty lines!!
            if (type == 1 && line.isBlank()) {
                if (linebreaks >= 2) {
                    k--;
                    linebreaks++;
                    continue;
                } else {
                    linebreaks++;
                }
            } else {
                linebreaks = 0;
            }
            lines.add(line);
            out.println(k + ": " + line);
        }
        scan.close();
        new File("src\\temp.txt").delete();
        return stringArrayListToArray(lines);
    }
    private void parse() {
        int[] p;
        int[] pageBounds;
        if (type == 0) {
            quote.setID(lines[findNextValue(lines, findTwoSpecificThing(lines, "QUOTE NUMBER", "ORDER NUMBER"), true)]);
            try {
                out.debug(file.getParentFile().getName());
                quote.setCustomerNum(Integer.parseInt(file.getParentFile().getName().substring(15,19)));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                out.debug("PO number parse fail");
                quote.setCustomerNum(lines[findNextValue(lines, findSpecificThing(lines, "CUSTOMER NUMBER"), false)]);
            }
            quote.setDate(lines[findNextDateValue(lines, findSpecificThing(lines, "SHIP DATE"))]);
            quote.setVendor(lines[1]);
            out.debug("  Quote Num - " + quote.getID());
            out.debug("CustomerNum - " + quote.getCustomerNum());
            out.debug("  Ship Date - " + quote.getDateString());
            out.debug("     Vendor - " + quote.getVendor());
            //index = 0;
            //int pages = Integer.parseInt(lines[26].substring(lines[26].length()-1));
            p = instancesOf(lines, lines[0]);
            pageBounds = new int[p.length];
            for (int i=1; i<pageBounds.length; i++) {
                pageBounds[i-1] = p[i];
            }
            pageBounds[pageBounds.length-1] = lines.length;
            //do this for every page
            for (int pb=0; pb<p.length; pb++){
                index = p[pb];
                ArrayList<Order> orderlist = new ArrayList<Order>();
                //keep track of the orders when we get to them
                int[] ordertrack = {-1,0,0};
                
                while (index<pageBounds[pb]) {
                    if (lines[index].contains(" ") && lines[index].split(" ")[0].matches("\\d+[a-zA-Z]{1,3}")) {
                        break;
                    }
                    index++;
                }
                while(!lines[index].isBlank() && index<pageBounds[pb]){
                    int sep = lines[index].indexOf(" ");
                    if (lines[index].matches("\\*+")) {
                        index++;
                        while(!lines[index].matches("\\*+")) {
                            index++;
                        }
                    } else if (lines[index].indexOf("*")==1 && lines[index].lastIndexOf("*")==lines[index].length()-1) {
                        index++;
                    } else if (sep != -1 && lines[index].substring(0, sep).matches("\\d+[a-zA-Z]{1,3}") && !lines[index].toLowerCase().matches("\\d+ft reel")){
                        ordertrack[0]++;
                        orderlist.add(new Order(false));
                        orderlist.get(ordertrack[0]).setDesc(lines[index].substring(sep+1));
                        orderlist.get(ordertrack[0]).setQuantity(lines[index].substring(0, sep));
                    } else {
                        orderlist.get(ordertrack[0]).appendDesc(lines[index]);
                    }
                    index++;
                }
                //skip a part that we don't need but is always the same
                //nvm it is not always the same
                /*while(!lines[index].matches("\\d+\\.\\d+(\\/[a-zA-Z]+)?") && index<pageBounds[pb]) {
                    index++;
                }*/
                int oldindex = index;
                while(!lines[index].matches("UNIT PRICE") && index<pageBounds[pb]) {
                    index++;
                }
                if (index >= pageBounds[pb]) {
                    index = oldindex;
                    while(!lines[index].contains("Printed By: ")) {
                        index++;
                    }
                }
                index += 2;
                
                //if (lines[index-2].equals("EXT PRICE")) {
                //true = unit price, false = ext price
                boolean toggle = true;
                //false = alt format (rare), true = main format
                //change this in the future:
                boolean alt = false;
                if (lines[index].equals("EXT PRICE")) {
                    alt = true;
                    index += 2;
                } else if (lines[index-2].contains("Printed By: ")) {
                    alt = true;
                }
                int snhline = findSpecificThing(lines, "S&H Charges", index)-2;
                while(index<pageBounds[pb] && index<snhline) {
                    if (alt) {
                        if (lines[index].isBlank()) {
                            toggle = !toggle;
                        } else if (lines[index].matches("\\d+\\.\\d+(\\/[a-zA-Z]+)?")) {
                            if (toggle) {
                                orderlist.get(ordertrack[1]).setRate(lines[index]);
                                ordertrack[1]++;
                            } else {
                                orderlist.get(ordertrack[2]).setAmount(lines[index]);
                                ordertrack[2]++;
                            }
                        } else {
                            break;
                        }
                        index++;
                    } else {
                        if (lines[index].equals("Subtotal") || lines[index].equals("EXT PRICE")) {
                            toggle = false;
                            index += 2;
                            if (lines[index].equals("EXT PRICE")) {
                                index += 2;
                            }
                            continue;
                        } else if (!lines[index].isBlank()) {
                            if (toggle) {
                                orderlist.get(ordertrack[1]).setRate(lines[index]);
                                ordertrack[1]++;
                            } else {
                                orderlist.get(ordertrack[2]).setAmount(lines[index]);
                                ordertrack[2]++;
                            }
                        }
                        index++;
                    }
                }
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
            }
            quote.setSubtotal(lines[index]);
            quote.setSNH(lines[index=findThing(lines, findSpecificThing(lines, "S&H Charges", index)+1)]);
            quote.setTotal(lines[index=findThing(lines, findSpecificThing(lines, "Amount Due", index)+1)]);
            quote.findSetTax();
            out.debug("   Subtotal - " + quote.getSubtotal());
            out.debug("        S&H - " + quote.getSNH());
            out.debug("        Tax - " + quote.getTax());
            out.debug("      Total - " + quote.getTotal());
        } else if (type == 1) {
            quote.setID(lines[findNextValue(lines, findTwoSpecificThing(lines, "QUOTE NUMBER", "ORDER NUMBER"), true)]);
            try {
                out.debug(file.getParentFile().getName());
                quote.setCustomerNum(Integer.parseInt(file.getParentFile().getName().substring(15,19)));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                out.debug("PO number parse fail");
                quote.setCustomerNum(lines[findNextValue(lines, findSpecificThing(lines, "CUSTOMER NUMBER"), false)]);
            }
            quote.setDate(lines[findNextDateValue(lines, findSpecificThing(lines, "SHIP DATE"))]);
            quote.setVendor(lines[1]);
            out.debug("  Quote Num - " + quote.getID());
            out.debug("CustomerNum - " + quote.getCustomerNum());
            out.debug("  Ship Date - " + quote.getDateString());
            out.debug("     Vendor - " + quote.getVendor());
            p = instancesOf(lines, lines[0]);
            pageBounds = new int[p.length];
            for (int i=1; i<pageBounds.length; i++) {
                pageBounds[i-1] = p[i];
            }
            pageBounds[pageBounds.length-1] = lines.length;
            boolean[] toggles = {false, false, false, true};
            //keep track of the orders when we get to them
            int[] ordertrack = {0,0,0,0};
            int ordersync = 0;
            ArrayList<Order> orderlist = new ArrayList<Order>();
            //do this for every page
            for (int pb=0; pb<p.length; pb++){
                index = p[pb];
                
                index = findSpecificThing(lines, "ORDER QTY", index);
                
                while (!lines[index].matches("\\d+[a-zA-Z]{1,3}") && index<pageBounds[pb]) {
                    index++;
                }
                
                while (lines[index].matches("\\d+[a-zA-Z]{1,3}") || lines[index].isBlank()) {
                    if (!lines[index].isBlank()) {
                        orderlist.add(new Order(false));
                        orderlist.get(ordertrack[0]).setQuantity(lines[index]);
                        ordertrack[0]++;
                    }
                    index++;
                }
                
                
                //index = findSpecificThing(lines, "ORDERED BY", index);
                int oldindex = index;
                int orderbound = lines.length;
                if (pb >= 1) {
                    index = findSpecificThing(lines, "ORDERED BY", index);
                    orderbound = index;
                } else {
                    while (index<pageBounds[pb]) {
                        if (lines[index].equals("WORLD ELECTRIC SUPPLY, INC.")) {
                            index += 2;
                        }
                        if (lines[index].contains(" ") && lines[index].split(" ")[0].matches("\\d+") && !lines[index].matches("\\d+ of \\d+")) {
                            break;
                        }
                        index++;
                    }
                }
                while (index<pageBounds[pb]) {
                    if (lines[index].equals("WORLD ELECTRIC SUPPLY, INC.")) {
                        index += 2;
                    }
                    if (lines[index].contains(" ") && lines[index].split(" ")[0].matches("\\d+") && !lines[index].matches("\\d+ of \\d+")) {
                        break;
                    }
                    index++;
                }
                new Object(); //breakpoint
                //I misunderstood how the format actually was but the code works as intended(?) so I'm not fixing it lmao
                //update: no. the code does Not work as intended and I should refactor
                while (index<pageBounds[pb] && !lines[index].toLowerCase().contains("continued on next page") && !lines[index].matches("S\\d{8,}")) {
                    /*
                    if (lines[index].contains("ORDERED BY")) {
                        index++;
                        while (index<pageBounds[pb]) {
                            if (lines[index].contains(" ") && lines[index].split(" ")[0].matches("\\d+") && !lines[index].matches("\\d+ of \\d+")) {
                                break;
                            }
                            index++;
                        }
                        continue;
                    }
                    */
                    if (!toggles[1] && !toggles[0] && lines[index].contains(" ") && lines[index].split(" ")[0].matches("\\d+")) {
                        toggles[1] = true;
                        orderlist.get(ordertrack[1]).setDesc(lines[index].substring(lines[index].indexOf(" ")+1));
                    } else if (toggles[1] && !lines[index].isBlank()) {
                        if (lines[index].matches("\\d+ .+") && pb >= 1) {
                                toggles[1] = false;
                                ordertrack[1]++;
                                index--;
                            } else {
                                orderlist.get(ordertrack[1]).appendDesc(lines[index]);
                            }
                    } else if (toggles[1]) {
                        ordertrack[1]++;
                        toggles[1] = false;
                        toggles[2] = true;
                    } else if (toggles[2]) {
                        if (lines[index].matches("\\d+")) {
                            toggles[2] = false;
                        }
                    } else if (toggles[0]) {
                        if (lines[index].matches("\\d+")) {
                            toggles[0] = false;
                        }
                    }
                    index++;
                }
                if (pb >= 1) {
                    while(!lines[index].equals("FREIGHT ALLOWED") && index<pageBounds[pb]) {
                        index++;
                    }
                    index += 2;
                    while(!lines[index].isBlank()) {
                        orderlist.get(ordersync).appendDesc(lines[index]);
                        index++;
                    }
                    
                    index = oldindex;
                    while (!lines[index].matches("\\d+ .+")) {
                        index++;
                    }
                    
                    while (index<pageBounds[pb] && !lines[index].toLowerCase().contains("continued on next page") && index < orderbound) {
                        /*
                        if (lines[index].contains("ORDERED BY")) {
                            index++;
                            while (index<pageBounds[pb]) {
                                if (lines[index].contains(" ") && lines[index].split(" ")[0].matches("\\d+") && !lines[index].matches("\\d+ of \\d+")) {
                                    break;
                                }
                                index++;
                            }
                            continue;
                        }
                        */
                        if (!toggles[1] && !toggles[0] && lines[index].contains(" ") && lines[index].split(" ")[0].matches("\\d+")) {
                            toggles[1] = true;
                            orderlist.get(ordertrack[1]).setDesc(lines[index].substring(lines[index].indexOf(" ")+1));
                        } else if (toggles[1] && !lines[index].isBlank()) {
                            orderlist.get(ordertrack[1]).appendDesc(lines[index]);
                        } else if (toggles[1]) {
                            ordertrack[1]++;
                            toggles[1] = false;
                            toggles[2] = true;
                        } else if (toggles[2]) {
                            if (lines[index].matches("\\d+")) {
                                toggles[2] = false;
                            }
                        } else if (toggles[0]) {
                            if (lines[index].matches("\\d+")) {
                                toggles[0] = false;
                            }
                        }
                        index++;
                    }
                }
                ordersync = ordertrack[1]-1;
                
                index = findSpecificThing(lines, "EXT PRICE", index);
                
                while (!lines[index].matches("\\d+\\.\\d+( \\w+)?")) {
                    index++;
                }
                
                while (!lines[index].equals("Subtotal") && index<pageBounds[pb]) {
                    if (lines[index].isBlank()) {
                            toggles[3] = !toggles[3];
                        } else if (lines[index].matches("\\d+\\.\\d+( \\w+)?")) {
                            if (toggles[3]) {
                                orderlist.get(ordertrack[2]).setRate(lines[index]);
                                ordertrack[2]++;
                            } else {
                                orderlist.get(ordertrack[3]).setAmount(lines[index]);
                                ordertrack[3]++;
                            }
                        } else {
                            break;
                        }
                    index++;
                }
                toggles[3] = true;
            }
            quote.addOrders(orderlist);
            for(Order order : quote.getOrders()) {
                out.debug("Description - " + order.getDesc());
                out.debug("   Quantity - " + order.getQuantity() + order.getQtyUnit());
                out.debug(" Unit Price - " + order.getRate() + "/" + order.getRateUnit());
                out.debug("  Ext Price - " + order.getAmount());
            }
            new Object();
            //quote.setSubtotal(lines[index]);
            //quote.setSNH(lines[index=findThing(lines, findSpecificThing(lines, "S&H Charges", index)+1)]);
            //quote.setTotal(lines[index=findThing(lines, findSpecificThing(lines, "Amount Due", index)+1)]);
            //quote.findSetTax();
            //out.debug("   Subtotal - " + quote.getSubtotal());
            //out.debug("        S&H - " + quote.getSNH());
            //out.debug("        Tax - " + quote.getTax());
            //out.debug("      Total - " + quote.getTotal());
        } else if (type == 2) {
            quote.setVendor("Graybar");
        }
    }
    private void wslCommand(File file) {
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
            for (String line; (line = reader.readLine()) != null;) {
                out.println(line);
            }
            
            // Wait for the process to complete
            int exitCode = process.waitFor();
            out.println("Exit Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Quote readTables(File filee, int type) throws Exception {
        this.type = type;
        this.file = filee;
        wslCommand(file);
        lines = arrayAndPrintStuff("src\\temp.txt", out);
        //int lineSize = lines.length;
        //Parsing the stuff in the java table
        quote = new Quote();
        parse();
        return quote.isValid(out, type);
        //return quote;
    }
}
