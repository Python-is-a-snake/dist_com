import java.io.Serializable;

public class Patient implements Serializable, Comparable<Patient> {
    private int id;
    private String name;
    private String surname;
    private String middleName;
    private String address;
    private String phoneNumber;
    private int medBookNumber;
    private String diagnosis;

    public Patient(int id, String name, String surname, String middleName, String address, String phoneNumber, int medBookNumber, String diagnosis) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.medBookNumber = medBookNumber;
        this.diagnosis = diagnosis;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setMedBookNumber(int medBookNumber) {
        this.medBookNumber = medBookNumber;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMiddleName() {
        return middleName;
    }

    public int getMedBookNumber() {
        return medBookNumber;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int compareTo(Patient o) {
        return this.id;
    }
}
