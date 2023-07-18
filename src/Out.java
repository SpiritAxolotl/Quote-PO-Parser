import java.io.FileNotFoundException;
import java.io.PrintWriter;

public final class Out {
    private PrintWriter out;
    public Out(String filename) throws FileNotFoundException {
        out = new PrintWriter(filename);
    }

    public void print(String s) {
        out.print(s);
        System.out.print(s);
    }
    public void println(String s) {
        out.println(s);
        System.out.println(s);
    }
    public void println() {
        out.println();
        System.out.println();
    }
    public void lnprint(String s) {
        out.print("\n" + s);
        System.out.print("\n" + s);
    }
    public void lnprintln(String s) {
        out.println("\n" + s);
        System.out.println("\n" + s);
    }
    public void debug(Object obj) {
        out.println("Debug: " + obj.toString());
        System.out.println("Debug: " + obj.toString());
    }
    public void close() {
        out.close();
    }
}
