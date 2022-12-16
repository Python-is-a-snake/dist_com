import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientSocketTask3 {
    private static int port = 0;

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
        InetAddress host = InetAddress.getLocalHost();
        Socket socket;
        ObjectOutputStream objectOutputStream;
        ObjectInputStream objectInputStream;
        Scanner scanner = new Scanner(System.in);
        while (true)
        {
            System.out.println("WHAT TO DO:\n" +
                    "1) Find patient with diagnosis\n" +
                    "2) Find patients with med card interval\n" +
                    "0) Exit");
            socket = new Socket(host.getHostName(), port);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("<!> REQUESTING TO SOCKET SERVER <!>");
            int commandIndex = Integer.parseInt(scanner.next());
            if (commandIndex == 0)
            {
                socket = new Socket(host.getHostName(), port);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("<!> REQUESTING CLOSING <!>");
                objectOutputStream.writeInt(commandIndex); objectOutputStream.flush();
                break;
            }
            switch (commandIndex) {
                case 1 -> {
                    String message = "" + commandIndex;
                    objectOutputStream.writeBytes(message);
                    objectOutputStream.flush();

                }
                default -> System.out.println("<!> WRONG INPUT <!>");
            }
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<Patient> results = (ArrayList<Patient>) objectInputStream.readObject();
            System.out.println("_-_-_ OUTPUT _-_-_");
            for (Patient patient : results)
            {
                System.out.println(patient);
            }
            objectInputStream.close();
            objectOutputStream.close();
            Thread.sleep(250);
        }
        objectOutputStream.writeInt(0);
        System.out.println("<!> SHUTTING DOWN <!>");
    }
}
