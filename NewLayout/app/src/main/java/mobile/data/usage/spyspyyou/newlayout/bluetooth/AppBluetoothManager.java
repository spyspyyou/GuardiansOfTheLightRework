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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import mobile.data.usage.spyspyyou.newlayout.ui.adapters.GameInformationAdapter;

import static android.R.attr.mode;
import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager.ModeSearch.assureCoarseLocationPermission;
import static mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager.ModeSearch.connectionListenerSearch;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.APP_IDENTIFIER;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.ASK_TO_TURN_ON_BT;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.GAME_NAME;


public class AppBluetoothManager {

    public static final int
            INVALID_BLUETOOTH_STATE = -1,
            REQUEST_BLUETOOTH = 210,
            REQUEST_CL_PERMISSION = 211;

    private static String localAddress = "";

    private static BluetoothAdapter bluetoothAdapter = null;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver = null;

    private static final ArrayList<BluetoothActionListener> bluetoothActionListeners = new ArrayList<>();

    private static volatile BluetoothMode bluetoothMode = new ModeNotInitialized();

    //----------------------------------------------------------------------------------------------
    //
    //          Bluetooth Activity Methods
    //
    //----------------------------------------------------------------------------------------------

    /**
     * Called when an Activity wants to use the Bluetooth API and thus extends BluetoothActivity.
     * @see BluetoothActivity
     * @param activity - Activity calling the method.
     */
    /*package*/ static void initialize(@NonNull Activity activity) {
        if (bluetoothMode instanceof ModeNotInitialized){
            Log.i("ABManager", "initialization starting");
            ((ModeNotInitialized) bluetoothMode).initialize(activity);
            new ModeIdle();
            Log.i("ABManager", "initialization complete, local Address is " + localAddress);
        }else {
            Log.i("ABManager", "already initialized");
        }
    }

    /*package*/ void onPause(){
        //todo:unregister receiver? Shut down BT?
    }

    /*package*/ void onActivityResult(int requestCode, int resultCode) {

    }

    /*package*/ void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    //----------------------------------------------------------------------------------------------
    //
    //          START OF PUBLIC ACCESS METHODS
    //
    //----------------------------------------------------------------------------------------------

    public static void releaseAll(Activity activity){
        new ModeIdle(activity);
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

    private static void cancelSearch(){

    }

    public static void joinGame(String gameAddress, @Nullable ConnectionListener listener){
        bluetoothAdapter.cancelDiscovery();
        ConnectionManager.clearConnectionQueue();
        ConnectionManager.connect(getRemoteDevice(gameAddress), listener);
    }

    public static void disconnectFrom(String address){
        ConnectionManager.disconnect(address);
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


    /*package*/ static BluetoothServerSocket getBluetoothServerSocket(UUID uuid) throws IOException {
        return bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_IDENTIFIER, uuid);
    }

    /*
    /*package/ static BluetoothDevice getRemoteDevice(String address) throws IllegalArgumentException {
        if (!BluetoothAdapter.checkBluetoothAddress(address)) throw new IllegalArgumentException("Invalid MAC-Address");
        return bluetoothAdapter.getRemoteDevice(address);
    }
    */

    private static void notifyStart(){
        synchronized (bluetoothActionListeners){
            for (BluetoothActionListener listener:bluetoothActionListeners){
                listener.onStart();
            }
        }
    }

    private static void notifyStop() {
        synchronized (bluetoothActionListeners) {
            for (BluetoothActionListener listener : bluetoothActionListeners) {
                listener.onStop();

            }
        }
    }

    private static boolean isHosting(String name){
        return name.startsWith(APP_IDENTIFIER) && name.length() != APP_IDENTIFIER.length();
    }

    public static String getLocalAddress(){
        return localAddress;
    }

    private static class BluetoothBroadcastReceiver extends BroadcastReceiver {

        private static final String[] FILTER_ACTIONS = {
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
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, INVALID_BLUETOOTH_STATE);
            if (state != INVALID_BLUETOOTH_STATE)
                bluetoothMode.onStateChanged(context, state);
            else
                Log.e("BluetoothBR", "Received an invalid bluetooth state changed event");
        }

        private void onDiscoveryStart(){
            Log.i("ABManager", "bluetooth discovery started");
            bluetoothMode.onDiscoveryStart();
        }

        private void onDiscoveryFinish(){
            Log.i("ABManager", "bluetooth discovery finished");
            bluetoothMode.onDiscoveryFinish();
        }

        private void onNameChange(){
            Log.i("ABManager", "Bluetooth name has changed to " + bluetoothAdapter.getName());
            bluetoothMode.onNameChange();
        }

        private void onDeviceFound(Intent intent){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null)
                Log.w("ABManager", "Received invalid device found intent");
            else
                bluetoothMode.onDeviceFound(device);
        }

