//code stolen from https://github.com/tabulapdf/tabula-java and HEAVILY modified by Davey

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

//Vendor name, quote date, vendor quote number, our reference, product code, product description, quantity, unit price, total price per line.
public class Tabula {
    public static int[] intArrayListToArray(ArrayList<Integer> ints) {
        int[] integers = new int[ints.size()];
        for (int i=0; i<ints.size(); i++) {
            integers[i] = ints.get(i);
        }
        return integers;
    }
    public static ArrayList<Integer> intArrayToArrayList(int[] ints) {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        for (int i : ints) {
            integers.add(i);
        }
        return integers;
    }
    public int findThing(ArrayList<String> strings, int index) {
        for(int i=index; i<strings.size(); i++) {
            if (!strings.get(i).isBlank()) {
                return i;
            }
        }
        return -1;
    }
    public int findThing(ArrayList<String> strings, int[] indexes) {
        int index = 0;
        for(int i=0; i<indexes.length; i++) {
            index = findThing(strings, index + indexes[i]);
        }
        return index;
    }
    public int findSpecificThing(ArrayList<String> strings, String target) {
        for(int i=0; i<strings.size(); i++) {
            if (strings.get(i).equals(target)) {
                return i;
            }
        }
        return -1;
    }
    public int findSpecificThing(ArrayList<String> strings, String target, int index) {
        for(int i=index; i<strings.size(); i++) {
            if (strings.get(i).equals(target)) {
                return i;
            }
        }
        return -1;
    }
    //mostly written by someone else:
    public PO readTables(File file, Out out) throws Exception {
        if (file.exists()) {
            out.println("File exists!");
            //out.println("Debug: " + file.getPath().substring(file.getPath().indexOf("\\")+1));
        } else {
            out.println("File doesn't exist :(");
        }
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file.getPath().substring(file.getPath().indexOf("\\")+1));
        PrintWriter pw = new PrintWriter("src\\temp.txt");
        //extract tables from document
        try (PDDocument document = PDDocument.load(in)) {
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            ObjectExtractor oe = new ObjectExtractor(document);
            PageIterator pi = oe.extract();
            while (pi.hasNext()) {
                // iterate over the pages of the document
                Page page = pi.next();
                List<Table> tables = sea.extract(page);
                // iterate over the tables of the page
                int i = 0;
                for (Table table : tables) {
                    // iterate over the rows of the table
                    for (@SuppressWarnings("rawtypes") List<RectangularTextContainer> cells : table.getRows()) {
                        // print all column-cells of the row plus linefeed
                        for (@SuppressWarnings("rawtypes") RectangularTextContainer content : cells) {
                            // Note: Cell.getText() uses \r to concat text chunks
                            String text = content.getText().replaceAll("\r", " ");
                            pw.println(text);
                            out.println(i + ": " + text);
                            //poList.add(new PO());
                            i++;
                        }
                        //out.println();
                    }
                }
            }
            oe.close();
            pw.close();
        } catch (NullPointerException ex) {
            out.println("NullPointerException error. Ignoring...");
        }
        //all of this is davey's code:
        //Putting the inputs into a table
        Scanner scan = new Scanner(new File("src\\temp.txt"));
        ArrayList<String> lines = new ArrayList<String>();
        while (scan.hasNextLine()) {
            lines.add(scan.nextLine().trim());
        }
        scan.close();
        int lineSize = lines.size();
        //Parsing the stuff in the java table
        //Quote quote = new Quote();
        PO po = new PO();
        po.setID(lines.get(5));
        po.setDate(lines.get(4));
        po.setVendor(lines.get(7));
        po.setPayTerms(lines.get(3));
        out.debug("         ID - " + po.getID());
        out.debug("       Date - " + po.getDateString());
        out.debug("     Vendor - " + po.getVendor());
        out.debug("  Pay Terms - " + po.getPayTerms());
        po.setMemo(lines.get(po.findSetTotal(lines)-1));
        if (lineSize <= 40) {
            Order order = new Order(true);
            order.setDesc(lines.get(28));
            order.setQuantity(lines.get(29));
            order.setRate(lines.get(30));
            order.setJob(lines.get(31));
            order.setAmount(lines.get(33));
            po.addOrder(order.isValid(out));
            out.debug("Description - " + order.getDesc());
            out.debug("   Quantity - " + order.getQuantity());
            out.debug("       Rate - " + order.getRate());
            out.debug("        Job - " + order.getJob());
            out.debug("     Amount - " + order.getAmount());
            if(po.getLastOrder().getDesc().isBlank()){
                po.removeOrder(po.getOrders().size()-1);
                out.debug("Removing that last one");
            }
        } else if (lineSize <= 51) {
            //findThing(lines, 11);
            for (int i=findThing(lines, new int[] {11,1,6})+1; i<=lineSize-12; i+=6) {
                Order order = new Order(true);
                order.setDesc(lines.get(i));
                order.setQuantity(lines.get(i+1));
                order.setRate(lines.get(i+2));
                order.setJob(lines.get(i+3));
                order.setAmount(lines.get(i+5));
                out.debug("Description - " + order.getDesc());
                out.debug("   Quantity - " + order.getQuantity());
                out.debug("       Rate - " + order.getRate());
                out.debug("        Job - " + order.getJob());
                out.debug("     Amount - " + order.getAmount());
                if(!order.getDesc().isBlank()){
                    po.addOrder(order.isValid(out));
                }
            }
            out.debug("       Memo - " + po.getMemo());
            out.debug("      Total - $" + po.getTotal());
            /*
            if(po.getLastOrder().getDesc().isBlank()){
                po.removeOrder(po.getOrders().size()-1);
            }
            */
        } else {
            int index = findThing(lines,findThing(lines,findThing(lines,11)+1)+6);
            Order ordertemp = new Order(true);
            ordertemp.setDesc(lines.get(index));
            ordertemp.setQuantity(lines.get(index+1));
            ordertemp.setRate(lines.get(index+2));
            ordertemp.setJob(lines.get(index+3));
            ordertemp.setAmount(lines.get(index+4));
            po.addOrder(ordertemp.isValid(out));
            for (int i=index+5; i<=lineSize-24; i+=12) {
                Order order = new Order(true);
                order.setDesc(lines.get(i));
                order.setQuantity(lines.get(i+2));
                order.setRate(lines.get(i+4));
                order.setJob(lines.get(i+6));
                order.setAmount(lines.get(i+8));
                out.debug("Description - " + order.getDesc());
                out.debug("   Quantity - " + order.getQuantity());
                out.debug("       Rate - " + order.getRate());
                out.debug("        Job - " + order.getJob());
                out.debug("     Amount - " + order.getAmount());
                if(!order.getDesc().isBlank()){
                    po.addOrder(order.isValid(out));
                }
            }
            out.debug("       Memo - " + po.getMemo());
            out.debug("      Total - $" + po.getTotal());
        }
        out.println();
        return po.isValid(out);
    }
}
