package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.UUID;

import testing.gotl.spyspyyo.bluetoothtesting.App;
import testing.gotl.spyspyyo.bluetoothtesting.AppEvents;
import testing.gotl.spyspyyo.bluetoothtesting.TODS;

public class BluetoothManagerIntern implements TODS, AppEvents{
    private static BluetoothAdapter bA;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver;

    public void onAppStart(){
        bA = BluetoothAdapter.getDefaultAdapter();
        if (bA == null)handleNonBluetoothDevice();
        BluetoothBroadcastReceiver.setupReceiver();
        if (startBluetoothOnAppStart)enableBluetooth();
    }

    public static void enableBluetooth(){
        if(bA.isEnabled())return;
        bA.enable();
    }

    private static void disableBluetooth(){
        if (!bA.isEnabled())return;
        bA.disable();
        destroyReceiver();
    }

    public static void onAppLeave(){
        disableBluetooth();
    }

    private static void handleNonBluetoothDevice(){
        new AlertDialog.Builder(App.accessActiveActivity(null))
                .setTitle("Bluetooth required")
                .setMessage("Unfortunately our app does not work on devices without bluetooth.")
                .setPositiveButton("exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        App.accessActiveActivity(null).finish();
                    }
                })
                .show();
    }

    private static void destroyReceiver(){
        App.accessActiveActivity(null).unregisterReceiver(bluetoothBroadcastReceiver);
    }

    public static class BluetoothBroadcastReceiver extends BroadcastReceiver {
        private final int INVALID_STATE = -1;

        private static String[] FILTER_ACTIONS = {
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothAdapter.ACTION_DISCOVERY_STARTED,
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED,
                BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED
        };

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    onStateChanged(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    onDiscoveryStart(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    onDiscoveryFinish(intent);
                    break;
                case BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED:
                    onNameChange(intent);
                    break;
                default:
                    Log.i("BBReceiver", "Received an unidentifiable Intent");
            }
        }

        private void onStateChanged(Intent intent){
            int extra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, INVALID_STATE);
            switch (extra){
               case BluetoothAdapter.STATE_ON:
                    break;
                case BluetoothAdapter.STATE_OFF:
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                default:
                    Log.e(this.toString(), "Received an invalid Bluetooth state changed Intent.");
            }
        }

        private void onDiscoveryStart(Intent intent){

        }

        private void onDiscoveryFinish(Intent intent){

        }

        private void onNameChange(Intent intent){

        }

        private static void setupReceiver(){
            IntentFilter intentFilter = new IntentFilter();
            for (String action:FILTER_ACTIONS) {
                intentFilter.addAction(action);
            }
            bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
            App.accessActiveActivity(null).registerReceiver(bluetoothBroadcastReceiver, intentFilter);
        }
    }

}
