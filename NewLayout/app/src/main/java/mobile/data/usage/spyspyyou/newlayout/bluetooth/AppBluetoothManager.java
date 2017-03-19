package mobile.data.usage.spyspyyou.newlayout.bluetooth;

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

import mobile.data.usage.spyspyyou.newlayout.ui.adapters.GameInformationAdapter;

import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.APP_IDENTIFIER;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.ASK_TO_TURN_ON_BT;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.GAME_NAME;


public class AppBluetoothManager {

    public static final int
            REQUEST_BLUETOOTH = 210,
            REQUEST_CL_PERMISSION = 211;

    private static String localAddress = "";

    private static boolean isServer = false;
    private static int searchConnections = -1;

    private static BluetoothAdapter bluetoothAdapter = null;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver = null;
    private static GameInformationAdapter gameListAdapter;
    private static final ArrayList<BluetoothActionListener> listeners = new ArrayList<>();
    private static final ConnectionListener connectionListenerSearch = new ConnectionListener() {
        @Override
        public void onConnectionEstablished(BluetoothDevice bluetoothDevice) {
            new GameInformationRequest(getLocalAddress()).send(bluetoothDevice.getAddress());
        }

        @Override
        public void onConnectionFailed(BluetoothDevice bluetoothDevice) {
            --searchConnections;
        }

        @Override
        public void onConnectionClosed(BluetoothDevice bluetoothDevice) {
            --searchConnections;
            if (searchConnections == 0){
                notifySearchFinished();
                searchConnections = -1;
            }
        }
    };


