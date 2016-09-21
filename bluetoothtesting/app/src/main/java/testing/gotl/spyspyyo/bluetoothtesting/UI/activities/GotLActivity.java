package testing.gotl.spyspyyo.bluetoothtesting.UI.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import testing.gotl.spyspyyo.bluetoothtesting.global.App;

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
}
