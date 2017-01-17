package testing.gotl.spyspyyo.bluetoothtesting.teststuff;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.BluetoothDeviceNameHandling;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.MAX_MAP_LENGTH;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.MIN_MAP_LENGTH;

public class DataCenter {

    private static final String
            APP_PREFS = "APP_PREFS",
            PICTURE_ID = "profilePicture",
            USER_NAME = "userName",
            GAME_NAME = "gameName",
            MAP_WIDTH = "mapWidth",
            MAP_HEIGHT = "mapHeight",
            //MAP_DATA = "mapData",
            FLUFFY_ALLOWED = "fluffyAllowed",
            SLIME_ALLOWED = "slimeAllowed",
            GHOST_ALLOWED = "ghostAllowed",
            NOX_ALLOWED = "noxAllowed",
            JOIN_RIGHTS = "joinRights",
            CHARACTER = "character";

    public final static byte
            DEFAULT = 0,
            HOSTING = 1,
            IN_GAME = 2;

    public final static byte
            PUBLIC = 0,
            FRIENDLY = 1,
            PRIVATE = 2;

    private static byte appStatus = DEFAULT;

    private static boolean
            fluffyAllowed = true,
            slimeAllowed = true,
            ghostAllowed = true,
            noxAllowed = true;

    private static byte
            pictureId = -1,
            joinRights = PUBLIC,

    private static String
            userName = "",
            gameName = "";

    private static SharedPreferences sharedPreferences;

    private static void initializeDataCenter(){
        sharedPreferences = App.getContext().getApplicationContext().getSharedPreferences(APP_PREFS, Activity.MODE_PRIVATE);
        pictureId = (byte) sharedPreferences.getInt(PICTURE_ID, -1);
        userName = sharedPreferences.getString(USER_NAME, "");
        gameName = sharedPreferences.getString(GAME_NAME, "");
        fluffyAllowed = sharedPreferences.getBoolean(FLUFFY_ALLOWED, true);
        slimeAllowed = sharedPreferences.getBoolean(SLIME_ALLOWED, true);
        ghostAllowed = sharedPreferences.getBoolean(GHOST_ALLOWED, true);
        noxAllowed = sharedPreferences.getBoolean(NOX_ALLOWED, true);
        joinRights = (byte) sharedPreferences.getInt(JOIN_RIGHTS, PUBLIC);
    }

    private static void saveData(){
        if (sharedPreferences == null)return;
        Log.i("DataCenter", "saving data");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PICTURE_ID, pictureId);
        editor.putString(USER_NAME, userName);
        editor.putString(GAME_NAME, gameName);
        //editor.putString(MAP_DATA, );
        editor.putBoolean(FLUFFY_ALLOWED, fluffyAllowed);
        editor.putBoolean(SLIME_ALLOWED, slimeAllowed);
        editor.putBoolean(GHOST_ALLOWED, ghostAllowed);
        editor.putBoolean(NOX_ALLOWED, noxAllowed);
        editor.putInt(JOIN_RIGHTS, joinRights);
        editor.apply();
    }


    //todo:set/get the map data
    //getters and setters
    public static String getUserName(){
        return matchNameToStandards(userName);
    }

    public static void setUserName(EditText editText) {
        userName = editText.getText().toString();
        AppBluetoothManager.updateBluetoothName();
        saveData();
    }

    //todo:assure this being done
    public static void setAppStatus(byte status){
        if (status>2||status<0)return;
        appStatus = status;
        AppBluetoothManager.updateBluetoothName();
        saveData();
    }

    public static byte getAppStatus(){
        return appStatus;
    }

    public static void setPictureId(byte id){
        pictureId = id;
        AppBluetoothManager.updateBluetoothName();
        saveData();
    }

    public static byte getPictureId(){
        return pictureId;
    }

    public static void setGameName(EditText editText) {
        gameName = editText.getText().toString();
        AppBluetoothManager.updateBluetoothName();
        saveData();
    }

    public static String getGameName() {
        return matchNameToStandards(gameName);
    }

    public static void setFluffyAllowed(boolean allowed){
        fluffyAllowed = allowed;
        saveData();
    }

    public static boolean getFluffyAllowed(){
        return fluffyAllowed;
    }

    public static void setSlimeAllowed(boolean allowed){
        slimeAllowed = allowed;
        saveData();
    }

    public static boolean getSlimeAllowed(){
        return slimeAllowed;
    }

    public static void setGhostAllowed(boolean allowed){
        ghostAllowed = allowed;
        saveData();
    }

    public static boolean getGhostAllowed(){
        return ghostAllowed;
    }

    public static void setNoxAllowed(boolean allowed){
        noxAllowed = allowed;
        saveData();
    }

    public static boolean getNoxAllowed(){
        return noxAllowed;
    }

    public static void setJoinRights(byte mJoinRights){
        joinRights = mJoinRights;
        saveData();
    }

    public static byte getJoinRights(){
        return joinRights;
    }

    public static boolean[]getAllowedPlayerTypes(){
        boolean[] bool = new boolean[4];
        bool[0] = getFluffyAllowed();
        bool[1] = getSlimeAllowed();
        bool[2] = getGhostAllowed();
        bool[3] = getNoxAllowed();
        return bool;
    }

    private static String removeInvalidCharacters(String string){
        for(char c:BluetoothDeviceNameHandling.FORBIDDEN_CHARS){
            string = string.replace(""+c, "");
        }
        if (string.equals(""))string = "string";
        return string;
    }

    public static String matchTextToStandards(String text){
        if (text == null) text = "text";
        int maxLength = BluetoothDeviceNameHandling.MAX_TEXT_LENGTH;
        if (text.length() > maxLength) text = text.substring(0, maxLength);
        text = removeInvalidCharacters(text);
        return text;
    }

    private static String matchNameToStandards(String name){
        if (name == null)name = "name";
        int maxLength = BluetoothDeviceNameHandling.MAX_NAME_LENGTH;
        if (name.length()>maxLength) name = name.substring(0, maxLength);
        name = removeInvalidCharacters(name);
        return name;
    }

}
