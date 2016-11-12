public class ObiThread extends Thread{
    ReturnTest1 returnTest1;
    @Override
    public void run() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        returnTest1.o.a = 10;
    }

    public ObiThread setReturnTest(ReturnTest1 r){
        returnTest1 = r;
        return this;
    }
}
