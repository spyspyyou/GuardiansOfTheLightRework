public class ObiThread extends ReturnTest1{

    @Override
    public void ma(Class c) {
        super.ma(c);
        if (c == super.getClass())System.out.print("\nstill equal");
    }

}