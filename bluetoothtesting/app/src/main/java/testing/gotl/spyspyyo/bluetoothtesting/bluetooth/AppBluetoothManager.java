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
import java.util.UUID;

import testing.gotl.spyspyyo.bluetoothtesting.global.App;
import testing.gotl.spyspyyo.bluetoothtesting.global.GlobalTrigger;
import testing.gotl.spyspyyo.bluetoothtesting.global.TODS;

public class AppBluetoothManager implements TODS, GlobalTrigger {

    // bluetooth states
    private static final short DISABLED = 0;
    private static final short ENABLED = 1;
    private static final short DISCOVERABLE = 2;

    private static int bluetoothState = DISABLED;
    private static BluetoothAdapter bluetoothAdapter;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver;

    // start of all the outside access method that the app has onto the bluetooth sector.

    //Global Trigger methods

    public void onAppStart(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)handleNonBluetoothDevice();
        if (startBluetoothOnAppEntering)enableBluetooth();
    }

    public void onAppResume(){
        if(bluetoothAdapter.isEnabled()){
            BluetoothBroadcastReceiver.setupReceiver();
            setBluetoothName();
        }
    }

    public void onAppStop(){
        if (stopBluetoothOnAppLeaving)disableBluetooth();
        else unregisterReceiver();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_START_DISCOVERABLE && resultCode == Activity.RESULT_CANCELED){
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

    //public access methods

    public static void getClientList(){

    }

    public static void getServerLIst(){

    }

    public static void updateFriendsStatus(){

    }

    public static void connectTo(){

    }

    public static void disconnectFrom(){

    }

    public static void blackList(){

    }

    public static void whiteList(){

    }

    //Intern methods

    private static void enableBluetooth(){
        if(bluetoothAdapter.isEnabled()&& bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)return;
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        App.accessActiveActivity(null).startActivityForResult(intent, REQUEST_START_DISCOVERABLE);
    }

    private static void disableBluetooth(){
        bluetoothAdapter.disable();
        unregisterReceiver();
    }

    private static void bluetoothDisabling(){
        //todo:handle the problem
    }

    private static void handleNonBluetoothDevice(){
        new AlertDialog.Builder(App.accessActiveActivity(null))
                .setTitle("Bluetooth required")
                .setMessage("Unfortunately our App does not work on devices without a Bluetooth Function.")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
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

    //todo:adjust
    private static void setBluetoothName(){
        bluetoothAdapter.setName(playerName + "_" + ((hostingGame)?"1-"+gameName:0));
    }

    public static BluetoothServerSocket getBluetoothServerSocket(UUID uuid) throws IOException{
        return bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    public static class BluetoothBroadcastReceiver extends BroadcastReceiver {
        private static final int INVALID_STATE = -1;

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
                    Log.w("BBReceiver", "Received an unidentifiable Intent");
            }
        }

        private void onStateChanged(Intent intent){
            int extra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, INVALID_STATE);
            switch (extra){
                case BluetoothAdapter.STATE_ON:
                    if (bluetoothState!=DISCOVERABLE)bluetoothState = ENABLED;
                    setBluetoothName();
                    break;
                case BluetoothAdapter.STATE_OFF:
                case BluetoothAdapter.STATE_TURNING_ON:
                case BluetoothAdapter.STATE_TURNING_OFF:
                    bluetoothState = DISABLED;
                    if (App.isActive())bluetoothDisabling();
                    break;
            }
        }

        private void onDiscoveryStart(Intent intent){

        }

        private void onDiscoveryFinish(Intent intent){

        }

        private void onNameChange(Intent intent){
            if (bluetoothState == DISABLED)return;
            setBluetoothName();
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
