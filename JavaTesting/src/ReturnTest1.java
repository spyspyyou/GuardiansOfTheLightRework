public class ReturnTest1 extends Thread{

    Obiekt o = new Obiekt();

    public static void main(String args[]){
        new ReturnTest1().ma();
    }

    public void ma(){
        o.a = 1;
        new ObiThread().setReturnTest(this).start();
        o = new Obiekt();
        test();
    }

    public void test(){
        System.out.print(o.a);
    }

}
