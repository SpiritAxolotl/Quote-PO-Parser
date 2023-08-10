import java.util.ArrayList;

public class Quote extends Base {
    private int id; //quote number
    private int custnum; //customer po number
    private int[] date; //shipping date
    private String vendor; //vendor NAME, not address
    private ArrayList<Order> orders; //quote-flavored
    private double subtotal;
    private double snh;
    private double tax;
    private double total; //amount due
    public Quote(int id, int custnum, int[] date, String vendor, ArrayList<Order> orders, double total) {
        this.id = id;
        this.custnum = custnum;
        this.date = date;
        this.vendor = vendor;
        this.orders = orders;
        this.total = total;
    }
    public Quote() {
        this(-1, -1, new int[] {-1,-1,-1}, "", new ArrayList<Order>(), -1);
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
        String cleanID = id;
        if (id.contains(" ")) {
            cleanID = id.substring(0, id.indexOf(" "));
        }
        if (id.substring(0,1).equals("S")) {
            this.setID(Integer.parseInt(cleanID.substring(1)));
        } else {
            this.setID(Integer.parseInt(cleanID));
        }
        return oldID;
    }
    public int getCustomerNum() {
        return this.custnum;
    }
    public int setCustomerNum(int custnum) {
        int oldNum = this.custnum;
        this.custnum = custnum;
        return oldNum;
    }
    public int setCustomerNum(String custnum) {
        int oldNum = this.custnum;
        this.setCustomerNum(Integer.parseInt(custnum.strip()));
        return oldNum;
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
        String[] newDate = date.split("/");
        return this.setDate(newDate);
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
        if (this.orders.size() == 0) {
            return this.getOrder(0);
        }
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
    public Order setOrder(int num, Order order) {
        return this.orders.set(num, order);
    }
    public void addOrders(ArrayList<Order> orderlist) {
        orders.addAll(orderlist);
    }
    public Order removeOrder(int num) {
        return this.orders.remove(num);
    }
    public double getSubtotal() {
        return this.subtotal;
    }
    public double setSubtotal(double subtotal) {
        double oldSubtotal = this.subtotal;
        this.subtotal = subtotal;
        return oldSubtotal;
    }
    public double setSubtotal(String subtotal) {
        String cleanSubtotal = subtotal.strip().replaceAll(",", "");
        cleanSubtotal = cleanSubtotal.substring(cleanSubtotal.indexOf("$")+1);
        return this.setSubtotal(
            Double.parseDouble(
                cleanSubtotal
            )
        );
    }
    public double getSNH() {
        return this.snh;
    }
    public double setSNH(double snh) {
        double oldSNH = this.snh;
        this.snh = snh;
        return oldSNH;
    }
    public double setSNH(String snh) {
        String cleanSNH = snh.strip().replaceAll(",", "");
        return this.setSNH(
            Double.parseDouble(
                cleanSNH.substring(
                    cleanSNH.indexOf("$")+1,
                    cleanSNH.length()-1
                )
            )
        );
    }
    public double getTax() {
        return this.tax;
    }
    public double setTax(double tax) {
        double oldTax = this.tax;
        this.tax = tax;
        return oldTax;
    }
    public double findSetTax() {
        double oldTax = this.tax;
        //if they're invalid there's no fallback yet
        this.setTax(this.getTotal() - this.getSubtotal() - this.getSNH());
        return oldTax;
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
        String cleanTotal = total.strip().replaceAll(",", "");
        cleanTotal = cleanTotal.substring(cleanTotal.indexOf("$")+1);
        return this.setTotal(
            Double.parseDouble(
                cleanTotal
            )
        );
    }
    
    public String toCSV() {
        String concat = "";
        boolean isBeginning = true;
        for (Order o : this.orders) {
            Object[] obj = {
                this.getVendor(),
                this.getID(),
                this.getCustomerNum(),
                this.getDateString(),
                o.getQuantity(),
                o.getQtyUnit(),
                o.getDesc(),
                o.getRate(),
                o.getRateUnit(),
                o.getAmount(),
                //this.getSubtotal(),
                this.getSNH(),
                this.getTax(),
                this.getTotal()
                //isBeginning
            };
            if (isBeginning) {
                isBeginning = false;
                concat += csvCommas(obj);
            } else concat += "\n" + csvCommas(obj);
        }
        return concat;
    }
    public Quote isValid(Out out, int type) throws NullPointerException {
        if( (type == 0) && (
            this.getID() == -1 ||
            this.getCustomerNum() == -1 ||
            (this.getDate(0) == -1 || this.getDate(1) == -1 || this.getDate(2) == -1) ||
            this.getVendor().isBlank() ||
            this.getTotal() == -1
        )){
            String message = "Parameters missing: ";
            if (this.getID() == -1) {
                message += "Quote Number, ";
            }
            if (this.getCustomerNum() == -1) {
                message += "PO Number, ";
            }
            if (this.getDate(0) == -1 || this.getDate(1) == -1 || this.getDate(2) == -1) {
                message += "Ship Date, ";
            }
            if (this.getVendor().isBlank()) {
                message += "Vendor Name, ";
            }
            if (this.getTotal() == -1) {
                message += "Amount, ";
            }
            message = message.substring(0, message.length()-2);
            out.println(message);
            throw new NullPointerException(message);
        }
        return this;
    }
}