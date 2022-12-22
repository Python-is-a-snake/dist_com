package LAB4_B;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class WriterTh extends Thread{
    ReentrantLock lock;
    Yard yard;
    String filename;

    WriterTh(ReentrantLock lock, Yard yard, String filename) {
        this.lock = lock;
        this.yard = yard;
        this.filename = filename;
    }

    public void run() {
        while (true) {
            lock.lock();
            try {
                FileWriter fileWriter;
                try {
                    fileWriter = new FileWriter(filename, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                PrintWriter writer = new PrintWriter(fileWriter);

                for (int i = 0; i < yard.yard.length; i++) {
                    writer.print(Arrays.toString(yard.yard[i]));
                    writer.println();
                }
                writer.println();
                writer.close();
            }
            finally {
                lock.unlock();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }
    }
}
