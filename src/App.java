import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class App extends Base {
    static File[] dir = (new File("src\\inputs\\extracted\\")).listFiles();
    static ArrayList<PO> poList = new ArrayList<PO>();
    static ArrayList<Quote> quoteList = new ArrayList<Quote>();
    static HashMap<Integer, Pair> pairs = new HashMap<Integer, Pair>();
    static ArrayList<String> filesNotRead = new ArrayList<String>();
    static ArrayList<String> filesRead = new ArrayList<String>();
    
    public int POIndexOfID(int id) {
        for (int i = 0; i < poList.size(); i++) {
            if (poList.get(i).getID() == id) {
                return i;
            }
        }
        return 1;
    }
    public int quoteIndexOfID(int id) {
        for (int i = 0; i < quoteList.size(); i++) {
            if (quoteList.get(i).getID() == id) {
                return i;
            }
        }
        return 1;
    }
    public String[] findSuppliesPO(int id){
        PO po = poList.get(POIndexOfID(id));
        Order[] temp = po.getOrdersArray();
        String[] descList = new String[temp.length];
        for (int i=0; i<temp.length; i++) {
            descList[i] = temp[i].getDesc();
        }
        return descList;
    }
    public String[] findSuppliesQuote(int id){
        Quote quote = quoteList.get(quoteIndexOfID(id));
        Order[] temp = quote.getOrdersArray();
        String[] descList = new String[temp.length];
        for (int i=0; i<temp.length; i++) {
            descList[i] = temp[i].getDesc();
        }
        return descList;
    }
    
    public static void sortLists() {
        for (int i = 1; i < poList.size(); i++) {
            PO currentElement = poList.get(i);
            int j = i - 1;
            while (j >= 0 && currentElement.getID() > poList.get(j).getID()) {
                poList.set(j + 1, poList.get(j));
                j--;
            }
            poList.set(j + 1, currentElement);
        }
        for (int i = 1; i < quoteList.size(); i++) {
            Quote currentElement = quoteList.get(i);
            int j = i - 1;
            while (j >= 0 && currentElement.getID() > quoteList.get(j).getID()) {
                quoteList.set(j + 1, quoteList.get(j));
                j--;
            }
            quoteList.set(j + 1, currentElement);
        }
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
        //iterate through the files
        for (File folder : dir) {
            PO po = new PO();
            ArrayList<Quote> quotes = new ArrayList<Quote>(Arrays.asList(new Quote()));
            for (File aPDF : folder.listFiles()) {
                out.println("Current file: \"" + aPDF.getName() + "\"");
                out.println("Filepath: \"" + aPDF.getPath() + "\"");
                if (match(matchList[0], (aPDF.getName()))) {
                    out.println("Matches! Type is PO");
                    try {
                        po = t.readTables(aPDF, out);
                        poList.add(po);
                        t.clear();
                        filesRead.add(aPDF.getPath());
                        out.println("File parsed correctly!\n");
                    } catch (Exception e) {
                        filesNotRead.add(aPDF.getPath());
                        out.println("File didn't parse correctly! Do this one manually.\n");
                    }
                } else {
                    boolean a = true;
                    if (match(matchList[1], (aPDF.getName()))) {
                        out.println("Matches! Type is AED");
                        try {
                            Quote quote = wsl.readTables(aPDF, 0);
                            quotes.add(quote);
                            filesRead.add(aPDF.getPath());
                            out.println("File parsed correctly!\n");
                        } catch (Exception e) {
                            filesNotRead.add(aPDF.getPath());
                            out.println("File didn't parse correctly! Do this one manually.\n");
                        }
                    } else if (match(matchList[2], (aPDF.getName()))) {
                        out.println("Matches! Type is World Electric");
                        try {
                            Quote quote = wsl.readTables(aPDF, 1);
                            quotes.add(quote);
                            filesRead.add(aPDF.getPath());
                            out.println("File parsed correctly!\n");
                        } catch (Exception e) {
                            filesNotRead.add(aPDF.getPath());
                            out.println("File didn't parse correctly! Do this one manually.\n");
                        }
                    } else if (match(matchList[3], (aPDF.getName()))) {
                        out.println("Matches! Type is Graybar");
                        try {
                            Quote quote = wsl.readTables(aPDF, 2);
                            quotes.add(quote);
                            filesRead.add(aPDF.getPath());
                            out.println("File not configured yet. Working on it now.\n");
                        } catch (Exception e) {
                            filesNotRead.add(aPDF.getPath());
                            out.println("File didn't parse correctly! Do this one manually.\n");
                        }
                    } else if (match(matchList[4], (aPDF.getName()))) {
                        out.println("Matches! Type is City Electric");
                        out.println("File not configured yet! (Davey's fault). Do this one manually.\n");
                        filesNotRead.add(aPDF.getPath());
                    } else {
                        out.println("File didn't match.\n");
                        if (aPDF.getName().substring(aPDF.getName().lastIndexOf(".")+1).toLowerCase().equals("pdf")) {
                            filesNotRead.add(aPDF.getPath());
                        }
                        a = false;
                    }
                    if (a) {
                        quoteList.addAll(quotes);
                        wsl.clear();
                    }
                }
            }
            for (Order o : po.getOrders()) {
                if (isReference(o.getDesc()) >= 0) {
                    //do nothing for now. this is for the supplies stuff
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
        
        sortLists();
        
        for (PO po : poList) {
            outPOs.println(po.toCSV());
        }
        for (Quote quote : quoteList) {
            outQuotes.println(quote.toCSV());
        }
        out.println("FILES READ:");
        for (String str : filesRead) {
            out.println("\\" + str);
        }
        out.lnprintln("FILES NOT READ:");
        for (String str : filesNotRead) {
            out.println("\\" + str);
        }
        int totalFiles = filesRead.size() + filesNotRead.size();
        out.lnprintln("\nTOTAL READ FILES: " + filesRead.size());
        out.println("TOTAL NOT READ FILES: " + filesNotRead.size());
        out.println("TOTAL FILES: " + totalFiles);
        out.lnprintln("PERCENT OF FILES NOT READ: " + formatDouble((double)(filesNotRead.size())*100.0/totalFiles) + "%");
        out.lnprintln("Program ended successfully!\n");
        outPOs.close();
        outQuotes.close();
        out.close();
    }
}
