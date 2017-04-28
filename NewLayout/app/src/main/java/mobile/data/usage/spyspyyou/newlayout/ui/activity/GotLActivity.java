package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.BluetoothActivity;


public abstract class GotLActivity extends BluetoothActivity {

    private static Resources REC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        REC = getResources();
    }

    public static Resources getRec(){
        return REC;
    }
}
