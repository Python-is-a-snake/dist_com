package LAB4_A;
import java.io.*;
import java.util.ArrayList;

public class IteratorForFiles {
    private String fileName;
    private ObjectOutputStream output;
    private WriterLocker lock;

    public IteratorForFiles(String fileName) {
        this.fileName = fileName;
        this.lock = new WriterLocker();
        try {
            this.output = new ObjectOutputStream(new FileOutputStream(fileName, true));
            clearFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void write(Writer writer) {
        try {
            lock.writingLock();
            output.writeObject(writer);
            lock.writingUnlock();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Writer search(String key, FieldNames fieldNames) {
        try {
            lock.readingLock();
            FileInputStream inputStream = new FileInputStream(fileName);
            ObjectInputStream input = new ObjectInputStream(inputStream);

            boolean isFound = false;
            Writer answer = new Writer("", "");
            while (!isFound && inputStream.available() > 0) {
                Writer writer = (Writer) input.readObject();
                switch (fieldNames) {
                    case NAME -> {
                        if (writer.getName().equals(key)) {
                            answer = writer;
                            isFound = true;
                        }
                    }
                    case PHONE -> {
                        if (writer.getPhone().equals(key)) {
                            answer = writer;
                            isFound = true;
                        }
                    }
                }
            }
            lock.readingUnlock();
            return answer;
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearFile() throws IOException {
        PrintWriter writer = new PrintWriter(fileName);
        writer.print("");
        writer.close();
        output = new ObjectOutputStream(new FileOutputStream(fileName, true));
    }

    private ArrayList<Writer> getAllItems(FileInputStream fileInputStream, ObjectInputStream input) throws IOException, ClassNotFoundException {
        ArrayList<Writer> array = new ArrayList<>();
        while (fileInputStream.available() > 0) {
            Writer buffer = (Writer) input.readObject();
            array.add(buffer);
        }
        return array;
    }

    public void removeByKey(String key, FieldNames fieldNames) {
        try{
            lock.writingLock();
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream input = new ObjectInputStream(fileInputStream);

            ArrayList<Writer> array = getAllItems(fileInputStream, input);

            int index = -1;
            switch (fieldNames){
                case NAME -> {
                    for (int i = 0; i < array.size(); ++i) {
                        if (array.get(i).getName().equals(key)) {
                            index = i;
                            break;
                        }
                    }
                }
                case PHONE -> {
                    for (int i = 0; i < array.size(); ++i) {
                        if (array.get(i).getPhone().equals(key)) {
                            index = i;
                            break;
                        }}}}

            if(index == -1){
                lock.writingUnlock();
                return;}
            array.remove(index);
            clearFile();
            for (Writer item : array) {
                output.writeObject(item);}
            lock.writingUnlock();} 
            catch (IOException | InterruptedException | ClassNotFoundException e){
            e.printStackTrace();}}

    public void readFile() throws IOException, ClassNotFoundException, InterruptedException {
        lock.readingLock();
        FileInputStream istream = new FileInputStream(fileName);
        ObjectInputStream input = new ObjectInputStream(istream);

        while (istream.available() > 0) {
            Writer buffer = (Writer) input.readObject();
            System.out.println(buffer);
        }
        lock.readingUnlock();
    }
}
