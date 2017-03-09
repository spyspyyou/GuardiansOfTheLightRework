package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import android.util.Log;

public class Handshake extends Message {
    @Override
    protected void onReception() {
        Log.v("Handshake", "-----------------------------------------------------------------------------------------------------------" +
                "\nNothing special, just a handshake, you know...");
    }
}
