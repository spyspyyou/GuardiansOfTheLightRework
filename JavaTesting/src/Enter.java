import static java.lang.Thread.sleep;

public class Enter {

    public static void main(String args[]){
        new Enter().test();
    }

    public void test(){
        ObiThread obiThread = new ObiThread();
        obiThread.start();
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        obiThread.start();
    }
}
