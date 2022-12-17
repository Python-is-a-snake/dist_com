package LAB3_A;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final int BEES_AMOUNT = 5;
    public static void main(String [] args){
        Semaphore semaphore = new Semaphore();
        Pot pot = new Pot(80);
        List<Thread> bees = new ArrayList<>();
        for (int i = 1; i < BEES_AMOUNT+1; i++) {
            Bee bee = new Bee(pot, semaphore);
            Thread b = new Thread(bee,"Bee #"+i);
            bees.add(b);
        }
        for(Thread b : bees){
            b.start();
        }
        Pooh pooh = new Pooh(semaphore, pot);
        Thread winnie = new Thread(pooh);
        winnie.start();
    }
}
