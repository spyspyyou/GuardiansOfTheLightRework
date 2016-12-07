package mobile.data.usage.spyspyyou.layouttesting.global;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS;

//todo:professionalize the strings;
//todo:exchange toast with Snackbar
public class App implements TODS {

    private static boolean appActive = false;
    private static boolean newActivityStarted = false;

    private static Activity currentActivity;

    //lists with the places to be noticed on big App Events
    private static GlobalTrigger[] onAppTrigger = {
            new DataCenter(),
            new AppBluetoothManager()
    };

    //activity on-calls
    //todo:apply to all activities

    public static void onActivityStarted(Activity newActivity){
        if (appActive)newActivityStarted = true;
        accessActiveActivity(newActivity);
        Log.i("App", "started activity: " + newActivity.toString());
        if (!appActive){
            onAppStart();
        }
    }

    public static void onActivityResumed(){
        if (!appActive)onAppResume();
    }

    public static void onActivityStopped(){
        if (!newActivityStarted)onAppStop();
        newActivityStarted = false;
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data){
        for (GlobalTrigger aE:onAppTrigger){
            aE.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == REQUEST_COARSE_LOCATION_PERMISSION&&grantResults.length>0)AppBluetoothManager.onCoarseLocationPermissionRequestResult(grantResults[0]== PackageManager.PERMISSION_GRANTED);
    }

    //global trigger calls

    private static void onAppStart(){
        Log.i("App", "App starting");
        appActive = true;
        for(GlobalTrigger aE:onAppTrigger){
            aE.onAppStart();
        }
    }

    private static void onAppResume(){
        for(GlobalTrigger aE:onAppTrigger){
            aE.onAppResume();
        }
    }


    private static void onAppStop(){
        Log.i("App", "App stopped");
        appActive = false;
        for(GlobalTrigger aE:onAppTrigger){
            aE.onAppStop();
        }
    }

    public static void toast(String text){
        try {
            Toast.makeText(currentActivity, text, Toast.LENGTH_SHORT).show();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    public static synchronized Activity accessActiveActivity(@Nullable Activity newActivity){
        if (newActivity!=null)currentActivity=newActivity;
        return currentActivity;
    }

    public static Context getContext(){
        return accessActiveActivity(null);
    }

    public static boolean isActive(){
        return appActive;
    }
}
