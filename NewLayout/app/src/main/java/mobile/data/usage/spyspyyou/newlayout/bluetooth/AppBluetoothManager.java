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

import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.APP_IDENTIFIER;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.ASK_TO_TURN_ON_BT;
import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.GAME_NAME;


public class AppBluetoothManager {

    private static final char TEXT_SEPARATOR= '\3';

    public static final int
            INVALID_BLUETOOTH_STATE = -1,
            REQUEST_BLUETOOTH = 210,
            REQUEST_CL_PERMISSION = 211;

    private static String
            localAddress = "",
            oldBluetoothName = "";

    private static BluetoothAdapter bluetoothAdapter = null;
    private static BluetoothBroadcastReceiver bluetoothBroadcastReceiver = null;

    private static final ArrayList<BluetoothActionListener> bluetoothActionListeners = new ArrayList<>();

    @NonNull
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
            new ModeIdle(activity);
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

    public static void startServer(Activity activity, GameInformation gameInformation){
        new ModeServer(activity, gameInformation);
    }

    public static void stopServer(Activity activity){
        new ModeConnection(activity);
    }

    public static void searchGames(Activity activity, SearchListener searchListener){
        new ModeSearch(activity, searchListener);
    }

    private static void cancelSearch(Activity activity){
        new ModeIdle(activity);
    }

    public static void joinGame(Activity activity, BluetoothDevice bluetoothDevice, @Nullable ConnectionListener listener){
        new ModeConnection(activity, bluetoothDevice, listener);
    }

    public static void disconnectFrom(String address){
        ConnectionManager.disconnect(address);
    }

    //----------------------------------------------------------------------------------------------
    //
    //          LISTENER HELP METHODS
    //
    //----------------------------------------------------------------------------------------------

    public static void addBluetoothListener(BluetoothActionListener listener){
        synchronized (bluetoothActionListeners) {
            bluetoothActionListeners.add(listener);
        }
    }

