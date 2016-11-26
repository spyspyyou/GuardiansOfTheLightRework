package mobile.data.usage.spyspyyou.layouttesting.utils;


import android.bluetooth.BluetoothDevice;

import mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;

public class BluetoothDeviceNameHandling implements TODS{

    public static final byte MAX_NAME_LENGTH = 16;
    private static final char USERNAME = '_';
    private static final char PICTURE_ID = '-';
    private static final char STATUS = ',';
    private static final char GAME_NAME = '.';
    private static final char DEFAULT = 'd';
    private static final char HOSTING = 'h';
    private static final char IN_GAME = 'g';

    public static char[] FORBIDDEN_CHARS = {
            USERNAME,
            PICTURE_ID,
            STATUS,
            GAME_NAME
    };


    public static String getUsername(BluetoothDevice bluetoothDevice){
        String name = bluetoothDevice.getName();
        return name.substring(name.charAt(USERNAME), name.charAt(USERNAME)+1);
    }

    public static boolean isAppDevice(BluetoothDevice bluetoothDevice){
        return bluetoothDevice.getName().startsWith(APP_IDENTIFIER);
    }

    public static boolean isInGame(BluetoothDevice bluetoothDevice){
        String name = bluetoothDevice.getName();
        return name.charAt(name.indexOf(STATUS)+1)== IN_GAME;
    }

    public static boolean isHosting(BluetoothDevice bluetoothDevice){
        String name = bluetoothDevice.getName();
        return name.charAt(name.indexOf(STATUS)+1)== HOSTING;
    }

    public static int getPictureId(BluetoothDevice bluetoothDevice){
        String name = bluetoothDevice.getName();
        return Integer.getInteger(name.substring(name.indexOf(PICTURE_ID)+1, name.indexOf(STATUS)));
    }

    private static char getStatusChar(byte status){
        switch (status){
            case 2:
                return IN_GAME;
            case 1:
                return HOSTING;
            case 0:
            default:
                return DEFAULT;
        }
    }

    public static String getBluetoothName(){
        return
                APP_IDENTIFIER
                + USERNAME
                + DataCenter.getUserName()
                + PICTURE_ID
                + DataCenter.getPictureId()
                + STATUS
                + getStatusChar(DataCenter.getAppStatus())
                + GAME_NAME
                + DataCenter.getGameName();

    }
}
