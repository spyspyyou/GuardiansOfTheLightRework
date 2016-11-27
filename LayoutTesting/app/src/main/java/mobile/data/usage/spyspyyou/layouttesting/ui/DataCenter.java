package mobile.data.usage.spyspyyou.layouttesting.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.global.GlobalTrigger;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.FocusManagedEditText;

public class DataCenter implements GlobalTrigger{

    private static final String
            APP_PREFS = "APP_PREFS",
            PICTURE_ID = "profilePicture",
            USER_NAME = "userName",
            GAME_NAME = "gameName";

    public static final int[] PROFILE_PICTURES = {
            R.drawable.ic_volume_mute_black_36dp,
            R.drawable.ic_fingerprint_black_36dp,
            R.drawable.ic_whatshot_black_36dp,
            R.drawable.ic_warning_black_36dp,
            R.drawable.ic_settings_black_36dp,
            R.drawable.ic_sentiment_satisfied_black_36dp,
            R.drawable.ic_sentiment_neutral_black_36dp,
            R.drawable.ic_sentiment_dissatisfied_black_36dp,
            R.drawable.ic_close_black_36dp,
            R.drawable.ic_notifications_none_black_36dp,
            R.drawable.ic_language_black_36dp,
            R.drawable.ic_share_black_36dp,
            R.drawable.ic_volume_up_black_36dp
    };

    private static SharedPreferences sharedPreferences;

    private final static byte DEFAULT = 0;
    private final static byte HOSTING = 1;
    private final static byte IN_GAME = 2;

    private static byte appStatus = DEFAULT;
    private static byte pictureId = -1;
    private static String userName;
    private static String gameName;


    @Override
    public void onAppStart() {
        initializeDataCenter();
    }

    @Override
    public void onAppResume() {

    }

    @Override
    public void onAppStop() {
        saveData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    private static void initializeDataCenter(){
        sharedPreferences = App.getContext().getApplicationContext().getSharedPreferences(APP_PREFS, Activity.MODE_PRIVATE);
        pictureId = (byte) sharedPreferences.getInt(PICTURE_ID, -1);
        userName = sharedPreferences.getString(USER_NAME, "");
        gameName = sharedPreferences.getString(GAME_NAME, "");
    }

    private static void saveData(){
        if (sharedPreferences == null)return;
        Log.i("DataCenter", "saving data");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PICTURE_ID, pictureId);
        editor.putString(USER_NAME, userName);
        editor.putString(GAME_NAME, gameName);
        editor.commit();
    }

    public static String getUserName(){
        return userName;
    }

    public static void setUserName(FocusManagedEditText editText) {
        userName = editText.getStringText();
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

    public static String getGameName() {
        return gameName;
    }

    //todo:set
    public static void setGameName(FocusManagedEditText editText) {
        gameName = editText.getStringText();
        AppBluetoothManager.updateBluetoothName();
        saveData();
    }
}
