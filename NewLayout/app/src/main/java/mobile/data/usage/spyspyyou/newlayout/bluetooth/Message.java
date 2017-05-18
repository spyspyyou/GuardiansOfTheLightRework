package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;

public abstract class Message implements Serializable{

    protected abstract void onReception();

    public void send(@NonNull BluetoothDevice receptor){
        send(new BluetoothDevice[]{receptor});
    }

    public void send(@NonNull BluetoothDevice[] receptors){
        ConnectionManager.send(receptors, this);
    }

    public void send(@NonNull Collection<BluetoothDevice> receptors){
        send(receptors.toArray(new BluetoothDevice[receptors.size()]));
    }
}
