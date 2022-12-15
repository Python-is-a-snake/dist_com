package LAB2_B;

public class Ivanov extends Thread {
    public Ivanov(){
        this.setName("Ivanov");
    }
    public void run() {
        while (!interrupted()) {
            Gear gear = Main.storage.poll();
            if (gear == null) {
                Main.storageFinished.set(true);
                break;
            }
            try {
                Thread.sleep(2800);
            } catch (Exception e) {
                break;
            }
            synchronized (Main.outsides) {
                Main.outsides.add(gear);
            }
            System.out.println("Ivanov takes out " + gear.name);
        }
        System.out.println(this.getName() + " finished his work!");
    }
}
