import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class App {
    static File[] pos = (new File("src\\inputs\\POs")).listFiles();
    static File[] quotes = (new File("src\\inputs\\Quotes")).listFiles();
    static ArrayList<PO> poList = new ArrayList<PO>();
    static ArrayList<Quote> quoteList = new ArrayList<Quote>();
    
    public static boolean match(String regex, String match) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(match);
        return m.matches();
    }
    public static String[] arraylistToArray(ArrayList<String> strings) {
        String[] strs = new String[strings.size()];
        for (int i=0; i<strings.size(); i++) {
            strs[i] = strings.get(i);
        }
        return strs;
    }
    //PO ID, date, vendor, orders(desc, quantity, rate, job, amount), total, tracker
    //reminder: comment how everything works later.

    public static void main(String[] args) throws Exception {
        Out out = new Out("src\\outputs\\output.txt");
        PrintWriter outPOs = new PrintWriter("src\\outputs\\POs.csv");
        PrintWriter outQuotes = new PrintWriter("src\\outputs\\Quotes.csv");
        outPOs.println("ID,Date,Vendor,Description,Quantity,Rate,Job,Amount,Total,Memo,Payment Terms,Tracker");
        File temp = new File("src\\temp.txt");
        temp.deleteOnExit();
        ArrayList<String> matchList = new ArrayList<String>();
        Scanner scan = new Scanner(new File("src\\inputs\\matches.txt"));
        while (scan.hasNextLine()) {
            matchList.add(scan.nextLine());
        }
        scan.close();
        int numFNF = 0;
        Tabula t = new Tabula();
        //iterate through the POs
        out.println("POs\n");
        for (File poPDF : pos) {
            out.println("Current file: " + poPDF.getName());
            out.println("Filepath: " + poPDF.getPath());
            //out.println("Matches \"" + matchList.get(0) + "\"?");
            if (match("PO_(\\d{4})_from_Radiance_Solar_LLC_(\\d{5,6})\\.pdf", poPDF.getName())) {
                out.println("Matches!\n");
                poList.add(t.readTablesPO(poPDF, out));
            } else {
                out.println("File didn't match.\n");
                numFNF++;
            }
        }
        //iterate through the Quotes
        out.println("Quotes\n");
        for (File quotePDF : quotes) {
            out.println("Current file: " + quotePDF.getName());
            out.println("Filepath: " + quotePDF.getPath() + "\n");
            if (match("Graybar Quotation_(\\d{10})__(\\d{14})\\.pdf", quotePDF.getName())) {
                quoteList.add(t.readTablesQuote(quotePDF, out, 4));
            } else if (match("S(\\d{9})-(\\d{4})_(\\d{5})\\.pdf", quotePDF.getName())) {
                quoteList.add(t.readTablesQuote(quotePDF, out, 1));
            } else if (match("Quotation REG_(\\d{5})\\.pdf", quotePDF.getName())) {
                quoteList.add(t.readTablesQuote(quotePDF, out, 2));
            } else if (match("S(\\d{7})-(\\d{4})\\.pdf", quotePDF.getName())) {
                quoteList.add(t.readTablesQuote(quotePDF, out, 3));
            } else {
                out.println("File didn't match.");
            }
        }
        /* why do I have this section:
        PO[] everything = new PO[poList.size()];
        for (int i=0; i<poList.size(); i++) {
            everything[i] = poList.get(i);
        }
        */
        for (PO p : poList) {
            outPOs.println(p.toCSV());
        }
        /*
        for (Quote q : quoteList) {
            outQuotes.println(q.toCSV());
        }*/
        outPOs.close();
        outQuotes.close();
        
        out.println("Total FileNotFound's: " + numFNF);
        out.println("Total Files: " + pos.length);
        out.println("" + (1.0-(double)numFNF/pos.length)*100);
        out.close();
    }
}