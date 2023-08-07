public class Order extends Base {
    //"type" will be true if a po and false if a quote
    private boolean type;
    private String desc;
    private int quantity;
    private String qtyunit; //quote exclusive
    private double rate; //gonna act as unit price too
    private String rateunit; //quote exclusive
    private String job;
    private double amount; //gonna act as ext price too
    
    public Order(boolean type) {
        this.type = type;
        this.quantity = -1;
        this.rate = -1.0;
        this.desc = "";
        this.amount = -1.0;
        this.job = "";
        this.qtyunit = "";
        this.rateunit = "";
    }
    public String getDesc() {
        return this.desc;
    }
    public String setDesc(String desc) {
        String oldDesc = this.desc;
        String cleanDesc = desc.replaceAll("\uFFFD", "-");
        this.desc = removeAsteriskContent(cleanDesc);
        return oldDesc;
    }
    public String appendDesc(String desc) {
        String oldDesc = this.desc;
        this.setDesc(oldDesc + desc);
        return oldDesc;
    }
    public int getQuantity() {
        return this.quantity;
    }
    public String getQtyUnit() {
        return this.qtyunit;
    }
    public int setQuantity(int quantity) {
        int oldQuantity = this.quantity;
        this.quantity = quantity;
        return oldQuantity;
    }
    public int setQuantity(String quantity) {
        int oldQuantity = this.quantity;
        String cleanQty = quantity.strip().replaceAll(",","");
        if (this.type) {
            this.setQuantity(Integer.parseInt(cleanQty));
        } else {
            int i = 1;
            try {
                while (i<quantity.length()) {
                    this.setQuantity(Integer.parseInt(cleanQty.substring(0,i)));
                    i++;
                }
            } catch (NullPointerException | NumberFormatException er) {
                i--;
                this.setQuantity(Integer.parseInt(cleanQty.substring(0,i)));
                this.setQtyUnit(cleanQty.substring(i));
            }
        }
        return oldQuantity;
    }
    public String setQtyUnit(String qtyunit) {
        String oldQtyUnit = this.qtyunit;
        this.qtyunit = qtyunit.toLowerCase();
        return oldQtyUnit;
    }
    public double getRate() {
        return this.rate;
    }
    public String getRateUnit() {
        return this.rateunit;
    }
    public double setRate(double rate) {
        double oldRate = this.rate;
        this.rate = rate;
        return oldRate;
    }
    public double setRate(String rate) {
        double oldRate = this.rate;
        String cleanRate = rate.strip().replaceAll(",", "").replaceAll("\"", "");
        if (this.type) {
            this.setRate(Double.parseDouble(cleanRate));
        } else {
            String[] split = cleanRate.split("\\/");
            this.setRate(Double.parseDouble(split[0]));
            this.setRateUnit(split[1]);
        }
        return oldRate;
    }
    public String setRateUnit(String rateunit) {
        String oldRateUnit = this.rateunit;
        this.rateunit = rateunit.toUpperCase();
        return oldRateUnit;
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
        String cleanAmount = amount.strip().replaceAll(",", "").replaceAll("\"", "");
        this.setAmount(Double.parseDouble(cleanAmount));
        return oldAmount;
    }
    //add another case for the quotes at some point
    public Order isValid(Out out) throws NullPointerException {
        if (this.type) {
            if (
                this.getDesc().isBlank() ||
                this.getQuantity() == -1 ||
                this.getRate() == -1.0 ||
                this.getAmount() == -1.0
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
        } else {
            if (
                this.getDesc().isBlank() ||
                this.getQuantity() == -1 ||
                this.getQtyUnit().isBlank() ||
                this.getRate() == -1.0 ||
                this.getRateUnit().isBlank() ||
                this.getAmount() == -1.0
            ) {
                String message = "Parameters missing: Order ";
                if (this.getDesc().isBlank()) {
                    message += "Description, ";
                }
                if (this.getQuantity() == -1) {
                    message += "Quantity, ";
                }
                if (this.getQtyUnit().isBlank()) {
                    message += "Quantity Unit, ";
                }
                if (this.getRate() == -1.0) {
                    message += "Rate, ";
                }
                if (this.getRateUnit().isBlank()) {
                    message += "Rate Unit, ";
                }
                if (this.getAmount() == -1.0) {
                    message += "Amount, ";
                }
                message = message.substring(0, message.length()-2);
                //-1, new int[3], "", new ArrayList<Order>(), -1, "", ""
                out.println(message);
                //out.close();
                //throw new NullPointerException(message);
            }
        }
        return this;
    }
}