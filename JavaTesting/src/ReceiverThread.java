import java.io.ObjectInputStream;

public class ReceiverThread extends Thread {

    Enter e;

    public ReceiverThread(Enter e){
        this.e = e;
    }

    @Override
    public void run() {
        e.interrupt();
    }
}
