package LAB3_B;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barber {
    private final Lock lock = new ReentrantLock();
    private final int timeToSleep = new Random().nextInt(450) + 50;

    public void cutHair(Visitor visitor) throws InterruptedException {
        lock.lock();
        System.out.println("<!> BARBER SERVES " + visitor.getName() + " RIGHT NOW <!>");
        Thread.sleep(timeToSleep);
        lock.unlock();
    }
}
