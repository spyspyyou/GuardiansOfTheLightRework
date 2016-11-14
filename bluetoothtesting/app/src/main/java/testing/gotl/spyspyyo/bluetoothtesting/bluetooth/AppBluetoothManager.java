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

import testing.gotl.spyspyyo.bluetoothtesting.UI.activities.BluetoothConnectionManagementTestActivity;
import testing.gotl.spyspyyo.bluetoothtesting.global.App;
import testing.gotl.spyspyyo.bluetoothtesting.global.GlobalTrigger;
import testing.gotl.spyspyyo.bluetoothtesting.global.TODS;

public class AppBluetoothManager implements TODS, GlobalTrigger {

    // bluetooth states
    private static final short DISABLED = 0;
    private static final short ENABLED = 1;
    private static final short DISCOVERABLE = 2;

    private static final char BLUETOOTH_NAME_USERNAME_INDICATOR = '_';
    private static final char BLUETOOTH_NAME_GAMEHOSTING_INDICATOR = '-';
    private static final char BLUETOOTH_NAME_GAMENAME_INDICATOR = '|';

    private static int bluetoothState = DISABLED;
    private static BluetoothAdapter bluetoothAdapter;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    private static ArrayList<BluetoothDevice> clients;
    private static ArrayList<BluetoothDevice> servers;

    // start of all the outside access method that the app has onto the bluetooth sector.

    //Global Trigger methods

    public void onAppStart(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)handleNonBluetoothDevice();
        if (startBluetoothOnAppEntering)enableBluetooth();
        BluetoothBroadcastReceiver.setupReceiver();
    }

    public void onAppResume(){

    }

    public void onAppStop(){
        if (stopBluetoothOnAppLeaving)disableBluetooth();
        unregisterReceiver();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_START_DISCOVERABLE && resultCode == Activity.RESULT_CANCELED){
            App.toast("Bluetooth was not enabled.");
        }
    }

    //public access methods

    public static ArrayList<BluetoothDevice> getClientList(){
        bluetoothAdapter.startDiscovery();
        clients = new ArrayList<>();
        return clients;
    }

    public static ArrayList<BluetoothDevice> getServerList(){
        bluetoothAdapter.startDiscovery();
        servers = new ArrayList<>();
        return servers;
    }

    //todo:add when the friends sector is introduced
    public static void updateFriendsStatus(){

    }

    public static void connectTo(BluetoothDevice bluetoothDevice){
        enableBluetooth();
        ConnectionManager.connect(bluetoothDevice);
    }

    public static void disconnectFrom(BluetoothDevice bluetoothDevice){
        ConnectionManager.disconnect(bluetoothDevice);
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
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
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
        return bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
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
            String deviceName = device.getName();
            if (!deviceName.startsWith(APP_IDENTIFIER))return;
            clients.add(device);
            if (deviceName.charAt(deviceName.indexOf(BLUETOOTH_NAME_GAMEHOSTING_INDICATOR)+1)=='1')
                servers.add(device);
            //todo:adjust the receiver of the notification
            BluetoothConnectionManagementTestActivity.notifyChange();
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
