package testing.gotl.spyspyyo.bluetoothtesting.global;

import android.content.Intent;

/**
 * Created by Sandro on 08/09/2016.
 */
public interface GlobalTrigger {

    void onAppStart();

    void onAppResume();

    void onAppStop();

    void onActivityResult(int requestCode, int resultCode, Intent data);

}
