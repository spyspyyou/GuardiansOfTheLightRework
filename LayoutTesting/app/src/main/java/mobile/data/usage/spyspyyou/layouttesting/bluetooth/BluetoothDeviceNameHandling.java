package mobile.data.usage.spyspyyou.layouttesting.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS;
import mobile.data.usage.spyspyyou.layouttesting.global.DataCenter;

public class BluetoothDeviceNameHandling implements TODS{

    public static final byte MAX_NAME_LENGTH = 16;
    public static final int MAX_TEXT_LENGTH = 256;

    private static final char
            USERNAME = '_',
            PICTURE_ID = '-',
            STATUS = ',',
            GAME_NAME = '.',
            ENTER = '\n',
            DEFAULT = 'd',
            HOSTING = 'h',
            IN_GAME = 'g';

    public static char[] FORBIDDEN_CHARS = {
            USERNAME,
            PICTURE_ID,
            STATUS,
            GAME_NAME,
            ENTER
    };

    public static String getUsername(String address){
        return getUsername(AppBluetoothManager.getBluetoothDevice(address));
    }

    public static String getUsername(BluetoothDevice bluetoothDevice){
        if (bluetoothDevice == null){
            Log.w("BDNHandling", "trying to get name from null device");
            return "";
        }
        String name = bluetoothDevice.getName();
        return name.substring(name.indexOf(USERNAME)+1, name.indexOf(PICTURE_ID));
    }

    public static String getGameName(BluetoothDevice bluetoothDevice){
        String name = bluetoothDevice.getName();
        name = name.substring(name.indexOf(GAME_NAME)+1);
        if (name.length() == 0) return "-";
        return name;
    }

    public static int getPictureId(String address){
        return getPictureId(AppBluetoothManager.getBluetoothDevice(address));
    }

    public static int getPictureId(BluetoothDevice bluetoothDevice){
        if (bluetoothDevice == null){
            Log.w("BDNHandling", "trying to get pictureId from null device");
            return -1;
        }
        String name = bluetoothDevice.getName();
        return Integer.parseInt(name.substring(name.indexOf(PICTURE_ID)+1, name.indexOf(STATUS)));
    }

    public static boolean isInGame(BluetoothDevice bluetoothDevice){
        if (bluetoothDevice == null){
            Log.w("BDNHandling", "trying to get isInGame from null device");
            return false;
        }
        String name = bluetoothDevice.getName();
        return name.charAt(name.indexOf(STATUS)+1)== IN_GAME;
    }

    /*package*/ static boolean isAppDevice(BluetoothDevice bluetoothDevice){
        if (bluetoothDevice == null){
            Log.w("BDNHandling", "trying to get isAppDevice from null device");
            return false;
        }
        return bluetoothDevice.getName().startsWith(APP_IDENTIFIER);
    }

    /*package*/ static boolean isHosting(BluetoothDevice bluetoothDevice){
        if (bluetoothDevice == null){
            Log.w("BDNHandling", "trying to get isHosting from null device");
            return false;
        }
        String name = bluetoothDevice.getName();
        return name.charAt(name.indexOf(STATUS)+1)== HOSTING;
    }

    /*package*/ static String getBluetoothName(){
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

    private static char getStatusChar(byte status){
        switch (status){
            case DataCenter.IN_GAME:
                return IN_GAME;
            case DataCenter.HOSTING:
                return HOSTING;
            case DataCenter.DEFAULT:
            default:
                return DEFAULT;
        }
    }
}
