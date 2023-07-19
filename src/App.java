import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class App {
    static File[] dir = (new File("src\\inputs\\extracted\\")).listFiles();
    //static File[] pos = (new File("src\\inputs\\POs")).listFiles();
    //static File[] quotes = (new File("src\\inputs\\Quotes")).listFiles();
    static ArrayList<PO> poList = new ArrayList<PO>();
    static ArrayList<Quote> quoteList = new ArrayList<Quote>();
    
    public static boolean match(String regex, String match) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(match);
        return m.matches();
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
        /*
        String match1 = "PO_(\\d{4})_from_Radiance_Solar_LLC_(\\d{5,6})\\.pdf";
        String match2 = matchList.get(0);
        out.debug("match1 == match2? " + match1.equals(match2));
        */
        //int numFNF = 0;
        Tabula t = new Tabula();
        //iterate through the POs
        //out.println("POs\n");
        for (File folder : dir) {for (File aPDF : folder.listFiles()) {
            out.println("Current file: " + aPDF.getName());
            out.println("Filepath: " + aPDF.getPath());
            //out.println("Matches \"" + matchList.get(0) + "\"?");
            if (match(matchList.get(0), aPDF.getName())) {
                out.println("Matches!\n");
                poList.add(t.readTablesPO(aPDF, out));
            } else if (match(matchList.get(1), aPDF.getName())) {
                quoteList.add(t.readTablesQuote(aPDF, out, 1));
            } else if (match(matchList.get(2), aPDF.getName())) {
                quoteList.add(t.readTablesQuote(aPDF, out, 2));
            } else if (match(matchList.get(3), aPDF.getName())) {
                quoteList.add(t.readTablesQuote(aPDF, out, 3));
            } else if (match(matchList.get(4), aPDF.getName())) {
                quoteList.add(t.readTablesQuote(aPDF, out, 4));
            } else {
                out.println("File didn't match.\n");
                //numFNF++;
            }
        }}
        //iterate through the Quotes
        //out.println("Quotes\n");
        /*
        for (File quotePDF : quotes) {
            out.println("Current file: " + quotePDF.getName());
            out.println("Filepath: " + quotePDF.getPath() + "\n");
                   if (match(matchList.get(1), quotePDF.getName())) {
                quoteList.add(t.readTablesQuote(quotePDF, out, 1));
            } else if (match(matchList.get(2), quotePDF.getName())) {
                quoteList.add(t.readTablesQuote(quotePDF, out, 2));
            } else if (match(matchList.get(3), quotePDF.getName())) {
                quoteList.add(t.readTablesQuote(quotePDF, out, 3));
            } else if (match(matchList.get(4), quotePDF.getName())) {
                quoteList.add(t.readTablesQuote(quotePDF, out, 4));
            } else {
                out.println("File didn't match.");
            }
        }
        */
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
        
        //out.println("Total Files Not Found: " + numFNF);
        //out.println("Total Files: " + pos.length);
        //out.println((1.0-(double)numFNF/pos.length)*100 + "% of files were found");
        out.close();
    }
}