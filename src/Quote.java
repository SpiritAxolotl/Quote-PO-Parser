import java.util.ArrayList;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Quote {
    private int id;
    private int[] date;
    private String vendor;
    private String ref;
    private ArrayList<Order> orders;
    private double total;
    //Vendor name, quote date, vendor quote number, our reference, product code, product description, quantity, unit price, total price per line.
    //Vendor, date, quote number, our reference(?), total price per line
    public Quote(int id, int[] date, String vendor, String ref, ArrayList<Order> orders, double total) {
        this.id = id;
        this.date = date;
        this.vendor = vendor;
        this.ref = ref;
        this.orders = orders;
        this.total = total;
    }
    public Quote() {
        this(-1, new int[3], "", "", new ArrayList<Order>(), -1);
        setDate(-1, -1, -1);
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
    public Order setOrder(int num, Order order) {
        return this.orders.set(num, order);
    }
    public Order removeOrder(int num) {
        return this.orders.remove(num);
    }
    public double getTotal() {
        return this.total;
    }
    public double setTotal(double total) {
        double oldTotal = this.total;
        this.total = total;
        return oldTotal;
    }
    public int findSetTotal(ArrayList<String> strings) {
        for(int i=strings.size()-1; i>=0; i--) {
            if (!strings.get(i).equals("")) {
                this.setTotal(strings.get(i));
                return i;
            }
        }
        return -1;
    }
    public double setTotal(String total) {
        String cleanTotal = total.trim().replaceAll(",", "");
        System.out.println(cleanTotal);
        //System.out.println(cleanTotal.substring(cleanTotal.indexOf("$")+2,cleanTotal.length()-1));
        double oldTotal = this.total;
        this.total = Double.parseDouble(cleanTotal.substring(cleanTotal.indexOf("$")+1,cleanTotal.length()-1));
        return oldTotal;
    }
    public String getRef() {
        return this.ref;
    }
    public String setRef(String ref) {
        String oldRef = this.ref;
        this.ref = ref;
        return oldRef;
    }
    
    public String intToString(double num) {
        if (num < 0) {
            return "";
        }
        return "" + num;
    }
    
    public String formatDoubleWithCommas(double number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyPattern("#,##0.00");
        return decimalFormat.format(number);
    }
    
    public String doubleToString(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(number);
    }
    private String csvCommas(Object[] all) {
        String concat = "";
        for (int i=0; i<all.length-1; i++) {
            concat += all[i]+",";
        }
        concat += all[all.length-1];
        return concat;
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
                o.getCode(),
                o.getOQuantity(),
                o.getPrice(),
                "\"" + this.formatDoubleWithCommas(o.getAmount()) + "\"",
                "\"$" + this.formatDoubleWithCommas(this.getTotal()) + "\"",
                this.getRef(),
                isBeginning
            };
            if (isBeginning) {isBeginning = false; concat += this.csvCommas(obj);} else concat += "\n" + this.csvCommas(obj);
        }
        return concat;
    }
    public Quote isValid(Out out) throws NullPointerException {
        if(
            this.getID() == -1 ||
            (this.getDate(0) == -1 || this.getDate(1) == -1 || this.getDate(2) == -1) ||
            this.getVendor().isBlank() ||
            this.getRef().isBlank() ||
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
            if (this.getRef().isBlank()) {
                message += "Ref, ";
            }
            if (this.getTotal() == -1) {
                message += "Total, ";
            }
            message = message.substring(0, message.length()-2);
            //-1, new int[3], "", new ArrayList<Order>(), -1, "", ""
            out.close();
            throw new NullPointerException(message);
        }
        return this;
    }
}