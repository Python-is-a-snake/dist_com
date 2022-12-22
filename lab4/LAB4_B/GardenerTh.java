package LAB4_B;

import java.util.concurrent.locks.ReentrantLock;

public class GardenerTh extends Thread {
    ReentrantLock lock;
    Yard yard;

    GardenerTh(ReentrantLock lock, Yard yard) {
        this.lock = lock;
        this.yard = yard;
    }
    public void run() {
        while (true) {
            lock.lock();
            try {
                for (int i = 0; i < yard.yard.length; i++) {
                    for (int j = 0; j < yard.yard.length; j++) {
                        yard.ChangePlantStatus(i, j, 1);
                    }
                }
            } finally {
                lock.unlock();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }
}
