import java.util.ArrayList;

public class Pair {
    private PO po;
    private ArrayList<Quote> quotes;
    public Pair(PO po, ArrayList<Quote> quotes) {
        this.po = po;
        this.quotes = quotes;
    }
    public Pair(PO po) {
        this.po = po;
    }
    public Pair(Quote quote) {
        this.quotes.add(quote);
    }
    
    public PO getPO(){
        return this.po;
    }
    public Quote getQuote(){
        return this.quotes.get(0);
    }
    public Quote getQuote(int num){
        return this.quotes.get(num);
    }public Quote getLastQuote(){
        return this.quotes.get(this.quotes.size()-1);
    }
    public ArrayList<Quote> getQuotes(){
        return this.quotes;
    }
    public void setPO(PO po){
        this.po = po;
    }
    public void addQuote(Quote quote) {
        this.quotes.add(quote);
    }
    public void addQuote(int num, Quote quote) {
        this.quotes.add(num, quote);
    }
    public Quote setQuote(int num, Quote quote) {
        return this.quotes.set(num, quote);
    }
    public Quote removeQuote(int num) {
        return this.quotes.remove(num);
    }
    public String toCSV(){
        return this.po.toCSV();
    }
}
