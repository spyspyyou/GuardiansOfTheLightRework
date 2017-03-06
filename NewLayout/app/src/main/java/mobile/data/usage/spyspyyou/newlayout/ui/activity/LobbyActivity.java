package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import mobile.data.usage.spyspyyou.newlayout.R;

public class LobbyActivity extends GotLActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activityLobby);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_close){
            finish();
        }
        return false;
    }
}
