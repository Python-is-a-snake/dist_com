package LAB5_B;
import java.util.*;

public class StringChecker implements Runnable {
    private List<StringChanger> stringChangers = new ArrayList<>();

    public void addChanger(StringChanger stringChanger) {
        this.stringChangers.add(stringChanger);
    }

    public void run() {
        Set<Long> set = new HashSet<>();
        for (StringChanger currentStringChanger : stringChangers) {
            String str= currentStringChanger.getStr();
            long n =(str.chars().map(t->(char)t).filter(t->t=='A'||t=='B').count());
            System.out.println(str+" "+n);
            set.add(n);
        }
        System.out.println();
        if (set.size()<=2){
            StringChanger.stopChanging();
        }
    }
}
