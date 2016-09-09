package testing.gotl.spyspyyo.bluetoothtesting;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.Nullable;
import android.util.Log;

import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.BluetoothManagerIntern;

/**
 * Created by Sandro on 07/09/2016.
 */
public class App extends Application implements TODS{

    private static boolean newAppFocus = true;
    private static AppEvents[] toInformOnFocus = {
            new BluetoothManagerIntern(),
            //add all necessary ones
    };

    private static AppEvents[] toInformOnLostFocus = {
            new BluetoothManagerIntern(),
            //add all necessary ones
    };

    public static void onAppFocusRestored(){
        for(AppEvents aE:toInformOnFocus){
            aE.onAppStart();
        }
    }

    public static void onAPpFocusLoss(){
        for(AppEvents aE:toInformOnLostFocus){
            aE.onAppStart();
        }
    }
    private static Activity currentActivity;

    public static synchronized Activity  accessActiveActivity(@Nullable Activity newContext){
        if (newContext!=null)currentActivity=newContext;
        return currentActivity;
    }

    public static Context getContext(){
        return accessActiveActivity(null);
    }

    public static boolean wasAppNewlyFocused(){
        return newAppFocus !=(newAppFocus=false);
    }
}
