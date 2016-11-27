package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.bluetooth.BluetoothDevice;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;

import static java.lang.Integer.parseInt;

public class GameInformation {

    private static final char HEIGHT_INDICATOR = 'h', PLAYER_COUNT_INDICATOR = 'p', PLAYER_TYPES_INDICATOR = 't';

    public final String GAME_NAME, GAME_HOST, GAME_ADDRESS;
    public final int WIDTH, HEIGHT, PLAYER_COUNT;
    public final boolean[] PLAYER_TYPES;

    public GameInformation(String gameName, String gameHost, String gameAddress, int width, int height, int playerCount, boolean[]playerTypes){
        GAME_NAME = gameName;
        GAME_HOST = gameHost;
        GAME_ADDRESS = gameAddress;
        WIDTH = width;
        HEIGHT = height;
        PLAYER_COUNT = playerCount;
        PLAYER_TYPES = playerTypes;
    }

    public static GameInformation fromString(String string, String adress){
        BluetoothDevice bluetoothDevice = AppBluetoothManager.getDeviceFromAddress(adress);
        String gameName, gameHost;
        if (bluetoothDevice == null) gameName = gameHost = "-";
        else {
            gameName = BluetoothDeviceNameHandling.getGamename(bluetoothDevice);
            gameHost = BluetoothDeviceNameHandling.getUsername(bluetoothDevice);
        }
        int
                width = parseInt(string.substring(0, string.indexOf(HEIGHT_INDICATOR))),
                height = parseInt(string.substring(string.indexOf(HEIGHT_INDICATOR)+1), string.indexOf(PLAYER_COUNT_INDICATOR)),
                playerCount = parseInt(string.substring(string.indexOf(PLAYER_COUNT_INDICATOR)+1), string.indexOf(PLAYER_TYPES_INDICATOR));
        boolean[] playerTypes = new boolean[4];
        char b;
        for (int i = 0; i < playerTypes.length; ++i){
             b = string.charAt(string.length() - (playerTypes.length - i));
            playerTypes[i] = (b == '1');
        }
        return new GameInformation(gameName, gameHost, adress, width, height, playerCount, playerTypes);
    }

    public String toString(){
        String string = "" + WIDTH + HEIGHT_INDICATOR + HEIGHT + PLAYER_COUNT_INDICATOR + PLAYER_COUNT + PLAYER_TYPES_INDICATOR;
        for (boolean b:PLAYER_TYPES){
            if(b)string += '1';
            else string += '0';
        }
        return string;
    }
}
