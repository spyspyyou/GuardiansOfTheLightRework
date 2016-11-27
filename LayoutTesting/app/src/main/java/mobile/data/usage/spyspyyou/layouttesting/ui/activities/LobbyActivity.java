package mobile.data.usage.spyspyyou.layouttesting.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.utils.GameInformation;

public class LobbyActivity extends GotLActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        toolbar = (Toolbar) findViewById(R.id.toolbar_lobby);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar == null) Log.i("LTest", "Action Bar is null");
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close){
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_lobby_join, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //todo:make it real
    public GameInformation getGameInformation(){
        return new GameInformation("testname", "testhost", "testadress", 10, 20, 8, new boolean[]{true, true, false, true});
    }
}
