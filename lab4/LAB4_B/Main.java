package LAB4_B;

import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Yard yard = new Yard();
        yard.prettyGardenOutput();

        WasterTh wasterTh = new WasterTh(lock, yard);
        wasterTh.start();
        PrintingTh threadPrinter = new PrintingTh(lock, yard);
        threadPrinter.start();
        ThreadGardener threadRainer = new ThreadGardener(lock, yard);
        threadRainer.start();
        WriterTh writerTh = new WriterTh(lock, yard, "result.txt");
        writerTh.start();
    }
}
