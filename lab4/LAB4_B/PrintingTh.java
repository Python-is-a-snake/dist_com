package LAB4_B;
import java.util.concurrent.locks.ReentrantLock;

public class PrintingTh extends Thread {
    ReentrantLock lock;
    Yard yard;

    PrintingTh(ReentrantLock lock, Yard yard) {
        this.lock = lock;
        this.yard = yard;
    }
    public void run() {
        while (true) {
            lock.lock();
            try {
                yard.prettyGardenOutput();
            } finally {
                lock.unlock();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
        }
    }
}
