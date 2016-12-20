package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.BluetoothDeviceNameHandling;
import mobile.data.usage.spyspyyou.layouttesting.global.DataCenter;

public class PlayerInformation {

    //address equals HOST if it is the Host and nothing if it is a player on a not host device
    private final String PLAYER_NAME;
    private final String ADDRESS;
    private final int PICTURE_ID;

    //for the host
    public PlayerInformation(String address) {
        PLAYER_NAME = BluetoothDeviceNameHandling.getUsername(address);
        ADDRESS = address;
        Log.w("PlayerInformation", "address shouldn't be null");
        PICTURE_ID = BluetoothDeviceNameHandling.getPictureId(address);
    }

    /*package*/ PlayerInformation(String name, int picId) {
        PLAYER_NAME = name;
        ADDRESS = "";
        PICTURE_ID = picId;
    }

    //for the this user, all remote users need the other constructor
    public PlayerInformation() {
        PLAYER_NAME = DataCenter.getUserName();
        ADDRESS = AppBluetoothManager.getAddress();
        PICTURE_ID = DataCenter.getPictureId();
    }

    /*package*/ String getPLAYER_NAME(){
        return PLAYER_NAME;
    }

    /*package*/ int getPICTURE_ID(){
        return PICTURE_ID;
    }

    public String getADDRESS(){
        return ADDRESS;
    }

    public String toString(){
        String string = "" + PICTURE_ID + '_' + PLAYER_NAME;
        Log.i("PlayerInformation", "toString: " + string);
        return string;
    }

    public static PlayerInformation fromString(String string){
        Log.i("PlayerInformation", "reading from string: " + string);
        int id = Integer.parseInt(string.substring(0, string.indexOf('_')));
        String name = string.substring(string.indexOf('_') + 1);
        return new PlayerInformation(name, id);
    }

}