    public static void removeBluetoothListener(BluetoothActionListener listener){
        synchronized (bluetoothActionListeners) {
            bluetoothActionListeners.remove(listener);
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
        synchronized (bluetoothActionListeners){
            for (BluetoothActionListener listener:bluetoothActionListeners){
                listener.onConnectionEstablished(address);
            }
        }
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

    private static void notifyStop() {
        synchronized (bluetoothActionListeners) {
            for (BluetoothActionListener listener : bluetoothActionListeners) {
                listener.onStop();

            }
        }
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

    private static abstract class BluetoothMode {

        /**
         * This constructor is just for the ModeNotInitialized mode.
         */
        /*
        todo:reinsert when finished with the rest
        protected BluetoothMode(){}
        */

        protected BluetoothMode(Context context) {
            if (bluetoothMode.getClass() == getClass()){
                Log.i("BluetoothMode", "Already in the requested mode: " + bluetoothMode.getClass().getSimpleName());
                return;
            }
            Log.e("BluetoothMode", "Changing mode from '" + bluetoothMode.getClass().getSimpleName() + "' to '" + this.getClass().getSimpleName() + "'");
            bluetoothMode.leaveMode(context);
            setupMode(context);
            bluetoothMode = this;
            Log.i("BluetoothMode", "Successfully changed bluetooth mode from '" + bluetoothMode.getClass().getSimpleName() + "' to '" + this.getClass().getSimpleName() + "'");

        }

        protected abstract void setupMode(Context context);

        protected abstract void leaveMode(Context context);

        protected abstract void onStateChanged(Context context, int state);

        protected abstract void onDiscoveryStart();

        protected abstract void onDiscoveryFinish();

        protected abstract void onNameChange();

        protected abstract void onDeviceFound(BluetoothDevice device);

    }

    private static class ModeNotInitialized extends BluetoothMode {

        private static boolean initialized = false;

        public ModeNotInitialized() {
            super();
        }

        @Override
        protected void setupMode(@Nullable Context context) {}

        @Override
        protected void leaveMode(@Nullable Context context) {
            if (!initialized)throw new BluetoothAPIException("The Bluetooth API was not initialized");
        }

        @Override
        protected void onStateChanged(Context context, int state) {
            Log.e("ModeNotInitialized", "onStateChanged() - This statement should not be reached.");
        }

        @Override
        protected void onDiscoveryStart() {
            Log.e("ModeNotInitialized", "onDiscoveryStart() - This statement should not be reached.");
        }

        @Override
        protected void onDiscoveryFinish() {
            Log.e("ModeNotInitialized", "onDiscoveryFinish() - This statement should not be reached.");
        }

        @Override
        protected void onNameChange() {
            Log.e("ModeNotInitialized", "onNameChange() - This statement should not be reached.");
        }

        @Override
        protected void onDeviceFound(BluetoothDevice device) {
            Log.e("ModeNotInitialized", "onDeviceFound() - This statement should not be reached.");
        }

        private void initialize(@NonNull Activity activity) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null)
                handleNonBluetoothDevice(activity);
            else {
                localAddress = Settings.Secure.getString(activity.getContentResolver(), "bluetooth_address");
                oldBluetoothName = bluetoothAdapter.getName();
                if (localAddress == null)localAddress = bluetoothAdapter.getAddress();
                bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
                ConnectionManager.initialize();
                initialized = true;
            }
        }

        private void handleNonBluetoothDevice(final Context context){
            Log.w("APManager", "Device does not support bluetooth");
            new AlertDialog.Builder(context)
                    .setTitle("Bluetooth absent")
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

        public ModeIdle(Context context) {
            super(context);
        }

        @Override
        protected void setupMode(Context context) {
            Log.d("ModeIdle", "setting up mode");
            bluetoothBroadcastReceiver.unregister(context.getApplicationContext());
            bluetoothAdapter.cancelDiscovery();
            ConnectionManager.releaseAll();
            //todo:should it really be turned off?
            bluetoothAdapter.disable();
            if (!(bluetoothMode instanceof ModeNotInitialized))notifyStop();
        }

        @Override
        protected void leaveMode(Context context) {
            Log.d("ModeIdle", "leaving mode");
            bluetoothBroadcastReceiver.register(context.getApplicationContext());
        }

        @Override
        protected void onStateChanged(Context context, int state) {
            Log.w("ModeIdle", "Bluetooth discovery started");
        }

        @Override
        protected void onDiscoveryStart() {
            Log.d("ModeIdle", "Bluetooth discovery started");
        }

        @Override
        protected void onDiscoveryFinish() {
            Log.w("ModeIdle", "Bluetooth discovery finished");
        }

        @Override
        protected void onNameChange() {
            Log.i("ModeIdle", "onNameChanged: " + bluetoothAdapter.getName());
        }

        @Override
        protected void onDeviceFound(BluetoothDevice bluetoothDevice) {
            Log.w("ModeIdle", "Bluetooth device found");
        }
    }

    private static class ModeConnection extends BluetoothMode {

        /**
         * This constructor is just for the ModeNotInitialized mode.
         *
         * @param activity
         */
        protected ModeConnection(Context context) {
            super(context);
        }

        @Override
        protected void setupMode(Context context) {

        }

        @Override
        protected void leaveMode(Context context) {

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

        }
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

        private final SearchListener SEARCH_LISTENER;
        private final Activity ACTIVITY;

        private ModeSearch(Activity activity, SearchListener searchListener) {
            super(activity);
            ACTIVITY = activity;
            SEARCH_LISTENER = searchListener;
        }

        @Override
        protected void setupMode(Context context) {
            if (hasCoarseLocationPermission(ACTIVITY)) {
                if (bluetoothAdapter.isEnabled())
                    startSearch();
                else if (ASK_TO_TURN_ON_BT)
                    ACTIVITY.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BLUETOOTH);
                else
                    bluetoothAdapter.enable();
            } else
                requestCoarseLocationPermission(ACTIVITY);
        }

        @Override
        protected void leaveMode(Context context) {
            bluetoothAdapter.cancelDiscovery();
            ConnectionManager.releaseAll();
        }

        @Override
        protected void onStateChanged(Context context, int state) {
            if (state == BluetoothAdapter.STATE_ON)
                startSearch();
            else if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF)
                new ModeIdle(context);
        }

        @Override
        protected void onDiscoveryStart() {

        }

        @Override
        protected void onDiscoveryFinish() {

        }

        @Override
        protected void onNameChange() {
            Log.i("ModeSearch", "onNameChanged: " + bluetoothAdapter.getName());
        }

        @Override
        protected void onDeviceFound(BluetoothDevice device) {
            String deviceName = device.getName();
            if (deviceName == null)
                Log.d("ABManager", "Found a device with an invalid name");
            else {
                Log.d("ABManager", "Found a valid device: " + '"' + deviceName + '"');
                if (isHosting(deviceName)) {
                    Log.i("ABManager", "Found a Game:" + "\nName: " + deviceName.replace(APP_IDENTIFIER, "").replace(""+TEXT_SEPARATOR, "\nHost: ") + "\nAddress: " + device.getAddress());
                    SEARCH_LISTENER.onGameFound(deviceName.substring(APP_IDENTIFIER.length(), deviceName.indexOf(TEXT_SEPARATOR)), deviceName);
                    ConnectionManager.connect(device, connectionListenerSearch);
                }
            }
        }

        private static boolean hasCoarseLocationPermission(final Activity activity) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        private static void requestCoarseLocationPermission(final Activity activity) {
            if (shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(activity)
                        .setTitle("Coarse Location Permission")
                        .setMessage("Dunno why but for since android 5.0 for some reason it is required to search for bluetooth devices...")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CL_PERMISSION);
                                    }
                                }
                        )
                        .setCancelable(false)
                        .show();
            } else
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CL_PERMISSION);
        }

        private static void startSearch(){
            bluetoothAdapter.cancelDiscovery();
            ConnectionManager.releaseAll();
            bluetoothAdapter.startDiscovery();
        }

        private static boolean isHosting(String name){
            return name.startsWith(APP_IDENTIFIER);
        }

    }

    private static class ModeServer extends BluetoothMode {

        private GameInformation gameInformation;

        public ModeServer(Activity activity, GameInformation gameInformation){
            super(activity);
            this.gameInformation = gameInformation;
            setNameHosting();
        }

        @Override
        protected void setupMode(Activity activity) {

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
            if (prepareBluetooth(activity, true)) {
                ConnectionManager.startServer();
                bluetoothAdapter.cancelDiscovery();
            }
        }

        @Override
        protected void leaveMode(Activity activity) {
            ConnectionManager.stopServer();
            resetName();
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
            if (!hasRightName())setNameHosting();
        }

        @Override
        protected void onDeviceFound(BluetoothDevice device) {

        }

        private boolean hasRightName(){
            return bluetoothAdapter.getName().equals(APP_IDENTIFIER + gameInformation.GAME_NAME + TEXT_SEPARATOR + gameInformation.HOST_NAME);
        }

        private void setNameHosting(){
            bluetoothAdapter.setName(APP_IDENTIFIER + gameInformation.GAME_NAME + TEXT_SEPARATOR + gameInformation.HOST_NAME);
        }

        private void resetName(){
            bluetoothAdapter.setName(oldBluetoothName);
        }

    }

    //----------------------------------------------------------------------------------------------
    //
    //          LISTENERS
    //
    //----------------------------------------------------------------------------------------------

    public interface BluetoothActionListener{

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

        void onGameFound(String gameName, String hostName);

        void onGameInformationReceived();

    }


    //----------------------------------------------------------------------------------------------
    //
    //          EXCEPTION
    //
    //----------------------------------------------------------------------------------------------

    public static class BluetoothAPIException extends RuntimeException{
        public BluetoothAPIException(String errorMessage){
            super(errorMessage);
        }
    }

}