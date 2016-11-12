package testing.gotl.spyspyyo.bluetoothtesting.global;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.AppBluetoothManager;

/**
 *
 */

//todo:professionalize the strings;
//todo:exchange toast with Snackbar
public class App implements TODS {

    private static boolean appActive = false;
    private static boolean newActivityStarted = false;

    private static Activity currentActivity;

    //lists with the places to be noticed on big App Events
    private static GlobalTrigger[] onAppTrigger = {
            new AppBluetoothManager()
    };

    //activity on-calls
    //todo:apply to all activities

    public static void onActivityStarted(Activity newActivity){
        newActivityStarted = true;
        accessActiveActivity(newActivity);
        if (!appActive)onAppStart();
    }

    public static void onActivityResumed(){
        if (!appActive)onAppResume();
    }

    public static void onActivityStopped(){
        if (!newActivityStarted)onAppStop();
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data){
        for (GlobalTrigger aE:onAppTrigger){
            aE.onActivityResult(requestCode, resultCode, data);
        }
    }

    //global trigger calls

    private static void onAppStart(){
        for(GlobalTrigger aE:onAppTrigger){
            aE.onAppStart();
        }
    }

    private static void onAppResume(){
        appActive = true;
        for(GlobalTrigger aE:onAppTrigger){
            aE.onAppResume();
        }
    }


    private static void onAppStop(){
        appActive = false;
        for(GlobalTrigger aE:onAppTrigger){
            aE.onAppStop();
        }
    }

    public static void toast(String text){
        Toast.makeText(currentActivity, text, Toast.LENGTH_SHORT).show();
    }

    public static synchronized Activity  accessActiveActivity(@Nullable Activity newContext){
        if (newContext!=null)currentActivity=newContext;
        return currentActivity;
    }

    public static Context getContext(){
        return accessActiveActivity(null);
    }

    public static boolean isActive(){
        return appActive;
    }
}
