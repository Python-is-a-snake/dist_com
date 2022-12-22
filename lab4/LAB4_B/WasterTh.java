package LAB4_B;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class WasterTh extends Thread {
    ReentrantLock lock;
    Yard yard;

    WasterTh(ReentrantLock lock, Yard yard) {
        this.lock = lock;
        this.yard = yard;
    }

    public void run() {
        while (true) {
            lock.lock();
            try {
                int randX = ThreadLocalRandom.current().nextInt(0, 9);
                int randY = ThreadLocalRandom.current().nextInt(0, 9);
                yard.ChangePlantStatus(randX, randY, 0);
            } finally {
                lock.unlock();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }
    }
}
