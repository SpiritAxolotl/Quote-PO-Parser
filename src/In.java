import java.util.Scanner;
//import java.io.File;

public final class In {
    private static Scanner in;
    
    public In(String input) {
        in = new Scanner(input);
    }
    
    public String next() {
        return in.nextLine();
    }
    public String skip(int num) {
        for (int i=0; i<num; i++) {
            in.nextLine();
        }
        return in.nextLine();
    }
    public boolean hasNextLine() {
        return in.hasNextLine();
    }
    public void close() {
        in.close();
    }
}
