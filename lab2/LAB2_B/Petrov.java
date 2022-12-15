package LAB2_B;

public class Petrov extends Thread {
    public Petrov(){
        this.setName("Petrov");
    }
    public void run() {
        while (!interrupted()) {
            Gear gear;
            synchronized (Main.outsides) {
                gear = Main.outsides.poll();
            }
            if (gear == null) {
                if (!Main.storageFinished.get()) {
                    continue;
                } else {
                    Main.outsidesFinished.set(true);
                    break;
                }
            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                break;
            }
            synchronized (Main.truck) {
                Main.truck.add(gear);
            }
            System.out.println("Petrov puts inside the truck " + gear.name);
        }
        System.out.println(this.getName()+"  finished his work!");
    }
}