    /**
     * Called when an Activity uses Bluetooth
     * @param context - context of the application calling this
     */
    public static void initialize(Context context){
        if (bluetoothAdapter != null)return;
        Log.i("ABManager", "initialization");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            handleNonBluetoothDevice(context);
            return;
        }
        localAddress = Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        if (localAddress == null)localAddress = bluetoothAdapter.getAddress();
        bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
        Log.i("ABManager", "initialization complete, local Address is " + localAddress);
    }

    //----------------------------------------------------------------------------------------------
    //
    //          START OF PUBLIC ACCESS METHODS
    //
    //----------------------------------------------------------------------------------------------

    public static void releaseRequirements(Context context){
        Log.d("ABManager", "releasing all requirements");
        bluetoothBroadcastReceiver.unregister(context);
        bluetoothAdapter.cancelDiscovery();
        ConnectionManager.disconnect();
        bluetoothAdapter.disable();
    }

    public static boolean startServer(Activity activity){
        if (prepareBluetooth(activity, true)) {
            ConnectionManager.startServer();
            bluetoothAdapter.cancelDiscovery();
            return true;
        }
        return false;
    }

    public static void stopServer(){
        ConnectionManager.stopServer();
        isServer = false;
        setBluetoothName();
    }

    public static boolean searchGames(Activity activity, GameInformationAdapter adapter){
        if (!assureCoarseLocationPermission(activity)){
            Log.w("APManager", "ain't got no coarse location permission");
            return false;
        }

        if (prepareBluetooth(activity, false)){
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
            ConnectionManager.disconnect();
            gameListAdapter = adapter;
            gameListAdapter.clear();
            return true;
        }
        return false;
    }

    public static void joinGame(String gameAddress, @Nullable ConnectionListener listener){
        bluetoothAdapter.cancelDiscovery();
        ConnectionManager.clearConnectionQueue();
        ConnectionManager.connect(getRemoteDevice(gameAddress), listener);
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

    public static void disconnectFrom(String address){
        ConnectionManager.disconnect(address);
    }

    public static void addConnectionListener(String address, ConnectionListener listener){
        ConnectionManager.addListener(address, listener);
    }

    public static void removeConnectionListener(String address, ConnectionListener listener){
        ConnectionManager.removeListener(address, listener);
    }

    //----------------------------------------------------------------------------------------------
    //
    //          INTERN METHODS
    //
    //----------------------------------------------------------------------------------------------

    private static void notifyStart(){
        synchronized (listeners){
            for (BluetoothActionListener listener:listeners){
                listener.onStart();
            }
        }
    }

    private static void notifyStop() {
        synchronized (listeners) {
            for (BluetoothActionListener listener : listeners) {
                listener.onStop();

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

    /*package*/ static void addGame(GameInformation gameInformation){
        Log.i("ABManager", "adding game " + gameInformation.GAME_NAME);
        gameListAdapter.addGame(gameInformation);
    }

    /*package*/ static void notifyConnectionEstablished(String address){
        synchronized (listeners){
            for (BluetoothActionListener listener:listeners){
                listener.onConnectionEstablished(address);
            }
        }
    }

    private static boolean prepareBluetooth(Activity activity, boolean server) {
        bluetoothBroadcastReceiver.register(activity);
        isServer = false;
        if (bluetoothAdapter.isEnabled()) {
            if (!server) {
                setBluetoothName();
                return true;
            }
        }
        if (server) {
            if (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                isServer = true;
                setBluetoothName();
                return true;
            }
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            activity.startActivityForResult(intent, REQUEST_BLUETOOTH);
        } else {
            if (ASK_TO_TURN_ON_BT) {
                activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH);
            } else {
                bluetoothAdapter.enable();
            }
        }
        return false;
    }

    private static void setBluetoothName(){
        String bluetoothName = APP_IDENTIFIER + ((isServer)?GAME_NAME:"");
        if (!bluetoothAdapter.isEnabled() || bluetoothAdapter.getName().equals(bluetoothName))return;
        bluetoothAdapter.setName(bluetoothName);
    }

    public static String getLocalAddress(){
        return localAddress;
    }

    /*package*/ static BluetoothServerSocket getBluetoothServerSocket(UUID uuid) throws IOException {
        return bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    private static boolean assureCoarseLocationPermission(final Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(activity)
                        .setTitle("Coarse Location")
                        .setMessage("Dunno why but need that to find games")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CL_PERMISSION);
                                    }
                                }
                        ).show();
            }else ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CL_PERMISSION);
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

    /*package*/ static BluetoothDevice getRemoteDevice(String address) throws IllegalArgumentException {
        if (!BluetoothAdapter.checkBluetoothAddress(address)) throw new IllegalArgumentException("Invalid MAC-Address");
        return bluetoothAdapter.getRemoteDevice(address);
    }

    private static class BluetoothBroadcastReceiver extends BroadcastReceiver {
        private static final int INVALID_STATE = -1;

        private static String[] FILTER_ACTIONS = {
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothAdapter.ACTION_DISCOVERY_STARTED,
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED,
                BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED,
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
                case BluetoothDevice.ACTION_FOUND:
                    onDeviceFound(intent);
                    break;
            }
        }

        private void onStateChanged(Context context, Intent intent){
            int extra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, INVALID_STATE);
            switch (extra){
                case BluetoothAdapter.STATE_ON:
                    notifyStart();
                    break;
                case BluetoothAdapter.STATE_OFF:
                case BluetoothAdapter.STATE_TURNING_OFF:
                    notifyStop();
                    releaseRequirements(context);
                    break;
            }
        }

        private void onDiscoveryStart(){
            notifySearchStarted();
            searchConnections = 0;
            Log.i("ABManager", "starting discovery");
        }

        private void onDiscoveryFinish(){
            if (searchConnections == 0){
                notifySearchFinished();
                searchConnections = -1;
            }
            Log.i("ABManager", "discovery finished");
        }

        private void onNameChange(){
            Log.i("ABManager", "name was changed to " + bluetoothAdapter.getName());
            setBluetoothName();
        }

        private void onDeviceFound(Intent intent){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null){
                Log.w("ABManager", "Received invalid device found intent");
                return;
            }

            String deviceName = device.getName();
            if (deviceName == null){
                Log.d("ABManager", "Found a Device with invalid name");
                return;
            }

            Log.d("ABManager", "Found a Device: " + '"' + deviceName + '"');
            if (isHosting(deviceName)) {
                Log.d("ABManager", "Found a Game: " + '"' + deviceName.replace(APP_IDENTIFIER, "") + '"');
                ConnectionManager.connect(getRemoteDevice(device.getAddress()), connectionListenerSearch);
                ++searchConnections;
            }
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

        private boolean isHosting(String name){
            return name.startsWith(APP_IDENTIFIER) && name.length() != APP_IDENTIFIER.length();
        }
    }

    //----------------------------------------------------------------------------------------------
    //
    //          LISTENERS
    //
    //----------------------------------------------------------------------------------------------

    public interface ConnectionListener {

        void onConnectionEstablished(BluetoothDevice bluetoothDevice);

        void onConnectionFailed(BluetoothDevice bluetoothDevice);

        void onConnectionClosed(BluetoothDevice bluetoothDevice);
    }

    public interface BluetoothActionListener{

        void onStart();

        void onStop();

        void onGameSearchStarted();

        void onGameSearchFinished();

        void onConnectionEstablished(String address);
    }
}