public class ReturnTest1 extends Thread{
    static boolean a = false;
    static boolean b = true;

    public static void main(String args[]){
        System.out.println(a!=(a=false));
        System.out.println(b!=(b=false));
    }
}
