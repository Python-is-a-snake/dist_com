package LAB3_A;

public class Semaphore {
    private boolean state; //TRUE - FALSE

    Semaphore(){
        this.state = false;
    }
    public void makeTrue(){
        state = true;
    }
    public boolean getState(){
        return state;
    }
    public void makeFalse(){
        state = false;
    }
}
