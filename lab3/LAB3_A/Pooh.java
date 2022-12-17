package LAB3_A;

public class Pooh implements Runnable{
    private Semaphore semaphore;
    private Pot pot;

    Pooh(Semaphore semaphore, Pot pot){
        this.semaphore = semaphore;
        this.pot = pot;
    }

    @Override
    public void run() {
        while (true){
            if(semaphore.getState()){
                System.out.println("WINNIE WOKE UP");
                pot.free();
                semaphore.makeFalse();
                break;
            }
            try {
                Thread.sleep(100);
            }catch (InterruptedException e){e.printStackTrace();}
        }
    }
}
