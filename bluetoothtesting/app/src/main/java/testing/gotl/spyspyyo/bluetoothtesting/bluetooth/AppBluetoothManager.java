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
import testing.gotl.spyspyyo.bluetoothtesting.UI.activities.GotLActivity;
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
    private static String bluetoothName = APP_IDENTIFIER;
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
        startReceiving();
    }

    public void onAppResume(){

    }

    public void onAppStop(){
        if (stopBluetoothOnAppLeaving)disableBluetooth();
        stopReceiving();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_START_DISCOVERABLE && resultCode == Activity.RESULT_CANCELED){
            bluetoothDisabling();
        }
    }

    //public access methods

    public static ArrayList<BluetoothDevice> getClientList(){
        bluetoothAdapter.startDiscovery();
        Log.i("BtTest", "searching for Clients");
        clients = new ArrayList<>();
        return clients;
    }

    public static ArrayList<BluetoothDevice> getServerList(){
        bluetoothAdapter.startDiscovery();
        Log.i("BtTest", "Searching for Hosts");
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

    public static void serverRequirementChanged(boolean serverAvailabilityRequired){
        if (serverAvailabilityRequired)ConnectionManager.startServerAvailability();
        else ConnectionManager.stopServerAvailability();
    }
    //Intern methods

    private static void enableBluetooth(){
        if (bluetoothAdapter.isEnabled()){
            setBluetoothName();
            if(bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)return;
        }
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
        new AlertDialog.Builder(App.accessActiveActivity(null))
                .setTitle("Stop Resisting!")
                .setMessage("The App requires active and visible bluetooth. So If you'd be so kind and tried hitting the right button again :)")
                .setPositiveButton("I will do my best!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableBluetooth();
                    }
                }).setNegativeButton("I am incorrigible.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.accessActiveActivity(null).finish();
            }
        }).show();
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

    private static void startReceiving(){
        BluetoothBroadcastReceiver.setupReceiver();
        new EventReceiverThread();
        new EventSenderThread();
        Log.e("BtTest", "started reception");
    }

    private static void stopReceiving(){
        App.accessActiveActivity(null).unregisterReceiver(bluetoothBroadcastReceiver);
        ConnectionManager.disconnect();
        ConnectionManager.stopServerAvailability();
        Log.e("BtTest", "stopped reception");
    }

    //todo:adjust
    private static void setBluetoothName(){
        bluetoothName = APP_IDENTIFIER + '_' + "";
        bluetoothAdapter.setName(bluetoothName);
    }

    public static BluetoothServerSocket getBluetoothServerSocket(UUID uuid) throws IOException{
        return bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    public static boolean isBluetoothEnabled() {
        return bluetoothAdapter.isEnabled();
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
                    if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)bluetoothState = ENABLED;
                    setBluetoothName();
                    enableBluetooth();
                    if (GotLActivity.isActiveActivityRequiresServer())ConnectionManager.startServerAvailability();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    bluetoothState = DISABLED;
                    ConnectionManager.stopServerAvailability();
                    if (App.isActive())bluetoothDisabling();
                    break;
            }
        }

        private void onDiscoveryStart(Intent intent){
            Log.e("BtTest", "starting discovery");
            BluetoothConnectionManagementTestActivity.checkBox.setChecked(true);
        }

        private void onDiscoveryFinish(Intent intent){
            Log.e("BtTest", "discovery finished");
            BluetoothConnectionManagementTestActivity.checkBox.setChecked(false);
        }

        private void onNameChange(Intent intent){
            if (bluetoothState == DISABLED)return;
            if (!bluetoothAdapter.getName().equals(bluetoothName)) setBluetoothName();
        }

        private void onDeviceFound(Intent intent){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            Log.e("BtTest", "Found a Device " + deviceName);
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
