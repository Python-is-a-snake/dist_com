package LAB3_B;

public class Barbershop {
    public static final int VISITORS = 3;

    public static void main(String[] args) {
        Barber barber = new Barber();
        for (int i = 0; i < VISITORS; ++i) {
            new Thread(new Visitor("Name: Thread-" + i, barber)).start();
        }
    }
}
