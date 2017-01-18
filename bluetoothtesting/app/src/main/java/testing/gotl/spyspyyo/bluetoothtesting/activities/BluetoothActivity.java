package testing.gotl.spyspyyo.bluetoothtesting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.AppBluetoothManager;

public abstract class BluetoothActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        AppBluetoothManager.initialize(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //todo:report to the AppBTManager
    }
}
