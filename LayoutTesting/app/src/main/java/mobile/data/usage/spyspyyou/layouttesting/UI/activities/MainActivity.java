package mobile.data.usage.spyspyyou.layouttesting.UI.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.App;

public class MainActivity extends GotLActivity {

    DrawerLayout drawerLayout;
    TextInputEditText textInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {

            }
        });
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        textInputEditText = (TextInputEditText) findViewById(R.id.username_input);
        textInputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    Log.i("MainActivity", "Enter geklickt");
                    textInputEditText.clearFocus();
                    return true;
                }
                return false;
            }
        });
        myToolbar.setTitle("Guardians of the Light");
        setSupportActionBar(myToolbar);
        ActionBar a = getSupportActionBar();
        if (a==null){
            App.toast("damn it");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.testing_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_option){
            //todo:start the options activity
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(Gravity.LEFT)) drawerLayout.closeDrawers();
        else super.onBackPressed();
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = true;
        super.onResume();
    }
}
