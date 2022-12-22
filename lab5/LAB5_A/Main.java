package LAB5_A;

public class Main {
    public static void main(String[] args){
        Recrute recrute = new Recrute(250);
        recrute.recrutePrint();
        ManagerOfTh manager = new ManagerOfTh(recrute, 20);
    }
}
