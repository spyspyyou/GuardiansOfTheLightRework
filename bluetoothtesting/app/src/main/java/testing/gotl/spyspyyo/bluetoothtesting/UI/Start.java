package testing.gotl.spyspyyo.bluetoothtesting.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import testing.gotl.spyspyyo.bluetoothtesting.R;

public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

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
