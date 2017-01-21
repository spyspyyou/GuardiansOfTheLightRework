package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;

public abstract class Messenger {
    private static final char SEPARATION_CHAR = '\n';

    private final Pair<String, Object>[] SEND_VARIABLES;
    private String[] receptors;

    //MAC-ADDRESS(es) of the target device(s)
    public Messenger(String eventString){
        receptors = null;

        SEND_VARIABLES = null;
    }

    @SafeVarargs
    public Messenger(@Nullable String[]receptors, Pair<String, Object>... sendVariables){
        this.receptors = receptors;
        SEND_VARIABLES = sendVariables;
    }

    protected abstract void onReception();

    /*package*/ String getMessageString(){
        String messageString = getClass().getSimpleName();
        return messageString;
    }

    public void send(){
        if(receptors == null){
            Log.w("Messenger", "tried sending to null receptors");
            return;
        }
        ConnectionManager.EventSenderThread.send(this);
    }

    public void send(String[]receptors){
        this.receptors = receptors;
        send();
    }

    /*package*/ static Messenger fromMessageString(String messageString) {
        try {
            int firstSepPos = messageString.indexOf(SEPARATION_CHAR);
            Class c = Class.forName(messageString.substring(0, firstSepPos));
            return (Messenger) c.getConstructor(String.class).newInstance(messageString);
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid Message String");
        }
    }

    /*package*/ String[] getReceptors(){
        return receptors;
    }
}
