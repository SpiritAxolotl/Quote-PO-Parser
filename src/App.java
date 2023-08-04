import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;


public class App extends Base {
    static File[] dir = (new File("src\\inputs\\extracted\\")).listFiles();
    static HashMap<Integer, PO> poMap = new HashMap<Integer, PO>();
    static HashMap<Integer, Quote> quoteMap = new HashMap<Integer, Quote>();
    static HashMap<Integer, Pair> pairs = new HashMap<Integer, Pair>();
    static ArrayList<String> filesNotRead = new ArrayList<String>();
    static ArrayList<String> filesRead = new ArrayList<String>();
    static ArrayList<String> filesNotMatched = new ArrayList<String>();
    
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
    //reminder: comment how everything works later.
    //fuck

    public static void main(String[] args) throws Exception {
        Out out = new Out("src\\outputs\\output.txt");
        PrintWriter outPOs = new PrintWriter("src\\outputs\\POs.csv");
        PrintWriter outQuotes = new PrintWriter("src\\outputs\\Quotes.csv");
        outPOs.println("PO #,Date,Vendor,Description,Qty,Rate,Job,Amount,Total,Memo,Payment Terms,Quotes?");
        outQuotes.println("Vendor Name,Quote Number,PO Number,Ship Date,Qty,Qty Unit,Description,Unit Price,\"UoM\",Ext Price,S&H,Tax,Total");
        ArrayList<String> matchListt = new ArrayList<String>();
        Scanner scan = new Scanner(new File("src\\inputs\\matches.txt"));
        while (scan.hasNextLine()) {
            matchListt.add(scan.nextLine());
        }
        scan.close();
        String[] matchList = stringArrayListToArrayStatic(matchListt);
        Tabula t = new Tabula(out);
        WSL wsl = new WSL(out);
        //int pairID = 0;
        //iterate through the files
        for (File folder : dir) {
            PO po = new PO();
            ArrayList<Quote> quotes = new ArrayList<Quote>(Arrays.asList(new Quote()));
            for (File aPDF : folder.listFiles()) {
                out.println("Current file: \"" + aPDF.getName() + "\"");
                out.println("Filepath: \"" + aPDF.getPath() + "\"");
                if (match(matchList[0], (aPDF.getName()))) {
                    out.println("Matches! Type is PO");
                    filesRead.add(aPDF.getPath());
                    po = t.readTables(aPDF, out);
                    poMap.put(po.getID(), po);
                    t.clear();
                } else {
                    boolean a = true;
                    if (match(matchList[1], (aPDF.getName()))) {
                        out.println("Matches! Type is 0");
                        filesRead.add(aPDF.getPath());
                        quotes.add(wsl.readTables(aPDF, 0));
                    } else if (match(matchList[2], (aPDF.getName()))) {
                        out.println("Matches! Type is 1");
                        filesNotRead.add(aPDF.getPath());
                        quotes.add(wsl.readTables(aPDF, 1));
                    } else if (match(matchList[3], (aPDF.getName()))) {
                        out.println("Matches! Type is 2");
                        filesNotRead.add(aPDF.getPath());
                        quotes.add(wsl.readTables(aPDF, 2));
                    } else if (match(matchList[4], (aPDF.getName()))) {
                        out.println("Matches! Type is 3");
                        filesNotRead.add(aPDF.getPath());
                        quotes.add(wsl.readTables(aPDF, 3));
                    } else {
                        out.println("File didn't match.\n");
                        if (aPDF.getName().substring(aPDF.getName().length()-3).toLowerCase().equals("pdf")) {
                            filesNotMatched.add(aPDF.getPath());
                        }
                        a = false;
                    }
                    if (a) {
                        quoteMap.put(quotes.get(quotes.size()-1).getID(), quotes.get(quotes.size()-1));
                        wsl.clear();
                    }
                }
            }
            for (Order o : po.getOrders()) {
                if (isReference(o.getDesc()) >= 0) {
                    //do nothing for now
                }
            }
            /*
            try {
                pairs.put(pairID, new Pair(po, quotes));
                pairID++;
            } catch (NullPointerException e) {
                out.println("Error linking PO and Quote. Ignoring for now...");
            }
            */
        }
        for (int id : poMap.keySet()) {
            outPOs.println(poMap.get(id).toCSV());
        }
        for (int id : quoteMap.keySet()) {
            outQuotes.println(quoteMap.get(id).toCSV());
        }
        out.println("FILES READ:");
        for (String str : filesRead) {
            out.println("\\" + str);
        }
        out.lnprintln("FILES NOT READ:");
        for (String str : filesNotRead) {
            out.println("\\" + str);
        }
        out.lnprintln("FILES NOT MATCHED:");
        for (String str : filesNotMatched) {
            out.println("\\" + str);
        }
        int totalFiles = filesRead.size() + filesNotRead.size() + filesNotMatched.size();
        out.lnprintln("TOTAL READ FILES: " + filesRead.size());
        out.println("TOTAL NOT READ FILES: " + filesNotRead.size());
        out.println("TOTAL NOT MATCHED FILES: " + filesNotMatched.size());
        out.println("TOTAL FILES: " + totalFiles);
        out.lnprintln("PERCENT OF FILES NOT READ OR MATCHED: " + formatDouble((double)(filesNotRead.size()+filesNotMatched.size())*100.0/totalFiles) + "%");
        out.lnprintln("Program ended successfully!\n");
        outPOs.close();
        outQuotes.close();
        out.close();
    }
}
