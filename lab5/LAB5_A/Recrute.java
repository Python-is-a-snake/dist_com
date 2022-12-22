package LAB5_A;
import java.util.Random;

public class Recrute {
    private int numberOfRecruits;
    private Boolean[] recruits;
    private Boolean[] previousState;
    public Recrute(int numberOfRecruits) {
        this.numberOfRecruits = numberOfRecruits;
        this.recruits = new Boolean[numberOfRecruits];
        this.previousState = new Boolean[numberOfRecruits];
        recrutStatesInint();
        initState();
    }

    public void initState() {
        for(int i = 0;i<numberOfRecruits;++i){
            previousState[i] = recruits[i];
        }
    }
    public Boolean[] getRecruts() {
        return recruits;
    }

    public void recrutStatesInint() {
        Random random = new Random();
        for (int i = 0; i < numberOfRecruits; ++i) {
            recruits[i] = random.nextBoolean();
        }
    }
    public synchronized boolean beingStat() {
        for(int i = 0;i<numberOfRecruits;++i){
            if(recruits[i] != previousState[i]){
                return false;
            }
        }
        return true;
    }
    public void partFixing(int begin, int end) {
        for (int i = begin; i < end - 1; ++i) {
            if (recruits[i] && !recruits[i + 1]) {
                recruits[i] = false;
                recruits[i + 1] = true;
            }
        }
        if (end != recruits.length) {
            if (recruits[end - 1] && !recruits[end]) {
                recruits[end - 1] = false;
                synchronized (recruits[end]){
                    recruits[end] = true;
                }
            }
        }
    }

    public void recrutePrint() {
        System.out.print("**[ ");
        for (int i = 0; i < numberOfRecruits; ++i) {
            System.out.print(recruits[i] ? "->" : "<-");
        }
        System.out.println(" ]**");
    }
}
