package testing.gotl.spyspyyo.bluetoothtesting.UI.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import testing.gotl.spyspyyo.bluetoothtesting.R;

public class Start extends GotLActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar a = getSupportActionBar();
        a.setBackgroundDrawable(new ColorDrawable(0x20aaaaaa));
        a.setDisplayShowTitleEnabled(false);
        a.setDisplayHomeAsUpEnabled(true);
    }
}
