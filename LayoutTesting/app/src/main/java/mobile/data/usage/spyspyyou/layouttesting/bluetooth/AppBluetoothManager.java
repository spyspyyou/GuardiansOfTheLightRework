package mobile.data.usage.spyspyyou.layouttesting.bluetooth;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.global.GlobalTrigger;
import mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.GotLActivity;
import mobile.data.usage.spyspyyou.layouttesting.utils.BluetoothDeviceNameHandling;

import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;

public class AppBluetoothManager implements TODS, GlobalTrigger {

    private static boolean bluetoothOnWhenAppEntered = false;
    private static BluetoothAdapter bluetoothAdapter;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    private static ArrayList<BluetoothDevice> clients;
    private static ArrayList<BluetoothDevice> servers;
    private static DeviceFoundNotificator notificator;

    // start of all the outside access method that the app has onto the bluetooth sector.

    //Global Trigger methods

    public void onAppStart(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)handleNonBluetoothDevice();
        if (bluetoothAdapter.isEnabled())bluetoothOnWhenAppEntered=true;
        else bluetoothOnWhenAppEntered = false;
        enableBluetooth();
        startReceiving();
    }

    public void onAppResume(){

    }

    public void onAppStop(){
        if (stopBluetoothOnAppLeaving&&!bluetoothOnWhenAppEntered)disableBluetooth();
        stopReceiving();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_START_DISCOVERABLE && resultCode == Activity.RESULT_CANCELED){
            bluetoothDisabling();
        }
    }

    //public access methods

    public static ArrayList<BluetoothDevice> getClientList(DeviceFoundNotificator deviceFoundNotificator){
        activateDeviceDiscovery();
        Log.i("BtTest", "searching for Clients");
        clients = new ArrayList<>();
        notificator = deviceFoundNotificator;
        return clients;
    }

    public static ArrayList<BluetoothDevice> getServerList(DeviceFoundNotificator deviceFoundNotificator){
        activateDeviceDiscovery();
        Log.i("BtTest", "Searching for Hosts");
        servers = new ArrayList<>();
        notificator = deviceFoundNotificator;
        return servers;
    }

    public static void updateBluetoothName(){
        if (isBluetoothEnabled()) {
            setBluetoothName();
        }
    }

    //todo:add when the friends sector is introduced
    public static void updateFriendsStatus(){

    }

    public static void connectTo(BluetoothDevice bluetoothDevice){
        ConnectionManager.connect(bluetoothDevice);
    }

    public static void disconnectFrom(BluetoothDevice bluetoothDevice){
        ConnectionManager.disconnect(bluetoothDevice);
    }

    public static void serverRequirementChanged(boolean serverAvailabilityRequired){
        if (serverAvailabilityRequired)ConnectionManager.startServerAvailability();
        else ConnectionManager.stopServerAvailability();
    }
    //Intern methods

    private static void enableBluetooth(){
        if (bluetoothAdapter.isEnabled())setBluetoothName();
        if(bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)return;
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        App.accessActiveActivity(null).startActivityForResult(intent, REQUEST_START_DISCOVERABLE);
    }

    private static void disableBluetooth(){
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    private static void activateDeviceDiscovery(){
        if (!assureCoarseLocationPermission()){
            App.toast("Can't discover without the 'Coarse Location' permission.");
            return;
        }
        if (bluetoothAdapter.isDiscovering())bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();
    }

    private static boolean assureCoarseLocationPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(App.getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            boolean explain = shouldShowRequestPermissionRationale(App.accessActiveActivity(null), Manifest.permission.ACCESS_COARSE_LOCATION);
            if (explain) {
                new AlertDialog.Builder(App.accessActiveActivity(null))
                        .setTitle(R.string.explanation)
                        .setMessage(R.string.explanation_text)
                        .setPositiveButton(R.string.give_me, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        request();
                                    }
                                }
                        ).setNegativeButton(R.string.expl_refuse, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                }).show();
            }else {
                request();
            }
            return false;
        }
        return true;
    }

    private static void request(){
        ActivityCompat.requestPermissions(App.accessActiveActivity(null),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION_PERMISSION);
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

    public static void onCoarseLocationPermissionRequestResult(boolean granted){
        if (granted){
            activateDeviceDiscovery();
            App.toast("The permission was granted. The discovery feature can now be used.");
        }else{
            App.toast("No permission was granted. Discovery feature can't be used..");
        }
    }

    private static void handleNonBluetoothDevice(){
        new AlertDialog.Builder(App.accessActiveActivity(null))
                .setTitle("Bluetooth required")
                .setMessage("Unfortunately the App does not work on devices without a Bluetooth Function.")
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
        Log.i("BtTest", "started reception");
    }

    private static void stopReceiving(){
        try {
            App.accessActiveActivity(null).unregisterReceiver(bluetoothBroadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        ConnectionManager.disconnect();
        ConnectionManager.stopServerAvailability();
        Log.i("BtTest", "stopped reception");
    }

    //todo:adjust
    private static void setBluetoothName(){
        String bluetoothName = BluetoothDeviceNameHandling.getBluetoothName();
        if (!bluetoothAdapter.isEnabled() || bluetoothAdapter.getName().equals(bluetoothName))return;
        bluetoothAdapter.setName(bluetoothName);
        Log.i("BtTest", "changed bluetooth name to: " + bluetoothName);
    }

    public static BluetoothServerSocket getBluetoothServerSocket(UUID uuid) throws IOException{
        return bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    public static boolean isBluetoothEnabled() {
        if (bluetoothAdapter == null)return false;
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
                    onDiscoveryStart();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    onDiscoveryFinish();
                    break;
                case BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED:
                    onNameChange();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    onDeviceFound(intent);
                    break;
                default:
                    Log.w("BtTest", "Received an unidentifiable Intent");
            }
        }

        private void onStateChanged(Intent intent){
            int extra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, INVALID_STATE);
            switch (extra){
                case BluetoothAdapter.STATE_ON:
                    if (GotLActivity.isActiveActivityRequiresServer()){
                        ConnectionManager.startServerAvailability();
                    }
                    break;
                case BluetoothAdapter.STATE_OFF:
                    ConnectionManager.stopServerAvailability();
                    if (App.isActive())bluetoothDisabling();
                    break;
            }
        }

        private void onDiscoveryStart(){
            Log.i("BtTest", "starting discovery");
        }

        private void onDiscoveryFinish(){
            Log.i("BtTest", "discovery finished");
        }

        private void onNameChange(){
            Log.i("BtTest", "onNameChange with name: " + bluetoothAdapter.getName());
            setBluetoothName();
        }

        private void onDeviceFound(Intent intent){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            if (deviceName == null){
                Log.i("BtTest", "Found a Device with invalid name");
                return;
            }
            Log.i("BtTest", "Found a Device " + deviceName);
            if (!BluetoothDeviceNameHandling.isAppDevice(device))return;
            clients.add(device);
            //todo:remove ! after the testing
            if (!BluetoothDeviceNameHandling.isHosting(device))
                servers.add(device);
            notificator.notifyChange();
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
