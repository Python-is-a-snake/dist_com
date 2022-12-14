import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Solution_B {
    private static final JSlider slider = new JSlider();
    private static MyTThread myTThread1;
    private static MyTThread myTThread2;

    public static void main(String[] args) {
        initJFrame();
    }
    private static AtomicInteger semaphore = new AtomicInteger(0);
    private static void initJFrame() {
        JFrame frame = new JFrame("SOLUTION_LAB1");
        slider.setBounds(40, 40 ,420, 40);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(5);
        //
        JLabel textMessage = new JLabel("<!> Critical section taken <!>");
        textMessage.setBounds(180, 100, 200,20);
        textMessage.setVisible(false);
        //
        JButton th1StartButton = new JButton("TH1 START");
        th1StartButton.setBounds(60, 150,100, 50);
        JButton th1StopButton = new JButton("TH1 STOP");
        th1StopButton.setBounds(60, 210,100, 50);
        th1StopButton.setEnabled(false);
        //
        JButton th2StartButton = new JButton("TH2 START");
        th2StartButton.setBounds(335, 150,100, 50);
        JButton th2StopButton = new JButton("TH2 STOP");
        th2StopButton.setBounds(335, 210,100, 50);
        th2StopButton.setEnabled(false);
        //
        th1StopButton.addActionListener(actionListener -> {
            th1StopButton.setEnabled(false);
            th1StartButton.setEnabled(true);
            th2StartButton.setEnabled(true);
            textMessage.setVisible(false);
            myTThread1.interrupt();});
        th2StopButton.addActionListener(actionListener -> {
            th2StopButton.setEnabled(false);
            th1StartButton.setEnabled(true);
            th2StartButton.setEnabled(true);
            textMessage.setVisible(false);
            myTThread2.interrupt();});

        th1StartButton.addActionListener(actionListener -> {
            th1StopButton.setEnabled(true);
            th1StartButton.setEnabled(false);
            th2StartButton.setEnabled(false);
            th2StopButton.setEnabled(false);
            textMessage.setVisible(true);
            myTThread1 = new MyTThread(slider, -1, semaphore);
            myTThread1.start();});
        th2StartButton.addActionListener(actionListener -> {
            th2StopButton.setEnabled(true);
            th2StartButton.setEnabled(false);
            th1StartButton.setEnabled(false);
            th1StopButton.setEnabled(false);
            textMessage.setVisible(true);
            myTThread2 = new MyTThread(slider, 1, semaphore);
            myTThread2.start();});

        frame.add(slider);
        frame.add(th1StartButton);
        frame.add(th1StopButton);
        frame.add(th2StartButton);
        frame.add(th2StopButton);
        frame.add(textMessage);

        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
class MyTThread extends Thread {
    private final AtomicInteger semaphore;
    private final JSlider slider;
    private final int step;
    public MyTThread(JSlider slider, int step, AtomicInteger semaphore) {
        this.slider = slider;
        this.step = step;
        this.semaphore = semaphore;
    }
    public void run() {
        if (!semaphore.compareAndSet(0, 1)){
            System.out.println("<!> EXITING <!>");
            return;
        }
        boolean marker = (slider.getValue() == 10 && step > 0) || (slider.getValue() == 90 && step<0);
        while(!isInterrupted()) {
            try {
                sleep(50);
                int valueSlide = slider.getValue();
                if (marker || (valueSlide > 10 && valueSlide < 90)) {
                    slider.setValue(valueSlide + step);
                }
            } catch (Exception e) {
                System.out.println("<!>" + Thread.currentThread().getName()+" WAS INTERRUPTED <!> ");
                //e.printStackTrace();
                interrupt();
                break;
            }}
        semaphore.set(0);
    }
}
