package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;


public abstract class BluetoothActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        AppBluetoothManager.initialize(this);
    }
}
