package LAB4_A;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("ENTER FILE NAME: ");
        String fileName = scanner.next();
        IteratorForFiles iteratorForFiles = new IteratorForFiles(fileName);

        Thread managerThread = new Thread(() -> {
            iteratorForFiles.write(new Writer("Mike", "0103010302301"));
            iteratorForFiles.write(new Writer("Joshua", "4223508767"));
            iteratorForFiles.write(new Writer("Bob", "12239492356"));
            iteratorForFiles.removeByKey("Bob", FieldNames.NAME);
            iteratorForFiles.removeByKey("Mike", FieldNames.NAME);
            iteratorForFiles.removeByKey("Dave", FieldNames.NAME);
            iteratorForFiles.removeByKey("0103010302301", FieldNames.PHONE);
            iteratorForFiles.removeByKey("12239492356", FieldNames.PHONE);
        });
        managerThread.start();
        Thread.sleep(2);
    }
}
