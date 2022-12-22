package LAB5_A;

public class ManagerOfRecrutre extends Thread {
    private int start, end;
    private Recrute recrute;
    private ManagerOfTh manager;

    public ManagerOfRecrutre(int start, int end, Recrute recrute, ManagerOfTh manager) {
        this.start = start;
        this.end = end;
        this.recrute = recrute;
        this.manager = manager;
    }

    public void run() {
        while (!isInterrupted()) {
            recrute.partFixing(start, end);
            manager.barrierIncrementation();
        }
    }
}
