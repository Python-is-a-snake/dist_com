package LAB4_A;
import java.io.Serializable;
import java.util.StringJoiner;
public class Writer implements Serializable {
    private String name;
    private String phone;

    public Writer(String name, String phone){
        this.name = name;
        this.phone = phone;
    }
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }

    public boolean equals(Writer other){
        return this.name.equals(other.name) && this.phone.equals(other.phone);
    }

    public String toString() {
        return new StringJoiner(", ", Writer.class.getSimpleName() + " -> ", "<-").add(name).add(phone).toString();
    }
}
