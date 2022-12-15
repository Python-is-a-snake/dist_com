package LAB2_B;

public class Nechiporchyk extends Thread {
    public Nechiporchyk(){
        this.setName("Nechiporchyk");
    }
    public void run() {
        while (!interrupted()) {
            Gear gear;
            synchronized (Main.truck) {
                gear = Main.truck.poll();
            }
            if (gear == null) {
                if (!Main.outsidesFinished.get()) {
                    continue;
                } else {
                    break;
                }
            }
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                break;
            }
            Main.commodityValue += gear.cost;
            System.out.println("Nechiporchyk counts value of " + gear.name + ".It costs " + gear.cost);
        }
        System.out.println(this.getName()+" finished his work!");
    }
}
