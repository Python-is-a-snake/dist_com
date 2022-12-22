package LAB5_B;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        StringChecker stringChecker = new StringChecker();
        CyclicBarrier barrier = new CyclicBarrier(4, stringChecker);
        for (int i = 0; i < 4; i++) {
            String str= initString();
            System.out.println(str);
            StringChanger stringChanger =new StringChanger(str, barrier);
            stringChecker.addChanger(stringChanger);
            Thread thread = new Thread(stringChanger);
            thread.start();
        }
    }
    public static String initString(){
        int len = 7;
        StringBuilder str=new StringBuilder();
        for (int i = 0; i < len; i++) {
            char randomChar = (char)(((int)(Math.random()*4))+(int)'A');
            str.append(randomChar);
        }
        return str.toString();
    }
}
