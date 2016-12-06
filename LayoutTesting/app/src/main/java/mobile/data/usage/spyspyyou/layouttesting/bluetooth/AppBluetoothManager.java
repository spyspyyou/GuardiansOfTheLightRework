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
import android.support.annotation.Nullable;
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

import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;

public class AppBluetoothManager implements TODS, GlobalTrigger {

    private static boolean bluetoothOnWhenAppEntered = false;
    private static boolean bluetoothRequestSent = false;
    private static BluetoothAdapter bluetoothAdapter;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    private static ArrayList<BluetoothDevice> clients;
    private static ArrayList<BluetoothDevice> servers;
    private static Notificator notificator;

    // start of all the outside access method that the app has onto the bluetooth sector.

    //Global Trigger methods

    public void onAppStart(){
        Log.i("APManager", "onAppStart");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)handleNonBluetoothDevice();
        bluetoothOnWhenAppEntered = isBluetoothEnabled();
        startReceiving();
        enableBluetooth();
    }

    public void onAppResume(){

    }

    public void onAppStop(){
        if (stopBluetoothOnAppLeaving){
            disableBluetooth();
            return;
        }
        if (makeBluetoothLikeBeforeAppStart && !bluetoothOnWhenAppEntered){
            disableBluetooth();
            return;
        }
        stopReceiving();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_START_DISCOVERABLE && resultCode == Activity.RESULT_CANCELED) {
            bluetoothDisabling();
        }
    }

    public static void serverRequirementChanged(boolean serverAvailabilityRequired){
        if (serverAvailabilityRequired)ConnectionManager.startServerAvailability();
        else ConnectionManager.stopServerAvailability();
    }

    //public access methods

    public static ArrayList<BluetoothDevice> getClientList(Notificator deviceFoundNotificator){
        Log.i("BtTest", "searching for Clients");
        deviceSearchSetup(deviceFoundNotificator);
        return clients;
    }

    public static ArrayList<BluetoothDevice> getServerList(Notificator deviceFoundNotificator){
        Log.i("BtTest", "Searching for Hosts");
        deviceSearchSetup(deviceFoundNotificator);
        return servers;
    }

    public static void cancelSearch(){
        bluetoothAdapter.cancelDiscovery();
    }

    public static void updateBluetoothName(){
        if (isBluetoothEnabled()) {
            setBluetoothName();
        }
    }

    /**
     * @param bluetoothDevice device to connect to
     * @param notificator interface to notify when the connection is established
     * @return true if there is already a connection to that device, else false
     */
    public static boolean connectTo(BluetoothDevice bluetoothDevice, @Nullable Notificator notificator){
        if (ConnectionManager.hasConnection(bluetoothDevice.getAddress()))return true;
        ConnectionManager.connect(bluetoothDevice, notificator);
        return false;
    }

    public static boolean hasConenction(String address){
        return ConnectionManager.hasConnection(address);
    }

    public static void checkBluetoothOn(){
        if (isBluetoothEnabled())return;
        enableBluetooth();
    }

    public static void disconnect(){
        ConnectionManager.disconnect();
    }

    public static void disconnectExcept(String address){
        ConnectionManager.disconnectExcept(address);
    }

    public static void disconnect(String address){
        ConnectionManager.disconnect(address);
    }

    public static BluetoothDevice getBluetoothDevice(String address){
        BluetoothDevice bluetoothDevice = ConnectionManager.getBluetoothDevice(address);
        if (bluetoothDevice == null && BluetoothAdapter.checkBluetoothAddress(address))bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        return bluetoothDevice;
    }

    public static String getAddress(){
        return android.provider.Settings.Secure.getString(App.getContext().getContentResolver(), "bluetooth_address");
    }

    //Intern methods

    /*package*/ static BluetoothServerSocket getBluetoothServerSocket(UUID uuid) throws IOException{
        return bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    /*package*/ static boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    private static void enableBluetooth(){
        if (bluetoothAdapter.isEnabled())setBluetoothName();
        if(bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)return;
        if (bluetoothRequestSent)return;
        bluetoothRequestSent = true;
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

    private static void deviceSearchSetup(Notificator deviceFoundNotificator){
        activateDeviceDiscovery();
        clients = new ArrayList<>();
        servers = new ArrayList<>();
        notificator = deviceFoundNotificator;
    }

    private static void request(){
        ActivityCompat.requestPermissions(App.accessActiveActivity(null),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION_PERMISSION);
    }

    public static void onCoarseLocationPermissionRequestResult(boolean granted){
        if (granted){
            activateDeviceDiscovery();
            App.toast("The permission was granted. The discovery feature can now be used.");
        }else{
            App.toast("No permission was granted. Discovery feature can't be used..");
        }
    }

    private static void bluetoothDisabling() {
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
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        App.accessActiveActivity(null).startActivity(homeIntent);
                    }
                }
        ).show();
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
        Log.i("BtTest", "started the reception");
    }

    private static void stopReceiving(){
        try {
            App.accessActiveActivity(null).getApplicationContext().unregisterReceiver(bluetoothBroadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        ConnectionManager.disconnect();
        ConnectionManager.stopServerAvailability();
        Log.i("BtTest", "stopped reception");
    }

    private static void setBluetoothName(){
        String bluetoothName = BluetoothDeviceNameHandling.getBluetoothName();
        if (!bluetoothAdapter.isEnabled() || bluetoothAdapter.getName().equals(bluetoothName))return;
        bluetoothAdapter.setName(bluetoothName);
    }

    private static boolean deviceAlreadyOnList(BluetoothDevice bluetoothDevice){
        for (BluetoothDevice device:clients){
            if (device.getAddress().equals(bluetoothDevice.getAddress()))return true;
        }
        return false;
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
                    setBluetoothName();
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
            // try in case there is old invalid data targeted
            try {
                notificator.discoveryFinished();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void onNameChange(){
            Log.i("BtTest", "onNameChange with name: " + bluetoothAdapter.getName());
            setBluetoothName();
        }

        private void onDeviceFound(Intent intent){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null){
                Log.i("BtTest", "Received invalid device found intent");
                return;
            }
            String deviceName = device.getName();
            if (deviceName == null){
                Log.i("BtTest", "Found a Device with invalid name");
                return;
            }
            Log.i("BtTest", "Found a Device: " + '"' + deviceName + '"');
            if (!BluetoothDeviceNameHandling.isAppDevice(device) || deviceAlreadyOnList(device))return;
            clients.add(device);
            if (BluetoothDeviceNameHandling.isHosting(device)){
                servers.add(device);
            }
            notificator.notifyChange();
        }

        private static void setupReceiver(){
            IntentFilter intentFilter = new IntentFilter();
            for (String action:FILTER_ACTIONS) {
                intentFilter.addAction(action);
            }
            bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
            App.accessActiveActivity(null).getApplicationContext().registerReceiver(bluetoothBroadcastReceiver, intentFilter);
        }
    }
}
