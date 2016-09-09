package testing.gotl.spyspyyo.bluetoothtesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("App", "StartActivity creating");
        App.accessActiveActivity(this);
        App.onAppFocusRestored();
        setContentView(R.layout.activity_start);
        Log.i("App", "StartActivity created");
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.onAPpFocusLoss();
    }
}
