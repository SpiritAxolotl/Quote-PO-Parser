public class Order {
    //"type" will be true if a po and false if a quote
    private boolean type;
    private String desc;
    private int quantity;
    private double rate;
    private String job;
    private double amount;
    private int code;
    private String oquantity;
    private String price;
    public Order(String desc, int quantity, double rate, String job, double amount) {
        this.desc = desc;
        this.quantity = quantity;
        this.rate = rate;
        this.job = job;
        this.amount = amount;
    }
    public Order(int code, String desc, String oquantity, String price) {
        this.code = code;
        this.desc = desc;
        this.oquantity = oquantity;
        this.price = price;
    }
    public Order(boolean type) {
        this.type = type;
        this.desc = "";
        if (this.type) {
            this.quantity = -1;
            this.rate = -1.0;
            this.job = "";
            this.amount = -1.0;
        } else {
            this.code = -1;
            this.oquantity = "";
            this.price = "";
        }
    }
    public String getDesc() {
        return this.desc;
    }
    public String setDesc(String desc) {
        String oldDesc = this.desc;
        this.desc = desc.replaceAll("\uFFFD", "-");
        return oldDesc;
    }
    public int getCode() {
        return this.code;
    }
    public String setCode(int code) {
        String oldCode = this.desc;
        this.code = code;
        return oldCode;
    }
    public int setCode(String code) {
        int oldCode = this.code;
        try {this.code = Integer.parseInt(code);} catch (NullPointerException | NumberFormatException er) {}
        return oldCode;
    }
    public String getPrice() {
        return this.price;
    }
    public String setPrice(String price) {
        String oldPrice = this.price;
        this.price = price;
        return oldPrice;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public String getOQuantity() {
        return this.oquantity;
    }
    public int setQuantity(int quantity) {
        int oldQuantity = this.quantity;
        this.quantity = quantity;
        return oldQuantity;
    }
    public int setQuantity(String quantity) {
        int oldQuantity = this.quantity;
        try {this.quantity = Integer.parseInt(quantity.trim().replaceAll(",",""));} catch (NullPointerException | NumberFormatException er) {}
        return oldQuantity;
    }
    public String setOQuantity(String oquantity) {
        String oldOQuantity = this.oquantity;
        this.oquantity = oquantity;
        return oldOQuantity;
    }
    public double getRate() {
        return this.rate;
    }
    public double setRate(double rate) {
        double oldRate = this.rate;
        this.rate = rate;
        return oldRate;
    }
    public double setRate(String rate) {
        double oldRate = this.rate;
        String cleanRate = rate.trim().replaceAll(",", "").replaceAll("\"", "");
        try {this.rate = Double.parseDouble(cleanRate);} catch (NullPointerException | NumberFormatException er) {}
        return oldRate;
    }
    public String getJob() {
        return this.job;
    }
    public String setJob(String job) {
        String oldJob = this.job;
        this.job = job;
        return oldJob;
    }
    public double getAmount() {
        return this.amount;
    }
    public double setAmount(double amount) {
        double oldAmount = this.amount;
        this.amount = amount;
        return oldAmount;
    }
    public double setAmount(String amount) {
        double oldAmount = this.rate;
        String cleanAmount = amount.trim().replaceAll(",", "").replaceAll("\"", "");
        try {this.amount = Double.parseDouble(cleanAmount);} catch (NullPointerException | NumberFormatException er) {}
        return oldAmount;
    }
    //add another case for the quotes at some point
    public Order isValid(Out out) throws NullPointerException {
        if(
            this.getDesc().isBlank() ||
            this.getQuantity() == -1 ||
            this.getRate() == -1.0 ||
            this.getAmount() == 1.0
        ) {
            String message = "Parameters missing: Order ";
            if (this.getDesc().isBlank()) {
                message += "Description, ";
            }
            if (this.getQuantity() == -1) {
                message += "Quantity, ";
            }
            if (this.getRate() == -1.0) {
                message += "Rate, ";
            }
            if (this.getAmount() == -1.0) {
                message += "Amount, ";
            }
            message = message.substring(0, message.length()-2);
            //-1, new int[3], "", new ArrayList<Order>(), -1, "", ""
            //out.close();
            //throw new NullPointerException(message);
        }
        return this;
    }
}