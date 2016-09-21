package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import testing.gotl.spyspyyo.bluetoothtesting.global.App;
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

    /**
     * this method directly returns info about connected games and then searches for further games
     * triggering a ui method when finding one
     */
    public static ArrayList<BluetoothDevice> getAvailableGames(){
        enableBluetooth();
        bA.startDiscovery();
        return ConnectionManager.getGameHostingDevices();
    }

    public static void getAvailableAppUsers(){
        enableBluetooth();
    }

    public static void updateFriendsStatus(){
        enableBluetooth();
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

    private static void setBluetoothName(){
        bA.setName(playerName + "_" + ((hostingGame)?"1-"+gameName:0));
    }

    public static BluetoothServerSocket getBluetoothServerSocket(UUID uuid) throws IOException{
        return bA.listenUsingRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    public static BluetoothServerSocket getInsecureBluetoothServerSocket(UUID uuid) throws IOException{
        return bA.listenUsingInsecureRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    public static class BluetoothBroadcastReceiver extends BroadcastReceiver {
        private final int INVALID_STATE = -1;

        private static String[] FILTER_ACTIONS = {
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothAdapter.ACTION_DISCOVERY_STARTED,
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED,
                BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED,
                BluetoothDevice.ACTION_FOUND
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
                case BluetoothDevice.ACTION_FOUND:
                    onDeviceFound(intent);
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
                    setBluetoothName();
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
            if (bluetoothState == BLUETOOTH_STATES.DISABLED)return;
            setBluetoothName();
            App.toast("BluetoothName was changed to '"+intent.getStringExtra(BluetoothAdapter.EXTRA_LOCAL_NAME)+"' by an unknown source. To assure App functionality, the name was changed back to its original string.");
        }

        private void onDeviceFound(Intent intent){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // give the device to the UI requesting the info
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
