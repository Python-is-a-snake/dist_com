package model;

public class Folder {
    public int code;
    public String name;

    public Folder(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String toString() {
        return String.format("Folder{ id=%d, name=\"%s\" }", code, name);
    }
}
