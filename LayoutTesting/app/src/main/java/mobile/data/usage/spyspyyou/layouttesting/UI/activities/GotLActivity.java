package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.global.App;

public abstract class GotLActivity extends AppCompatActivity {

    protected static boolean activeActivityRequiresServer = false;

    @Override
    protected void onStart() {
        super.onStart();
        App.onActivityStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.onActivityResumed();
        AppBluetoothManager.serverRequirementChanged(activeActivityRequiresServer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.onActivityStopped();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        App.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        App.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static boolean isActiveActivityRequiresServer(){
        return activeActivityRequiresServer;
    }
}
