package LAB2_B;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    static int commodityValue;
    static final Queue<Gear> storage = new LinkedList<>();
    static final Queue<Gear> outsides = new LinkedList<>(); // used for synchronization
    static final Queue<Gear> truck = new LinkedList<>();
    static final AtomicBoolean storageFinished = new AtomicBoolean(false);
    static final AtomicBoolean outsidesFinished = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        initStorage();
        Ivanov ivanov = new Ivanov();
        Petrov petrov = new Petrov();
        Nechiporchyk nechiporchyk = new Nechiporchyk();
        ivanov.start();
        petrov.start();
        nechiporchyk.start();
        nechiporchyk.join();
        System.out.println("Value (cost) of all commodity = " + commodityValue);
    }

    public static void initStorage(){
        storage.addAll(
                List.of(
                        new Gear[]{
                                new Gear("AK 47", 230),
                                new Gear("Helmet ULACH", 400),
                                new Gear("Flashlight", 20),
                                new Gear("NVG GEN3", 4500),
                                new Gear("5.11 Tactical \"Hexgrid Plate Carrier\"", 8000),
                                new Gear("Slick Plate Carrier", 6500),
                                new Gear("KRISS Vector .45 ACP", 2000),}
                ));
    }
}

class Gear {
    String name;
    double cost;
    public Gear(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }
}
