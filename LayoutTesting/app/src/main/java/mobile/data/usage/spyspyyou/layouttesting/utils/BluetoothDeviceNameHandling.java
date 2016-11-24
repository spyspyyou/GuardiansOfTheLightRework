package mobile.data.usage.spyspyyou.layouttesting.utils;


import android.bluetooth.BluetoothDevice;

import mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS;

public class BluetoothDeviceNameHandling implements TODS{

    private static final char BLUETOOTH_NAME_USERNAME_INDICATOR = '_';
    private static final char BLUETOOTH_NAME_GAME_STATUS_INDICATOR = '-';
    private static final char BLUETOOTH_NAME_GAMENAME_INDICATOR = '|';


    public static String getName(BluetoothDevice bluetoothDevice){
        String name = bluetoothDevice.getName();
        return name.substring(name.charAt(BLUETOOTH_NAME_USERNAME_INDICATOR), name.charAt(BLUETOOTH_NAME_GAME_STATUS_INDICATOR)+1);
    }

    public static boolean isAppDevice(BluetoothDevice bluetoothDevice){
        return bluetoothDevice.getName().startsWith(APP_IDENTIFIER);
    }

    public static boolean isHosting(BluetoothDevice bluetoothDevice){
        String name = bluetoothDevice.getName();
        return name.charAt(name.indexOf(BLUETOOTH_NAME_GAME_STATUS_INDICATOR)+1)=='1';
    }

    public static String getBluetoothNameFromData(){
        return APP_IDENTIFIER + BLUETOOTH_NAME_USERNAME_INDICATOR+;
    }
}
