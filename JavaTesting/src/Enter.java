import java.io.*;

public class Enter extends Thread {

    /*public static void main(String args[]){
        new  Enter().start();
    }*/

    @Override
    public void run() {
        PipedInputStream is = new PipedInputStream();
        PipedOutputStream os = null;
        ObjectInputStream oi = null;
        ObjectOutputStream oo = null;
        try {
            os = new PipedOutputStream(is);
            oo = new ObjectOutputStream(os);
            oo.flush();
            oi = new ObjectInputStream(is);

            System.out.println("writing object");
            oo.writeObject(new SenderThread());
            System.out.println("wrote object, now reading");
            new ReceiverThread(this).start();
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            oi.readObject();

            System.out.println("didn't get interrupted");
        }
        catch (IOException e) {
            e.printStackTrace();
            try {
                System.out.print(oi.readObject());} catch (IOException e1) {e1.printStackTrace();} catch (ClassNotFoundException e1) {e1.printStackTrace();}
        } catch (ClassNotFoundException e) {e.printStackTrace();}
    }
}
