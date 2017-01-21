package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


import android.util.Log;

public abstract class Messenger {

    private final Object[] SEND_VARIABLES;

    //MAC-ADDRESS(es) of the target device(s)
    public Messenger(String eventString){
        SEND_VARIABLES = null;

    }

    public Messenger(Object... sendVariables){
        SEND_VARIABLES = sendVariables;
    }

    protected abstract void onReception();

    public String toString(){
        return getClass().getSimpleName();
    }

    public void send(String[] receptors){
        if (receptors == null){
            Log.w("Messenger", "receptors null");
            return;
        }
        ConnectionManager.EventSenderThread.send(this);
    }

    /*package*/ static Messenger fromEventString(String eventString) throws Exception {
        return null;
    }

    /*package*/ String[] getReceptors(){
        return null;
    }
}
