package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.BluetoothDeviceNameHandling;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;

import static java.lang.Integer.parseInt;

public class GameInformation {

    private static final char HEIGHT_INDICATOR = 'h', PLAYER_TYPES_INDICATOR = 't';

    /*package*/ final String GAME_NAME, GAME_HOST;
    private final String GAME_ADDRESS;
    /*package*/ final int WIDTH, HEIGHT;
    /*package*/ final boolean[] PLAYER_TYPES;

    public GameInformation(int width, int height, boolean[]playerTypes){
        GAME_NAME = DataCenter.getGameName();
        GAME_HOST = DataCenter.getUserName();
        GAME_ADDRESS = "";
        WIDTH = width;
        HEIGHT = height;
        PLAYER_TYPES = playerTypes;
    }

    private GameInformation(String gameName, String gameHost, String gameAddress, int width, int height, boolean[]playerTypes){
        GAME_NAME = gameName;
        GAME_HOST = gameHost;
        GAME_ADDRESS = gameAddress;
        WIDTH = width;
        HEIGHT = height;
        PLAYER_TYPES = playerTypes;
    }

    public static GameInformation fromString(String string, String address){
        BluetoothDevice bluetoothDevice = AppBluetoothManager.getBluetoothDevice(address);
        String gameName, gameHost;
        Log.i("GameInformation", "reading information from string: " + string);
        if (bluetoothDevice == null) gameName = gameHost = "-";
        else {
            gameName = BluetoothDeviceNameHandling.getGameName(bluetoothDevice);
            gameHost = BluetoothDeviceNameHandling.getUsername(bluetoothDevice);
        }
        int
                width = parseInt(string.substring(0, string.lastIndexOf(HEIGHT_INDICATOR))),
                height = parseInt(string.substring(string.lastIndexOf(HEIGHT_INDICATOR)+1, string.lastIndexOf(PLAYER_TYPES_INDICATOR)));
        boolean[] playerTypes = new boolean[4];
        char b;
        for (int i = 0; i < playerTypes.length; ++i){
             b = string.charAt(string.length() - (playerTypes.length - i));
            playerTypes[i] = (b == '1');
        }
        return new GameInformation(gameName, gameHost, address, width, height, playerTypes);
    }

    public String toString(){
        String string = "" + WIDTH + HEIGHT_INDICATOR + HEIGHT + PLAYER_TYPES_INDICATOR;
        for (boolean b:PLAYER_TYPES){
            if(b)string += '1';
            else string += '0';
        }
        return string;
    }

    public String getGAME_ADDRESS(){
        return GAME_ADDRESS;
    }
}
