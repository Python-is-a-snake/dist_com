package LAB3_A;

public class Bee implements Runnable {
    Pot pot;
    Semaphore semaphore;
    Bee(Pot pot, Semaphore semaphore){
        this.pot = pot;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        while (true){
            if(pot.isFilled()){
                semaphore.makeTrue();
                break;
            }
            pot.addHoney();
        }
    }
}
