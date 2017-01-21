package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import testing.gotl.spyspyyo.bluetoothtesting.activities.BluetoothActivity;
import testing.gotl.spyspyyo.bluetoothtesting.teststuff.GameInformation;
import testing.gotl.spyspyyo.bluetoothtesting.teststuff.GameInformationList;

import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static testing.gotl.spyspyyo.bluetoothtesting.teststuff.TEST_VARIABLES.APP_IDENTIFIER;
import static testing.gotl.spyspyyo.bluetoothtesting.teststuff.TEST_VARIABLES.ASK_TO_TURN_ON_BT;
import static testing.gotl.spyspyyo.bluetoothtesting.teststuff.TEST_VARIABLES.GAME_NAME;
import static testing.gotl.spyspyyo.bluetoothtesting.teststuff.TEST_VARIABLES.REQUEST_COARSE_LOCATION_PERMISSION;

//todo:turn off when the app is left?

public class AppBluetoothManager {

    private static final String NOT_HOSTING_STRING = "\n";

    private static final int
            REQUEST_START_DISCOVERABLE = 0,
            REQUEST_ENABLE_BT = 1;

    //the status options
    private static final byte
            NONE = 0,
            CONNECTION = 1,
            SEARCH = 2,
            SERVER = 3;

    private static byte
            status = NONE,
            targetStatus = NONE;

    private static String localAddress = "";

    private static BluetoothAdapter bluetoothAdapter = null;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver = null;
    private static GameInformationList gameList;
    private static final ArrayList<BluetoothActionListener> listeners = new ArrayList<>();

