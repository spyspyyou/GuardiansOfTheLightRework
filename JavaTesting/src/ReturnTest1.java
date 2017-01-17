public class ReturnTest1 extends Thread{

    public static void main(String args[]){
        new ObiThread().ma(ObiThread.class);
    }

    public void ma(Class c){
        if (c == this.getClass())System.out.print("is equal");
        else System.out.print("different");
    }
}
