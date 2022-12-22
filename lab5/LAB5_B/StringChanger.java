package LAB5_B;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class StringChanger implements Runnable {
    private String str;
    private final CyclicBarrier barrier;
    private static boolean isAlive = true;

    public StringChanger(String str, CyclicBarrier barrier){
        this.str = str;
        this.barrier = barrier;
    }
    public String getStr() {
        return str;
    }
    public void run(){
        while(isAlive) {
            int randIndex = (int)(str.length()*Math.random());
            switch (str.charAt(randIndex)) {
                case 'A':
                    str=setChar(randIndex, 'C');
                    break;
                case 'B':
                    str=setChar(randIndex, 'D');
                    break;
                case 'C':
                    str=setChar(randIndex, 'A');
                    break;
                case 'D':
                    str=setChar(randIndex, 'B');
                    break;
            }

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {}
        }
    }
    public String setChar(int index, char newChar){
        StringBuffer strBuffer=(new StringBuffer(str));
        strBuffer.setCharAt(index, newChar);
        return strBuffer.toString();
    }
    public static void stopChanging(){
        isAlive=false;
    }
}
