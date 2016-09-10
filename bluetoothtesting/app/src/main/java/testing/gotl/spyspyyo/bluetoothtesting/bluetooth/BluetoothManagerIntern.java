package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import testing.gotl.spyspyyo.bluetoothtesting.UI.App;
import testing.gotl.spyspyyo.bluetoothtesting.global.GlobalTrigger;
import testing.gotl.spyspyyo.bluetoothtesting.global.TODS;

public class BluetoothManagerIntern implements TODS, GlobalTrigger {
    private enum BLUETOOTH_STATES{
        DISABLED,
        ENABLED,
        DISCOVERABLE
    }
    private static BLUETOOTH_STATES bluetoothState = BLUETOOTH_STATES.DISABLED;
    private static BluetoothAdapter bA;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver;

    // start of all the outside access method that the app has onto the bluetooth sector.


    //active methods

    public static void getAvailableGames(){

    }

    public static void getAvailableAppUsers(){

    }

    public static void updateFriendsStatus(){

    }

    //trigger methods

    public void onAppStart(){
        bA = BluetoothAdapter.getDefaultAdapter();
        if (bA == null)handleNonBluetoothDevice();
        if (startBluetoothOnAppEntering)enableBluetooth();
    }

    public void onAppResume(){
        BluetoothBroadcastReceiver.setupReceiver();
        //TODO - reminder with activation button for bluetooth
    }

    public void onAppStop(){
        if (stopBluetoothOnAppLeaving)disableBluetooth();
        unregisterReceiver();
    }

    public void onActivityResult(int resultCode, Intent data){
        if (resultCode == Activity.RESULT_CANCELED){
            switch (bluetoothState){
                case DISABLED:
                    App.toast("Bluetooth was not enabled.");
                    break;
                case ENABLED:
                    App.toast("Discoverable was not enabled.");
                    break;
                case DISCOVERABLE:
                    App.toast("If this is displayed, I failed. ^^");
                    break;
            }
        }
    }

    // end of the outside access methods

    public static void enableBluetooth(){
        if(bA.isEnabled()&& bA.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)return;
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        App.accessActiveActivity(null).startActivityForResult(intent, REQUEST_START_DISCOVERABLE);
    }

    private static void disableBluetooth(){
        if (!bA.isEnabled())return;
        bA.disable();
        unregisterReceiver();
    }

    private static void badBluetoothDisabling(){
        if (alertBluetoothTurnedOff){
            new AlertDialog.Builder(App.accessActiveActivity(null))
                    .setTitle("Bluetooth required")
                    .setMessage("Without bluetooth turned on, you can't be invited to games.")
                    .setPositiveButton("re-enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enableBluetooth();
                        }
                    }).setNegativeButton("Understood", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
            }).show();
        }
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

    private static void unregisterReceiver(){
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
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
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
                   if (bluetoothState!=BLUETOOTH_STATES.DISCOVERABLE)bluetoothState = BLUETOOTH_STATES.ENABLED;
                    break;
                case BluetoothAdapter.STATE_OFF:
                    bluetoothState = BLUETOOTH_STATES.DISABLED;
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    bluetoothState = BLUETOOTH_STATES.DISABLED;
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    bluetoothState = BLUETOOTH_STATES.DISABLED;
                    //todo:only call when the app is in a bluetooth requiring state
                    if (App.isActive())badBluetoothDisabling();
                    break;
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
