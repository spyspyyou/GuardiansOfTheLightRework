package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;


public abstract class GotLActivity extends AppCompatActivity {

    private static Resources REC;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        REC = getResources();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        AppBluetoothManager.initialize(this);
    }

    public static Resources getRec(){
        return REC;
    }
}
