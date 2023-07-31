import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Base {
    public int[] intArrayListToArray(ArrayList<Integer> ints) {
        int[] integers = new int[ints.size()];
        for (int i=0; i<ints.size(); i++) {
            integers[i] = ints.get(i);
        }
        return integers;
    }
    
    public ArrayList<Integer> intArrayToArrayList(int[] ints) {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        for (int i : ints) {
            integers.add(i);
        }
        return integers;
    }
    
    public String[] stringArrayListToArray(ArrayList<String> strs) {
        String[] strings = new String[strs.size()];
        for (int i=0; i<strs.size(); i++) {
            strings[i] = strs.get(i).strip();
        }
        return strings;
    }
    
    public ArrayList<String> stringArrayToArrayList(String[] strs) {
        ArrayList<String> strings = new ArrayList<String>();
        for (String i : strs) {
            strings.add(i.strip());
        }
        return strings;
    }
    
    public int findNextValue(String[] strings, int index, boolean quote) {
        for(int i=index; i<strings.length; i++) {
            try {
                if (quote && strings[i].length()>0) {
                    Integer.parseInt(strings[i].substring(1));
                } else {
                    Integer.parseInt(strings[i]);
                }
                return i;
            } catch (NumberFormatException e) {}
        }
        return -1;
    }
    
    public int findNextDateValue(String[] strings, int index) {
        for(int i=index; i<strings.length; i++) {
            try {
                String[] split = strings[i].split("/");
                Integer.parseInt(split[0]);
                Integer.parseInt(split[1]);
                Integer.parseInt(split[2]);
                return i;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {}
        }
        return -1;
    }
    
    public int findThing(String[] strings, int index) {
        for(int i=index; i<strings.length; i++) {
            if (!strings[i].isBlank()) {
                return i;
            }
        }
        return -1;
    }
    public int findThing(String[] strings) {
        return findThing(strings, 0);
    }
    public int findThing(String[] strings, int[] indexes) {
        int index = 0;
        for(int i=0; i<indexes.length; i++) {
            int thing = findThing(strings, index + indexes[i]);
            if (thing < 0) {
                return -1;
            }
            index = thing + 1;
        }
        return index - 1;
    }
    public int findThing(ArrayList<String> strings) {
        return findThing(stringArrayListToArray(strings));
    }
    public int findThing(ArrayList<String> strings, int index) {
        return findThing(stringArrayListToArray(strings), index);
    }
    public int findThing(ArrayList<String> strings, int[] indexes) {
        return findThing(stringArrayListToArray(strings), indexes);
    }
    
    public int findSpecificThing(String[] strings, String target, int index) {
        for(int i=index; i<strings.length; i++) {
            if (strings[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
    public int findSpecificThing(String[] strings, String target) {
        return findSpecificThing(strings, target, 0);
    }
    public int findTwoSpecificThing(String[] strings, String target, String target2) {
        int a = findSpecificThing(strings, target);
        int b = findSpecificThing(strings, target2);
        return getMinPositiveValue(a,b);
    }
    public int findSpecificThing(ArrayList<String> strings, String target) {
        return findSpecificThing(stringArrayListToArray(strings), target);
    }
    public int findSpecificThing(ArrayList<String> strings, String target, int index) {
        return findSpecificThing(stringArrayListToArray(strings), target, index);
    }
    public int findTwoSpecificThing(ArrayList<String> strings, String target, String target2) {
        return findTwoSpecificThing(stringArrayListToArray(strings), target, target2);
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
    
    public String doubleToString(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(number);
    }
    
    public String csvCommas(Object[] all) {
        String concat = "";
        for (int i=0; i<all.length-1; i++) {
            concat += inputSanitizer(all[i].toString())+",";
        }
        concat += all[all.length-1];
        return concat;
    }
    
    public static String formatDouble(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(number);
    }
    
    //note that this doesn't FULLY sanitize the entire input
    //it will get f'd up if there are single doublequotes
    //and I pray none of the inputs have them
    //(but if they do it will be obvious when it happens)
    //update: it was obvious when it happened. fixing now...
    public String inputSanitizer(String text) {
        if (text.contains(",") || text.contains("\"")) {
            text = "\"" + text.replaceAll("\"", "\"\"") + "\"";
        }
        if (text.equals("-1") || text.equals("-1.0")) {
            text = "";
        }
        return text;
    }
    
    public int[] instancesOf(String[] str, String match) {
        ArrayList<Integer> ints = new ArrayList<Integer>();
        for (int i=0; i<str.length; i++) {
            if (str[i].equals(match)) {
                ints.add(i);
            }
        }
        return intArrayListToArray(ints);
    }
    
    public static boolean match(String regex, String match){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(match);
        return m.matches();
    }
    
    public static boolean isValid(int num) {
        return num < 0;
    }
    public static boolean isValid(double num) {
        return num < 0.0;
    }
    public static boolean isValid(String str) {
        return str.strip().equals("");
    }
    
    public static int getMinPositiveValue(int value1, int value2) {
        if (value1 >= 0 && value2 >= 0) {
            return Math.min(value1, value2);
        } else if (value1 < 0 && value2 >= 0) {
            return value2;
        } else if (value1 >= 0 && value2 < 0) {
            return value1;
        } else {
            return -1;
        }
    }
}
