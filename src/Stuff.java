import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Stuff {
    public static int[] intArrayListToArray(ArrayList<Integer> ints) {
        int[] integers = new int[ints.size()];
        for (int i=0; i<ints.size(); i++) {
            integers[i] = ints.get(i);
        }
        return integers;
    }
    public static ArrayList<Integer> intArrayToArrayList(int[] ints) {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        for (int i : ints) {
            integers.add(i);
        }
        return integers;
    }
    public int findThing(ArrayList<String> strings, int index) {
        for(int i=index; i<strings.size(); i++) {
            if (!strings.get(i).isBlank()) {
                return i;
            }
        }
        return -1;
    }
    public int findThing(ArrayList<String> strings, int[] indexes) {
        int index = 0;
        for(int i=0; i<indexes.length; i++) {
            index = findThing(strings, index + indexes[i]) + 1;
        }
        return index - 1;
    }
    public int findSpecificThing(ArrayList<String> strings, String target) {
        for(int i=0; i<strings.size(); i++) {
            if (strings.get(i).equals(target)) {
                return i;
            }
        }
        return -1;
    }
    public int findSpecificThing(ArrayList<String> strings, String target, int index) {
        for(int i=index; i<strings.size(); i++) {
            if (strings.get(i).equals(target)) {
                return i;
            }
        }
        return -1;
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
    
    public String csvCommas(Object[] all) {
        String concat = "";
        for (int i=0; i<all.length-1; i++) {
            concat += all[i]+",";
        }
        concat += all[all.length-1];
        return concat;
    }
}