    /**
     * Called when an Activity uses Bluetooth
     * @see BluetoothActivity
     * @param context - context of the application calling this
     */
    public static void initialize(Context context){
        if (bluetoothAdapter != null)return;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            handleNonBluetoothDevice(context);
            return;
        }
        localAddress = Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
    }

    public static void onAppLeave(Context context){
        releaseRequirements(context);
        //todo:turn off bt?
    }

    //----------------------------------------------------------------------------------------------
    //
    //          START OF PUBLIC ACCESS METHODS
    //
    //----------------------------------------------------------------------------------------------

    public static void releaseRequirements(Context context){
        targetStatus = NONE;
        if (status != NONE) {
            status = NONE;
            notifyStatusChanged();
        }
        setBluetoothName();

        bluetoothBroadcastReceiver.unregister(context);
        bluetoothAdapter.cancelDiscovery();

        ConnectionManager.stopServer();
        ConnectionManager.disconnect();
    }

    public static void startServer(Activity activity, GameInformation gameInformation){
        targetStatus = SERVER;
        bluetoothBroadcastReceiver.register(activity);
        //start the reception threads
        ConnectionManager.startServer();
        if(bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            activity.startActivityForResult(intent, REQUEST_START_DISCOVERABLE);
        } else {
            bluetoothAdapter.cancelDiscovery();
            setBluetoothName();
            status = targetStatus;
            notifyStatusChanged();
            Log.i("BtTest", "bluetooth server available");
        }
    }

    public static void stopServer(){
        ConnectionManager.stopServer();
        //todo:turn off discoverable with 1s intent if user wants it
        if (targetStatus == SERVER){
            status = targetStatus = CONNECTION;
            notifyStatusChanged();
        }
    }

    public static boolean searchGames(Activity activity, GameInformationList list){
        if (!assureCoarseLocationPermission(activity)){
            Log.w("APManager", "ain't got no coarse location permission");
            return false;
        }

        targetStatus = SEARCH;
        if (prepareClient(activity)){
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
        }

        gameList = list;
        gameList.clear();
        return true;
    }

    public static void joinGame(String gameAddress, @Nullable ConnectionListener listener){
        if (status != SEARCH && status != CONNECTION)return;
        bluetoothAdapter.cancelDiscovery();
        targetStatus = CONNECTION;
        if (status != CONNECTION) {
            status = CONNECTION;
            notifyStatusChanged();
        }
        ConnectionManager.connect(getBluetoothDevice(gameAddress), listener);
    }

    public static void addBluetoothListener(BluetoothActionListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public static void removeBluetoothListener(BluetoothActionListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    //----------------------------------------------------------------------------------------------
    //
    //          INTERN METHODS
    //
    //----------------------------------------------------------------------------------------------

    private static void notifyStatusChanged(){
        synchronized (listeners){
            for (BluetoothActionListener listener:listeners){
                listener.onStateChanged(status);
            }
        }
    }

    private static void notifySearchStarted(){
        synchronized (listeners){
            for (BluetoothActionListener listener:listeners){
                listener.onGameSearchStarted();
            }
        }
    }

    private static void notifySearchFinished(){
        synchronized (listeners){
            for (BluetoothActionListener listener:listeners){
                listener.onGameSearchFinished();
            }
        }
    }

    /*package*/ static void notifyConnectionEstablished(String address){
        synchronized (listeners){
            for (BluetoothActionListener listener:listeners){
                listener.onConnectionEstablished(address);
            }
        }
    }

    private static boolean prepareClient(Activity activity) {
        bluetoothBroadcastReceiver.register(activity);
        if (bluetoothAdapter.isEnabled()) {
            setBluetoothName();
            Log.i("BtTest", "bluetooth client available");
            return true;
        }
        if (ASK_TO_TURN_ON_BT) {
            activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        } else {
            bluetoothAdapter.enable();
        }
        return false;
    }

    private static void setBluetoothName(){
        String bluetoothName = APP_IDENTIFIER + ((status == SERVER)?GAME_NAME:NOT_HOSTING_STRING);
        if (!bluetoothAdapter.isEnabled() || bluetoothAdapter.getName().equals(bluetoothName))return;
        bluetoothAdapter.setName(bluetoothName);
    }

    private static BluetoothDevice getBluetoothDevice(String address) {
        BluetoothDevice bluetoothDevice = null;
        if (BluetoothAdapter.checkBluetoothAddress(address))
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        else Log.w("ABManager", "received an invalid address");
        return bluetoothDevice;
    }

    /*package*/ static String getLocalAddress(){
        return localAddress;
    }

    /*package*/ static BluetoothServerSocket getBluetoothServerSocket(UUID uuid) throws IOException{
        return bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    private static boolean assureCoarseLocationPermission(Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(activity)
                        .setTitle("Coarse Location")
                        .setMessage("Dunno why but need that to find games")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }
                        ).show();
            }
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSION);
            return false;
        }
        return true;
    }

    private static void handleNonBluetoothDevice(final Context context){
        Log.w("APManager", "Device does not support bluetooth");
        new AlertDialog.Builder(context)
                .setTitle("Bluetooth?")
                .setMessage("Unfortunately your device seems to have no bluetooth. In a nutshell this app is pretty useless for you.")
                .setPositiveButton("Ok :(", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .show();
    }

    private static class BluetoothBroadcastReceiver extends BroadcastReceiver {
        private static final int INVALID_STATE = -1;

        private static String[] FILTER_ACTIONS = {
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothAdapter.ACTION_DISCOVERY_STARTED,
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED,
                BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED,
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED,
                BluetoothDevice.ACTION_FOUND
        };

        private static final IntentFilter filter = new IntentFilter();
        static {
            for (String action:FILTER_ACTIONS) {
                filter.addAction(action);
            }
        }

        private static boolean registered = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    onStateChanged(context, intent);
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
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    onScanModeChanged(context);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    onDeviceFound(intent);
                    break;
            }
        }

        private void onStateChanged(Context context, Intent intent){
            int extra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, INVALID_STATE);
            switch (extra){
                case BluetoothAdapter.STATE_ON:
                    if (targetStatus == SEARCH){
                        bluetoothAdapter.cancelDiscovery();
                        bluetoothAdapter.startDiscovery();
                    }
                    break;
                case BluetoothAdapter.STATE_OFF:
                case BluetoothAdapter.STATE_TURNING_OFF:
                    if (targetStatus != NONE)releaseRequirements(context);
                    break;
            }
        }

        private void onDiscoveryStart(){
            notifySearchStarted();
            if (targetStatus == SEARCH) {
                status = targetStatus;
                notifyStatusChanged();
            }else{
                bluetoothAdapter.cancelDiscovery();
            }
            Log.i("BtTest", "starting discovery");
        }

        private void onDiscoveryFinish(){
            notifySearchFinished();
            if (status == SEARCH){
                status = targetStatus = CONNECTION;
                notifyStatusChanged();
            }
            Log.i("BtTest", "discovery finished");
        }

        private void onNameChange(){
            Log.i("BtTest", "name was changed to" + bluetoothAdapter.getName());
            setBluetoothName();
        }

        private void onScanModeChanged(Context context){
            if (targetStatus == SERVER)
                if (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    status = targetStatus;
                    notifyStatusChanged();
                }else {
                    releaseRequirements(context);
                }
        }

        private void onDeviceFound(Intent intent){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null){
                Log.w("BtTest", "Received invalid device found intent");
                return;
            }

            String deviceName = device.getName();
            if (deviceName == null){
                Log.d("BtTest", "Found a Device with invalid name");
                return;
            }

            Log.d("ABManager", "Found a Device: " + '"' + deviceName + '"');
            if (isHosting(device) && !deviceAlreadyOnList(device)) gameList.add(new GameInformation(device.getAddress()));
        }

        private void register(Context context){
            if (registered)return;
            registered = true;
            context.getApplicationContext().registerReceiver(this, filter);
        }

        private void unregister(Context context){
            if (!registered)return;
            registered = false;
            context.getApplicationContext().unregisterReceiver(this);
        }

        private boolean deviceAlreadyOnList(BluetoothDevice bluetoothDevice){
            for (GameInformation gameInformation: gameList){
                if (gameInformation.getHostAddress().equals(bluetoothDevice.getAddress()))return true;
            }
            return false;
        }

        private boolean isHosting(BluetoothDevice bluetoothDevice){
            String name = bluetoothDevice.getName();
            return name.startsWith(APP_IDENTIFIER) && !bluetoothDevice.getName().endsWith(NOT_HOSTING_STRING);
        }
    }

    //----------------------------------------------------------------------------------------------
    //
    //          LISTENERS
    //
    //----------------------------------------------------------------------------------------------

    public interface ConnectionListener {

        void onConnectionEstablished();

        void onConnectionFailed();

        void onConnectionClosed();
    }

    public interface BluetoothActionListener{

        void onStateChanged(byte status);

        void onGameSearchStarted();

        void onGameSearchFinished();

        void onConnectionEstablished(String address);
    }
}