package testing.gotl.spyspyyo.bluetoothtesting.UI;

import android.app.Activity;
import android.content.Intent;

public class GotLActivity extends Activity{

    @Override
    protected void onStart() {
        super.onStart();
        App.onActivityStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.onActivityResumed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.onActivityStopped();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        App.onActivityResult(requestCode, resultCode, data);
    }

}
