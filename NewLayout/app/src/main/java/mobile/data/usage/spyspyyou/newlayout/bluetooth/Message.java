package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;

public abstract class Message implements Serializable{

    protected abstract void onReception();

    public void send(@NonNull String receptor){
        send(new String[]{receptor});
    }

    public void send(@NonNull String[] receptors){
        ConnectionManager.send(receptors, this);
    }

    public void send(@NonNull Collection<String> receptors){
        send(receptors.toArray(new String[receptors.size()]));
    }
}
