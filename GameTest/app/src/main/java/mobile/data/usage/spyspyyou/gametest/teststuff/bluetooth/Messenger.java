package mobile.data.usage.spyspyyou.gametest.teststuff.bluetooth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.events.GameEvent;

public abstract class Messenger {

    private static final char SEPARATION_CHAR = '\1';
    public static final char SUB_SEPARATION_CHAR = '\2';

    private final Map<String, Object> OBJECTS = new HashMap<>();
    protected String[] receptors;

    boolean corrupted = false;

    public Messenger(String message) {
        receptors = null;
        int sepIndex;
        Class<?> c;
        Object obj;
        String key;
        synchronized (OBJECTS){
            try {
                while (!message.isEmpty()) {
                    sepIndex = message.indexOf(SEPARATION_CHAR);
                    c = Class.forName(message.substring(0, sepIndex));
                    message = message.substring(++sepIndex);

                    sepIndex = message.indexOf(SEPARATION_CHAR);
                    key = message.substring(0, sepIndex);
                    message = message.substring(++sepIndex);

                    sepIndex = message.indexOf(SEPARATION_CHAR);
                    obj = c.getConstructor(String.class).newInstance(message.substring(0, sepIndex));
                    message = message.substring(++sepIndex);

                    OBJECTS.put(key, obj);
                }
            }catch (Exception e){
                e.printStackTrace();
                corrupted = true;
                Log.e("Messenger", "message could not be read: " + message);
            }
        }
    }

    public Messenger(@Nullable String[]receptors){
        this.receptors = receptors;
    }

    /*package*/ void received(){
        if (!corrupted)onReception();
    }

    protected abstract void onReception();

    public void clearObjects(){
        synchronized (OBJECTS) {
            OBJECTS.clear();
        }
    }

    public void putObject(String key, Object object){
        synchronized (OBJECTS) {
            OBJECTS.put(key, object);
        }
    }

    @Nullable
    protected Object getObject(String key) {
        return OBJECTS.get(key);
    }

    protected boolean getBoolean(String key){
        Object o = getObject(key);
        if (o instanceof Boolean)
            return (boolean) o;
        return false;
    }

    protected int getInt(String key){
        Object o = getObject(key);
        if (o instanceof Integer)
            return (int) o;
        return -1;
    }

    protected double getDouble(String key){
        Object o = getObject(key);
        if (o instanceof Double)
            return (double) o;
        return -1;
    }

    protected String getString(String key){
        Object o = getObject(key);
        if (o instanceof String)
            return (String) o;
        return "";
    }

    /*package*/ String getMessageString(){
        String messageString = getClass().getSimpleName();
        synchronized (OBJECTS) {
            for (String key : OBJECTS.keySet()) {
                messageString += OBJECTS.get(key).getClass().getSimpleName() + SEPARATION_CHAR;
                messageString += key + SEPARATION_CHAR;
                messageString += "" + OBJECTS.get(key) + SEPARATION_CHAR;
            }
        }
        return messageString;
    }

    /**
     * you are encouraged to override this to clear/put the objects or set receptors before calling super.send();
     */
    public void send(){
        if(receptors == null){
            Log.w("Messenger", "tried sending to null receptors");
            return;
        }
        //todo: temporary
        Game.addEvent((GameEvent) this);
        // ConnectionManager.EventSenderThread.send(this);
    }

    @NonNull
    /*package*/ static Messenger fromMessageString(String message) throws InvalidMessageException {
        try {
            int sepPos = message.indexOf(SEPARATION_CHAR);
            Class<Messenger> c = (Class<Messenger>) Class.forName(message.substring(0, sepPos));
            return c.getConstructor(String.class).newInstance(message.substring(sepPos + 1));
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidMessageException("Invalid Message String");
        }
    }

    /*package*/ String[] getReceptors(){
        return receptors;
    }

    public static class InvalidMessageException extends Exception {
        public InvalidMessageException(String message){
            super(message);
        }
    }
}
