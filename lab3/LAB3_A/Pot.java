package LAB3_A;

public class Pot {
    private int maxHoneyAmount;
    private int honeyAmount;
    private boolean filled;

    Pot(int maxHoneyAmount){
        this.maxHoneyAmount = maxHoneyAmount;
        this.honeyAmount = 0;
        this.filled = false;

    }
    public synchronized void addHoney(){
        if(honeyAmount >= maxHoneyAmount){
            System.out.println("<!> POT IS FULL <!>");
            filled = true;
            return;
        }
        honeyAmount++;
        System.out.println("HONEY LEFT: "+ honeyAmount +" BEE: "+Thread.currentThread().getName());
    }
    public void free(){
        honeyAmount = 0;
        System.out.println("NO MORE HONEY ");
    }
    public synchronized boolean isFilled(){
        return filled;
    }
}
