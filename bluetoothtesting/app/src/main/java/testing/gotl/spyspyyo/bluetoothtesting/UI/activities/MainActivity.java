package testing.gotl.spyspyyo.bluetoothtesting.UI.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import testing.gotl.spyspyyo.bluetoothtesting.R;
import testing.gotl.spyspyyo.bluetoothtesting.global.App;

public class MainActivity extends GotLActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar a = getSupportActionBar();
        if (a==null){
            App.toast("damn it");
        }
        a.setSubtitle("subbing");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.testing_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = true;
        super.onResume();
    }
}
