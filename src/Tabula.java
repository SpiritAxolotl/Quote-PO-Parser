//code stolen from https://github.com/tabulapdf/tabula-java and HEAVILY modified by Davey

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class Tabula extends Base {
    private Out out;
    private PO po;
    private String[] lines;
    public Tabula(Out out) {
        this.out = out;
    }
    public void clear() {
        lines = null;
        po = null;
    }
    public void tabulaRead(File file) throws IOException {
        //written by not me
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file.getPath().substring(file.getPath().indexOf("\\")+1));
        ArrayList<String> lines = new ArrayList<String>();
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
                            String text = content.getText().replaceAll("\r", " ").strip();
                            lines.add(text);
                            //pw.println(text);
                            out.println(i + ": " + text);
                            //poList.add(new PO());
                            i++;
                        }
                        //out.println();
                    }
                }
            }
            oe.close();
            this.lines = stringArrayListToArray(lines);
            //pw.close();
        } catch (NullPointerException ex) {
            out.println("NullPointerException error. Ignoring...");
        }
    }
    public PO readTables(File file, Out out) throws Exception {
        if (file.exists()) {
            out.println("File exists!");
            //out.println("Debug: " + file.getPath().substring(file.getPath().indexOf("\\")+1));
        } else {
            out.println("File doesn't exist :(");
        }
        tabulaRead(file);
        //all of this is davey's code:
        //Putting the inputs into a table
        int lineSize = lines.length;
        //Parsing the stuff in the java table
        //Quote quote = new Quote();
        po = new PO();
        po.setID(lines[5]);
        po.setQuoteNum(file.getParentFile().listFiles().length-1);
        po.setDate(lines[4]);
        po.setVendor(lines[7]);
        po.setPayTerms(lines[3]);
        out.debug("         ID - " + po.getID());
        out.debug("       Date - " + po.getDateString());
        out.debug("     Vendor - " + po.getVendor());
        out.debug("  Pay Terms - " + po.getPayTerms());
        out.debug("    Quotes? - " + po.getQuoteNum());
        po.setMemo(lines[po.findSetTotal(lines)-1]);
        if (lineSize <= 40) {
            Order order = new Order(true);
            order.setDesc(lines[28]);
            order.setQuantity(lines[29]);
            order.setRate(lines[30]);
            order.setJob(lines[31]);
            order.setAmount(lines[33]);
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
            out.debug("       Memo - " + po.getMemo());
            out.debug("      Total - $" + po.getTotal());
        } else if (lineSize <= 88 && po.getID() != 7144) {
            for (int i=findThing(lines, findSpecificThing(lines, "Amount")+1); i<=lineSize-12; i+=6) {
                Order order = new Order(true);
                order.setDesc(lines[i]);
                order.setQuantity(lines[i+1]);
                order.setRate(lines[i+2]);
                order.setJob(lines[i+3]);
                order.setAmount(lines[i+5]);
                out.debug("Description - " + order.getDesc());
                out.debug("   Quantity - " + order.getQuantity());
                out.debug("       Rate - " + order.getRate());
                out.debug("        Job - " + order.getJob());
                out.debug("     Amount - " + order.getAmount());
                if(!order.getDesc().isBlank()){
                    po.addOrder(order.isValid(out));
                } else {
                    out.debug("Removing that last one...");
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
            /*int test = 0;
            System.out.println(test + " | 0 expected");
            System.out.println((test++) + " | 0 expected");
            System.out.println((test+=2) + " | 1 expected");
            System.out.println((test) + " | 3 expected");
            test+=2;*/
            int index = findThing(lines, new int[] {11,1,6});
            boolean toggle = true;
            for (int i=index; i<=lineSize-24; i=findThing(lines, i)) {
                Order order = new Order(true);
                if (toggle) {
                    order.setDesc(lines[i++]);
                    order.setQuantity(lines[i++]);
                    order.setRate(lines[i++]);
                    order.setJob(lines[i++]);
                    i++;
                    order.setAmount(lines[i++]);
                } else {
                    order.setDesc(lines[i]);
                    order.setQuantity(lines[i+=2]);
                    order.setRate(lines[i+=2]);
                    order.setJob(lines[i+=2]);
                    order.setAmount(lines[i+=4]);
                    i++;
                }
                toggle = !toggle;
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
