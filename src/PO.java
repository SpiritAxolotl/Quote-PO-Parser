import java.util.ArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PO {
    private int id;
    private int[] date;
    private String vendor;
    private ArrayList<Order> orders;
    private double total;
    private String memo;
    private String payterms;
    public PO(int id, int[] date, String vendor, ArrayList<Order> orders, double total, String memo, String payterms) {
        this.id = id;
        this.date = date;
        this.vendor = vendor;
        this.orders = orders;
        this.total = total;
        this.memo = memo;
        this.payterms = payterms;
    }
    public PO() {
        this(-1, new int[] {-1,-1,-1}, "", new ArrayList<Order>(), -1, "", "");
    }
    
    public int getID() {
        return this.id;
    }
    public int setID(int id) {
        int oldID = this.id;
        this.id = id;
        return oldID;
    }
    public int setID(String id) {
        int oldID = this.id;
        try {this.id = Integer.parseInt(id);} catch (NullPointerException | NumberFormatException er) {}
        return oldID;
    }
    public int[] getDate() {
        return this.date;
    }
    public int getDate(int num) {
        return this.date[num];
    }
    public String getDateString() {
        return this.date[0] + "/" + this.date[1] + "/" + this.date[2];
    }
    public int[] setDate(int[] date) {
        int[] oldDate = this.date;
        this.date = date;
        return oldDate;
    }
    public int[] setDate(int month, int day, int year) {
        int[] oldDate = this.date;
        this.date[0] = month;
        this.date[1] = day;
        this.date[2] = year;
        return oldDate;
    }
    public int[] setDate(String[] date) {
        int[] oldDate = this.date;
        int[] newDate = new int[3];
        for (int i=0; i<3; i++) {
            newDate[i] = Integer.parseInt(date[i]);
        }
        this.date = newDate;
        return oldDate;
    }
    public int[] setDate(String date) {
        int[] oldDate = this.date;
        String[] newDate = date.split("/");
        try {
            this.date[0] = Integer.parseInt(newDate[0]);
            this.date[1] = Integer.parseInt(newDate[1]);
            this.date[2] = Integer.parseInt(newDate[2]);
        } catch (NullPointerException | NumberFormatException er) {}
        return oldDate;
    }
    public String getVendor() {
        return this.vendor;
    }
    public String setVendor(String vendor) {
        String oldVendor = this.vendor;
        this.vendor = vendor;
        return oldVendor;
    }
    public Order getOrder() {
        return this.orders.get(0);
    }
    public Order getOrder(int num) {
        return this.orders.get(num);
    }
    public Order[] getOrdersArray() {
        Order[] orders = new Order[this.orders.size()];
        for (int i=0; i<this.orders.size(); i++) {
            orders[i] = this.orders.get(i);
        }
        return orders;
    }
    public Order getLastOrder() {
        return this.orders.get(this.orders.size()-1);
    }
    public ArrayList<Order> getOrders() {
        return this.orders;
    }
    public boolean addOrder(Order order) {
        return this.orders.add(order);
    }
    public void addOrder(Order order, int num) {
        this.orders.add(num, order);
    }
    public void addOrders(Order[] orders) {
        for(Order order : orders) {
            this.orders.add(order);
        }
    }
    public void addOrders(Order[] orders, int num) {
        for(int i=orders.length; i>=0; i--) {
            this.orders.add(num, orders[i]);
        }
    }
    public Order setOrder(int num, Order order) {
        return this.orders.set(num, order);
    }
    public Order removeOrder(int num) {
        return this.orders.remove(num);
    }
    public double getTotal() {
        return this.total;
    }
    //maybe write something that checks the size and makes sure it's correct?
    //or at least allows a tiny margin of error?
    //but even then maybe that needs to be reported in some form
    public double setTotal(double total) {
        double oldTotal = this.total;
        /*double computedTotal = 0;
        for (Order order : orders) {
            computedTotal += order.getAmount();
        }
        if (computedTotal == total) {
            this.total = total;
        } else {
            this.total = computedTotal;
        }*/
        this.total = total;
        return oldTotal;
    }
    //goes from the bottom of the list until it hits something that isn't blank (assumed to be the total)
    public int findSetTotal(ArrayList<String> strings) {
        for(int i=strings.size()-1; i>=0; i--) {
            if (!strings.get(i).isBlank()) {
                this.setTotal(strings.get(i));
                return i;
            }
        }
        return -1;
    }
    public int findSetDate(ArrayList<String> strings) {
        for(int i=11; i<strings.size(); i++) {
            if (!strings.get(i).isBlank()) {
                this.setDate(strings.get(i));
                return i;
            }
        }
        return -1;
    }
    public int findDescDate(ArrayList<String> strings) {
        for(int i=findSetDate(strings)+1; i<strings.size(); i++) {
            if (!strings.get(i).isBlank()) {
                //this.setDate(strings.get(i));
                return i;
            }
        }
        return -1;
    }
    public double setTotal(String total) {
        String cleanTotal = total.trim().replaceAll(",", "");
        //System.out.println(cleanTotal);
        //System.out.println(cleanTotal.substring(cleanTotal.indexOf("$")+2,cleanTotal.length()-1));
        double oldTotal = this.total;
        this.total = Double.parseDouble(cleanTotal.substring(cleanTotal.indexOf("$")+1,cleanTotal.length()-1));
        return oldTotal;
    }
    public String getMemo() {
        return this.memo;
    }
    public String setMemo(String memo) {
        String oldMemo = this.memo;
        this.memo = memo;
        return oldMemo;
    }
    public String getPayTerms() {
        return this.payterms;
    }
    public String setPayTerms(String payterms) {
        String oldPayTerms = this.payterms;
        this.payterms = payterms;
        return oldPayTerms;
    }
    
    public String intToString(double num) {
        if (num < 0) {
            return "";
        }
        return "" + num;
    }
    
    public String formatDoubleWithCommas(double number, boolean isDollar) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        if (isDollar) {
            decimalFormat.applyPattern("$#,##0.00");
        } else {
            decimalFormat.applyPattern("#,##0.00");
        }
        return decimalFormat.format(number);
    }
    
    public String formatDouble(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(number);
    }
    
    private String csvCommas(Object[] all) {
        String concat = "";
        for (int i=0; i<all.length-1; i++) {
            concat += this.inputSanitizer(all[i].toString())+",";
        }
        concat += all[all.length-1];
        return concat;
    }
    //note that this doesn't FULLY sanitize the entire input
    //it will get f'd up if there are single doublequotes
    //and I pray none of the inputs have them
    //(but if they do it will be obvious when it happens)
    //update: it was obvious when it happened. fixing now...
    public String inputSanitizer(String text) {
        if (text.contains(",")||text.contains("\"")) {
            text = "\"" + text.replaceAll("\"", "\"\"") + "\"";
        }
        return text;
    }
    public String toCSV() {
        String concat = "";
        boolean isBeginning = true;
        for (Order o : this.orders) {
            Object[] obj = {
                this.getID(),
                this.getDateString(),
                this.getVendor(),
                o.getDesc(),
                this.intToString(o.getQuantity()),
                this.formatDoubleWithCommas(o.getRate(), false),
                o.getJob(),
                this.formatDoubleWithCommas(o.getAmount(), false),
                this.formatDoubleWithCommas(this.getTotal(), true),
                this.getMemo(),
                this.getPayTerms(),
                isBeginning
            };
            if (isBeginning) {
                isBeginning = false; 
                concat += this.csvCommas(obj);
            } else {
                concat += "\n" + this.csvCommas(obj);
            }
        }
        return concat;
    }
    public PO isValid(Out out) throws NullPointerException {
        if(
            this.getID() == -1 ||
            (this.getDate(0) == -1 || this.getDate(1) == -1 || this.getDate(2) == -1) ||
            this.getVendor().isBlank() ||
            this.getTotal() == -1
        ){
            String message = "Parameters missing: ";
            if (this.getID() == -1) {
                message += "PO ID, ";
            }
            if (this.getDate(0) == -1 || this.getDate(1) == -1 || this.getDate(2) == -1) {
                message += "Date, ";
            }
            if (this.getVendor().isBlank()) {
                message += "Vendor Name, ";
            }
            if (this.getTotal() == -1) {
                message += "Total, ";
            }
            message = message.substring(0, message.length()-2);
            //-1, new int[3], "", new ArrayList<Order>(), -1, "", ""
            out.println(message);
            out.close();
            throw new NullPointerException(message);
        }
        return this;
    }
}