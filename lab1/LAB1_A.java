import javax.swing.*;
public class Solution_A {
    private static final JSlider slider = new JSlider();
    private static MyThread myThread1 = new MyThread(slider, -1);
    private static MyThread myThread2 = new MyThread(slider, 1);

    public static void main(String[] args) {
        initJFrame();
    }
    
    private static void initJFrame() {
        JFrame frame = new JFrame("SOLUTION_LAB1");
        slider.setBounds(40, 40 ,420, 80);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(5);
        JButton startButton = new JButton("RUN");
        startButton.setBounds(200, 260,100, 100);
        startButton.addActionListener(actListener -> {
            myThread1.start();
            myThread2.start();
            startButton.setEnabled(false);});
        SpinnerNumberModel spinModel1 = new SpinnerNumberModel(0, 0, 5, 1);
        JSpinner firstSpinner = new JSpinner(spinModel1);
        firstSpinner.setBounds(80, 180, 100, 50);
        firstSpinner.addChangeListener(listener -> {
            int priority = (int) firstSpinner.getValue();
            myThread1.setPriority(priority);});
        SpinnerModel spinModel2 = new SpinnerNumberModel(0, 0, 5, 1);
        JSpinner secondSpinner = new JSpinner(spinModel2);
        secondSpinner.setBounds(320, 180, 100, 50);
        secondSpinner.addChangeListener(listener -> {
            int priority = (int) secondSpinner.getValue();
            myThread2.setPriority(priority);});

        frame.add(slider);
        frame.add(startButton);
        frame.add(firstSpinner);
        frame.add(secondSpinner);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

}
class MyThread extends Thread {
    private final JSlider slider;
    private volatile int step;
    public MyThread(JSlider slider, int step) {
        this.slider = slider;
        this.step = step;
    }
    public void run() {
        while(!isInterrupted()){
            synchronized (slider) {
                try {
                    sleep(50);
                    if(slider.getValue() > 10 && slider.getValue() < 90) {
                        slider.setValue(slider.getValue() + step);
                    } else {
                        interrupt();
                    }
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted!"+Thread.currentThread().getName());
                }}
        }
        System.out.println("Thread finished!"+Thread.currentThread().getName());
    }
}
