//code stolen from https://github.com/tabulapdf/tabula-java and modified by Davey

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
    private PO po;
    private Quote quote;
    public PO readTablesPO(File file, Out out) throws Exception {
        this.readTables(file, out, 0);
        return this.po;
    }
    public Quote readTablesQuote(File file, Out out, int type) throws Exception {
        this.readTables(file, out, type);
        return this.quote;
    }
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
    private void readTables(File file, Out out, int type) throws Exception {
        if (file.exists()) {
            out.println("File exists!");
        } else {
            out.println("File doesn't exist :(");
        }
        //out.debug("inputs\\extracted\\" + file.getParent() + file.getName());
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file.getPath().substring(4));
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
                    @SuppressWarnings("rawtypes") List<List<RectangularTextContainer>> rows = table.getRows();
                    // iterate over the rows of the table
                    for (@SuppressWarnings("rawtypes") List<RectangularTextContainer> cells : rows) {
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
        switch (type) {
        case 0:
            PO po = new PO();
            po.setID(lines.get(5));
            po.setDate(lines.get(4));
            po.setVendor(lines.get(7));
            po.setPayTerms(lines.get(3));
            out.debug("ID - " + po.getID());
            out.debug("Date - " + po.getDateString());
            out.debug("Vendor - " + po.getVendor());
            out.debug("Payment Terms - " + po.getPayTerms());
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
                out.debug("Quantity - " + order.getQuantity());
                out.debug("Rate - " + order.getRate());
                out.debug("Job - " + order.getJob());
                out.debug("Amount - " + order.getAmount());
                if(po.getLastOrder().getDesc().isBlank()){
                    po.removeOrder(po.getOrders().size()-1);
                }
            } else if (lineSize <= 51) {
                //findThing(lines, 11);
                for (int i=findThing(lines,findThing(lines,findThing(lines,11)+1)+6); i<=lineSize-12; i+=6) {
                    Order order = new Order(true);
                    order.setDesc(lines.get(i));
                    order.setQuantity(lines.get(i+1));
                    order.setRate(lines.get(i+2));
                    order.setJob(lines.get(i+3));
                    order.setAmount(lines.get(i+5));
                    out.debug("Description - " + order.getDesc());
                    out.debug("Quantity - " + order.getQuantity());
                    out.debug("Rate - " + order.getRate());
                    out.debug("Job - " + order.getJob());
                    out.debug("Amount - " + order.getAmount());
                    if(!order.getDesc().isBlank()){
                        po.addOrder(order.isValid(out));
                    }
                }
                out.debug("Memo - " + po.getMemo());
                out.debug("Total - $" + po.getTotal());
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
                    out.debug("Quantity - " + order.getQuantity());
                    out.debug("Rate - " + order.getRate());
                    out.debug("Job - " + order.getJob());
                    out.debug("Amount - " + order.getAmount());
                    if(!order.getDesc().isBlank()){
                        po.addOrder(order.isValid(out));
                    }
                }
                out.debug("Memo - " + po.getMemo());
                out.debug("Total - $" + po.getTotal());
            }
            this.po = po.isValid(out);
            break;
        //uncomment and modify when I start working on quotes
        /*case 1:
            quote.setID(lines.get(14));
            quote.setDate(lines.get(2));
            quote.setVendor(lines.get(4));
            quote.setRef(lines.get(3));
            out.debug("ID - " + quote.getID());
            out.debug("Date - " + quote.getDateString());
            out.debug("Vendor - " + quote.getVendor());
            out.debug("Ref - " + quote.getRef());
            quote.setRef(lines.get(8));
            for (int i=28; i<=lineSize-12; i+=6) {
                Order order = new Order(true);
                order.setDesc(lines.get(i));
                order.setQuantity(lines.get(i+1));
                order.setRate(lines.get(i+2));
                order.setJob(lines.get(i+3));
                order.setAmount(lines.get(i+5));
                quote.addOrder(order);
                out.debug("Description - " + order.getDesc());
                out.debug("Quantity - " + order.getQuantity());
                out.debug("Rate - " + order.getRate());
                out.debug("Job - " + order.getJob());
                out.debug("Amount - " + order.getAmount());
            }
            out.debug("Total - $" + quote.getTotal());
            if(quote.getLastOrder().getDesc().isBlank()){
                quote.removeOrder(quote.getOrders().size()-1);
                }*/
        }
        out.println();
    }
}
