import java.util.ArrayList;

public class PO extends Base {
    private int id;
    private int[] date;
    private String vendor;
    private ArrayList<Order> orders;
    private double total;
    private String memo;
    private String payterms;
    private int quotenum;
    public PO() {
        this.id = -1;
        this.date = new int[] {-1,-1,-1};
        this.vendor = "";
        this.orders = new ArrayList<Order>();
        this.total = -1;
        this.memo = "";
        this.payterms = "";
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
        this.setID(Integer.parseInt(id));
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
    public int[] setDate(int index, int num) {
        //for index: 0 = month, 1 = day, 2 = year
        int[] oldDate = this.date;
        this.date[index] = num;
        return oldDate;
    }
    public int[] setDate(int month, int day, int year) {
        int[] oldDate = this.date;
        this.setDate(month, day, year);
        return oldDate;
    }
    public int[] setDate(String[] date) {
        int[] oldDate = this.date;
        int[] newDate = new int[3];
        for (int i=0; i<3; i++) {
            newDate[i] = Integer.parseInt(date[i].strip());
        }
        this.setDate(newDate);
        return oldDate;
    }
    public int[] setDate(String date) {
        int[] oldDate = this.date;
        String[] newDate = date.split("/");
        for (int i=0;i<3;i++) {
            this.setDate(i, Integer.parseInt(newDate[i]));
        }
        return oldDate;
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
    //idk why I wrote this but it definitely is used so I won't touch it
    public int findDescDate(ArrayList<String> strings) {
        for(int i=findSetDate(strings)+1; i<strings.size(); i++) {
            if (!strings.get(i).isBlank()) {
                //this.setDate(strings.get(i));
                return i;
            }
        }
        return -1;
    }
    public String getVendor() {
        return this.vendor;
    }
    public String setVendor(String vendor) {
        String oldVendor = this.vendor;
        this.vendor = vendor;
        return oldVendor;
    }
    public Order getOrder(int num) {
        return this.orders.get(num);
    }
    public Order getOrder() {
        return this.getOrder(0);
    }
    public Order getLastOrder() {
        return this.getOrder(this.orders.size()-1);
    }
    public Order[] getOrdersArray() {
        Order[] orders = new Order[this.orders.size()];
        for (int i=0; i<this.orders.size(); i++) {
            orders[i] = this.orders.get(i);
        }
        return orders;
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
    public int findSetTotal(String[] strings) {
        for(int i=strings.length-1; i>=0; i--) {
            if (!strings[i].isBlank()) {
                this.setTotal(strings[i]);
                return i;
            }
        }
        return -1;
    }
    public double setTotal(String total) {
        String cleanTotal = total.strip().replaceAll(",", "");
        return this.setTotal(
            Double.parseDouble(
                cleanTotal.substring(
                    cleanTotal.indexOf("$")+1,
                    cleanTotal.length()-1
                )
            )
        );
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
    public int getQuoteNum() {
        return this.quotenum;
    }
    public int setQuoteNum(int quotenum) {
        int oldQuoteNum = this.quotenum;
        this.quotenum = quotenum;
        return oldQuoteNum;
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
                o.getQuantity(),
                o.getRate(),
                o.getJob(),
                o.getAmount(),
                this.getTotal(),
                this.getMemo(),
                this.getPayTerms(),
                //isBeginning
                this.getQuoteNum()
            };
            if (isBeginning) {
                isBeginning = false; 
                concat += csvCommas(obj);
            } else {
                concat += "\n" + csvCommas(obj);
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
            String message = "Parameters missing: PO ";
            if (this.getID() == -1) {
                message += "ID, ";
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
            out.println(message);
            throw new NullPointerException(message);
        }
        return this;
    }
}