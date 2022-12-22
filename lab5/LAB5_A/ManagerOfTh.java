package LAB5_A;
import java.util.ArrayList;

public class ManagerOfTh {
    private final int numberOfThreads;
    private ArrayList<Thread> allThreads;

    private int reachedBarrier;

    private Recrute recrute;

    public ManagerOfTh(Recrute recrute, int minimumNumberPerThread) {
        int recruteAmount = recrute.getRecruts().length;
        this.numberOfThreads = recruteAmount / minimumNumberPerThread;
        this.reachedBarrier = 0;
        this.recrute = recrute;
        int numberPerThread = (int) Math.ceil((double) recruteAmount / numberOfThreads);
        this.allThreads = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; ++i) {
            int beginIndex = numberPerThread * i;
            int endIndex = Math.min(numberPerThread + beginIndex, recruteAmount);
            Thread thread = new ManagerOfRecrutre(beginIndex, endIndex, recrute, this);
            allThreads.add(thread);
            thread.start();
        }
    }

    public synchronized void barrierIncrementation() {
        reachedBarrier++;
        if (reachedBarrier == numberOfThreads) {
            recrute.recrutePrint();
            if (!recrute.beingStat()) {
                reachedBarrier = 0;
                recrute.initState();
                notifyAll();
            } else {
                System.out.println("Stationary state has been reached!");
                for (Thread thread : allThreads) {
                    thread.interrupt();
                }
            }
        } else {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
