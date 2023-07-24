//import java.io.BufferedReader;
import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
//import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class App {
    static File[] dir = (new File("src\\inputs\\extracted\\")).listFiles();
    //static File[] pos = (new File("src\\inputs\\POs")).listFiles();
    //static File[] quotes = (new File("src\\inputs\\Quotes")).listFiles();
    static HashMap<Integer, PO> poMap = new HashMap<Integer, PO>();
    static HashMap<Integer, Quote> quoteMap = new HashMap<Integer, Quote>();
    static HashMap<Integer, Pair> pairs = new HashMap<Integer, Pair>();
    //static ArrayList<PO> poList = new ArrayList<PO>();
    //static ArrayList<Quote> quoteList = new ArrayList<Quote>();
    
    public static boolean match(String regex, String match) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(match);
        return m.matches();
    }
    public String[] findSuppliesPO(int id){
        PO po = poMap.get(id);
        Order[] temp = po.getOrdersArray();
        String[] descList = new String[temp.length];
        for (int i=0; i<temp.length; i++) {
            descList[i] = temp[i].getDesc();
        }
        return descList;
    }
    public String[] findSuppliesQuote(int id){
        Quote quote = quoteMap.get(id);
        Order[] temp = quote.getOrdersArray();
        String[] descList = new String[temp.length];
        for (int i=0; i<temp.length; i++) {
            descList[i] = temp[i].getDesc();
        }
        return descList;
    }
    //PO ID, date, vendor, orders(desc, quantity, rate, job, amount), total, tracker
    //reminder: comment how everything works later.

    public static void main(String[] args) throws Exception {
        Out out = new Out("src\\outputs\\output.txt");
        PrintWriter outCSV = new PrintWriter("src\\outputs\\POs.csv");
        outCSV.println("ID,Date,Vendor,Description,Quantity,Rate,Job,Amount,Total,Memo,Payment Terms,Tracker");
        File temp = new File("src\\temp.txt");
        temp.deleteOnExit();
        ArrayList<String> matchList = new ArrayList<String>();
        Scanner scan = new Scanner(new File("src\\inputs\\matches.txt"));
        while (scan.hasNextLine()) {
            matchList.add(scan.nextLine());
        }
        scan.close();
        
        Tabula t = new Tabula();
        WSL wsl = new WSL();
        int pairID = 0;
        //iterate through the files
        for (File folder : dir) {
            PO po = new PO();
            ArrayList<Quote> quotes = new ArrayList<Quote>(Arrays.asList(new Quote()));
            for (File aPDF : folder.listFiles()) {
                out.println("Current file: " + aPDF.getName());
                out.println("Filepath: " + aPDF.getPath());
                if (match(matchList.get(0), aPDF.getName())) {
                    out.println("Matches!\n");
                    po = t.readTables(aPDF, out);
                } else if (match(matchList.get(1), aPDF.getName())) {
                    quotes.add(wsl.readTables(aPDF, out, 0));
                } else if (match(matchList.get(2), aPDF.getName())) {
                    quotes.add(wsl.readTables(aPDF, out, 1));
                } else if (match(matchList.get(3), aPDF.getName())) {
                    quotes.add(wsl.readTables(aPDF, out, 2));
                } else if (match(matchList.get(4), aPDF.getName())) {
                    quotes.add(wsl.readTables(aPDF, out, 3));
                } else {
                    out.println("File didn't match.\n");
                }
            }
            try {
                pairs.put(pairID, new Pair(po.isValid(out), quotes));
                pairID++;
            } catch (NullPointerException e) {
                out.println("Error linking PO and Quote. Ignoring for now...");
            }
        }
        for (int id : pairs.keySet()) {
            outCSV.println(pairs.get(id).toCSV());
        }
        
        outCSV.close();
        //outQuotes.close();
        out.close();
    }
}
