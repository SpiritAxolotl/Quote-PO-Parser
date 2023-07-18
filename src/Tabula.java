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
        this.readTables(file, out, 0, "POs\\");
        return this.po;
    }
    public Quote readTablesQuote(File file, Out out, int type) throws Exception {
        this.readTables(file, out, type, "Quotes\\");
        return this.quote;
    }
    private void readTables(File file, Out out, int type, String suff) throws Exception {
        if (file.exists()) {
            out.println("File exists!");
        } else {
            out.println("File doesn't exist :(");
        }
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("inputs\\" + suff + file.getName());
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
                            String text = content.getText().replace("\r", " ");
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
                po.addOrder(order);
                out.debug("Description - " + order.getDesc());
                out.debug("Quantity - " + order.getQuantity());
                out.debug("Rate - " + order.getRate());
                out.debug("Job - " + order.getJob());
                out.debug("Amount - " + order.getAmount());
            } else {
                for (int i=28; i<=lineSize-12; i+=6) {
                    Order order = new Order(true);
                    order.setDesc(lines.get(i));
                    order.setQuantity(lines.get(i+1));
                    order.setRate(lines.get(i+2));
                    order.setJob(lines.get(i+3));
                    order.setAmount(lines.get(i+5));
                    po.addOrder(order);
                    out.debug("Description - " + order.getDesc());
                    out.debug("Quantity - " + order.getQuantity());
                    out.debug("Rate - " + order.getRate());
                    out.debug("Job - " + order.getJob());
                    out.debug("Amount - " + order.getAmount());
                }
                out.debug("Memo - " + po.getMemo());
                out.debug("Total - $" + po.getTotal());
                if(po.getLastOrder().getDesc().isBlank()){
                    po.removeOrder(po.getOrders().size()-1);
                }
            }
            this.po = po.isValid();
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
