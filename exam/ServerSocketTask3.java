import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketTask3 {
    private static int port = 0;
    private static ServerSocket server;

    static {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Callback callback = new Callback();

    public static void main(String args[]) throws IOException{
        List<Patient> patients = new ArrayList<>() {
            {
                add(new Patient(1, "Mark", "Zuker",
                        "Johnsons", "Bakery St. 78", "09823468", 8934, "Amnesia"));
                add(new Patient(2, "David", "Backhem",
                        "Addamson", "Inter St. 3", "782365", 1094, "Broken leg"));
                add (new Patient(3, "Melany", "Dead",
                    "Stew", "Love Avenue, 49", "8723521", 4398, "Cough"));
            }
        };
        while(!callback.toFinish){
            System.out.println("<!> WAITING FOR REQUEST FROM CLIENT <!>");
            Socket socket = server.accept();
            PatientIterator iterator = new PatientIterator(socket, callback, patients);
            iterator.run();
        }
        System.out.println("<!> SERVER SHUTS DOWN<!>");
        server.close();
    }
}
class PatientIterator implements Runnable
{
    private Socket socket;
    private Callback callback;
    private List<Patient> patients;

    public PatientIterator(Socket socket, Callback callback, List<Patient> patients)
    {
        this.callback = callback;
        this.socket = socket;
        this.patients = patients;
    }
    public void run() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String message = reader.readLine();
            String splitMessage[] = message.split("#");
            String commandIndex = splitMessage[0];
            System.out.println("TO DO: " + splitMessage[0]);
            if (commandIndex.equals("0"))
            {
                callback.toFinish = true;
                return;
            }
            List<Patient> result = new ArrayList<>();
            switch (commandIndex) {
                case "1" -> {
                    for(Patient patient : patients) {
                        if(patient.getDiagnosis().equals(splitMessage[1])){
                            result.add(patient);
                        }
                    }
                }
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(result);
            inputStreamReader.close();
            objectOutputStream.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class Callback { public boolean toFinish = false;}
