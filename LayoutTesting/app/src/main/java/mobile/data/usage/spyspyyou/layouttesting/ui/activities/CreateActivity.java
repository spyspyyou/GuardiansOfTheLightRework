package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import mobile.data.usage.spyspyyou.layouttesting.R;

public class CreateActivity extends GotLActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private Button buttonCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        toolbar = (Toolbar) findViewById(R.id.toolbar_create);
        buttonCreate = (Button) findViewById(R.id.button_create);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar == null) Log.i("LTest", "Action Bar is null");
        else actionBar.setDisplayHomeAsUpEnabled(true);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), LobbyActivity.class));
            }
        });
    }


    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }
}