        private void register(Context context){
            if (registered)
                Log.w("BluetoothBR", "receiver is already registered");
            else {
                registered = true;
                context.getApplicationContext().registerReceiver(this, filter);
            }
        }

        private void unregister(Context context){
            if (!registered)
                Log.w("BluetoothBR", "receiver isn't registered");
            else {
                registered = false;
                context.getApplicationContext().unregisterReceiver(this);
            }
        }

    }

    //todo: add a handler postDelayed to catch something taking too long?

    private static abstract class BluetoothMode {

        public BluetoothMode(Activity activity) {
            if (bluetoothMode == null)return;
            Log.e("BluetoothMode", "Changing mode from '" + bluetoothMode.getClass().getSimpleName() + "' to '" + this.getClass().getSimpleName() + "'");
            bluetoothMode.leaveMode(activity);
            if(setupMode(activity)) {
                bluetoothMode = this;
                Log.i("BluetoothMode", "Successfully changed bluetooth mode from '" + bluetoothMode.getClass().getSimpleName() + "' to '" + this.getClass().getSimpleName() + "'");
            }else
                Log.e("BluetoothMode", "Failed to change bluetooth mode from '" + bluetoothMode.getClass().getSimpleName() + "' to '" + this.getClass().getSimpleName() + "'");
        }

        protected abstract boolean setupMode(Activity activity);

        protected abstract void leaveMode(Activity activity);

        protected abstract void onStateChanged(Context context, int state);

        protected abstract void onDiscoveryStart();

        protected abstract void onDiscoveryFinish();

        protected abstract void onNameChange();

        protected abstract void onDeviceFound(BluetoothDevice device);

    }

    private static class ModeNotInitialized extends BluetoothMode {

        public ModeNotInitialized() {
            super(null);
        }

        @Override
        protected boolean setupMode(@Nullable Activity activity) {return true;}

        @Override
        protected void leaveMode(@Nullable Activity activity) {
            if (bluetoothAdapter == null)throw new BluetoothAPIException("The Bluetooth API was not initialized");
        }

        @Override
        protected void onStateChanged(Context context, int state) {
            Log.e("APManager.ModeNI", "onStateChanged() - This statement should not be reached.");
        }

        @Override
        protected void onDiscoveryStart() {
            Log.e("APManager.ModeNI", "onDiscoveryStart() - This statement should not be reached.");
        }

        @Override
        protected void onDiscoveryFinish() {
            Log.e("APManager.ModeNI", "onDiscoveryFinish() - This statement should not be reached.");
        }

        @Override
        protected void onNameChange() {
            Log.e("APManager.ModeNI", "onNameChange() - This statement should not be reached.");
        }

        @Override
        protected void onDeviceFound(BluetoothDevice device) {
            Log.e("APManager.ModeNI", "onDeviceFound() - This statement should not be reached.");
        }

        private void initialize(@NonNull Activity activity) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null)
                handleNonBluetoothDevice(activity);
            else {
                localAddress = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_address");
                if (localAddress == null)localAddress = bluetoothAdapter.getAddress();
                bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
            }
        }

        private void handleNonBluetoothDevice(final Context context){
            Log.w("APManager", "Device does not support bluetooth");
            new AlertDialog.Builder(context)
                    .setTitle("Bluetooth?")
                    .setMessage("Unfortunately your device doesn't seem to have bluetooth. In a nutshell this app is pretty useless for you.")
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
    }

    private static class ModeIdle extends BluetoothMode {

        public ModeIdle(Activity activity) {
            super(activity);
        }

        @Override
        protected boolean setupMode(Activity activity) {
            bluetoothBroadcastReceiver.unregister(activity.getApplicationContext());
            bluetoothAdapter.cancelDiscovery();
            ConnectionManager.disconnect();
            ConnectionManager.stopServer();
            ConnectionManager.clearConnectionQueue();
            bluetoothAdapter.disable();
            if (!(bluetoothMode instanceof ModeNotInitialized))notifyStop();
            return true;
        }

        @Override
        protected void leaveMode(Activity activity) {
            bluetoothBroadcastReceiver.register(activity.getApplicationContext());
            notifyStart();
        }

        @Override
        protected void onStateChanged(Context context, int state) {

        }

        @Override
        protected void onDiscoveryStart() {

        }

        @Override
        protected void onDiscoveryFinish() {

        }

        @Override
        protected void onNameChange() {

        }

        @Override
        protected void onDeviceFound(BluetoothDevice bluetoothDevice) {

        }
    }

    private static class ModeConnection extends BluetoothMode {

    }

    private static class ModeSearch extends BluetoothMode {

        private static final ConnectionListener connectionListenerSearch = new ConnectionListener() {
            @Override
            public void onConnectionEstablished(BluetoothDevice bluetoothDevice) {
                new GameInformationRequest(getLocalAddress()).send(bluetoothDevice.getAddress());
            }

            @Override
            public void onConnectionFailed(BluetoothDevice bluetoothDevice){}

            @Override
            public void onConnectionClosed(BluetoothDevice bluetoothDevice) {}
        };
        private final SearchListener searchListener;

        public ModeSearch(Activity activity, SearchListener searchListener) {
            super(activity);
            this.searchListener = searchListener;
        }

        @Override
        protected boolean setupMode(Activity activity) {
            return false;
        }

        @Override
        protected void leaveMode(Activity activity) {

        }

        @Override
        protected void onStateChanged(Context context, int state) {

        }

        @Override
        protected void onDiscoveryStart() {

        }

        @Override
        protected void onDiscoveryFinish() {

        }

        @Override
        protected void onNameChange() {

        }

        @Override
        protected void onDeviceFound(BluetoothDevice device) {
            String deviceName = device.getName();
            if (deviceName == null)
                Log.d("ABManager", "Found a device with an invalid name");
            else {
                Log.d("ABManager", "Found a valid device: " + '"' + deviceName + '"');
                if (isHosting(deviceName)) {
                    Log.i("ABManager", "Found a Game: " + '"' + deviceName.replace(APP_IDENTIFIER, "") + '"');
                    ConnectionManager.connect(getRemoteDevice(device.getAddress()), connectionListenerSearch);
                }
            }
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
    }

    private static class ModeServer extends BluetoothMode {

        @Override
        protected boolean setupMode(Context context) {

            return true;
        }

        @Override
        protected void leaveMode(Context context) {
            ConnectionManager.stopServer();
            setBluetoothName();
        }
    }

    //----------------------------------------------------------------------------------------------
    //
    //          LISTENERS
    //
    //----------------------------------------------------------------------------------------------

    public interface BluetoothActionListener{

        void onStart();

        void onStop();

        void onConnectionEstablished(BluetoothDevice bluetoothDevice);
    }

    public interface ConnectionListener {

        void onConnectionEstablished(BluetoothDevice bluetoothDevice);

        void onConnectionFailed(BluetoothDevice bluetoothDevice);

        void onConnectionClosed(BluetoothDevice bluetoothDevice);
    }

    public interface SearchListener {

        void onSearchStarted();

        void onSearchFinished();

        void onGameFound();

        void onGameInformationReceived();

    }

    public static class BluetoothAPIException extends RuntimeException{
        public BluetoothAPIException(String errorMessage){
            super(errorMessage);
        }
    }

}