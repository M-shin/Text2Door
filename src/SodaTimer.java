
import java.io.InputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author S570837
 */
public class SodaTimer implements Runnable {

    InputStream is;
    boolean completed;

    public SodaTimer(InputStream is) {
        this.is = is;
        completed = false;

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
            if (!completed) {
                System.setIn
            }
        } catch (Exception e) {
        }
    }

    public void done() {
        completed = true;
    }
}
