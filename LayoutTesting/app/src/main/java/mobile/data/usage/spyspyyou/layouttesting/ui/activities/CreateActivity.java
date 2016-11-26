package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.FocusManagedEditText;
import mobile.data.usage.spyspyyou.layouttesting.utils.BluetoothDeviceNameHandling;

public class CreateActivity extends GotLActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create);
        Button buttonCreate = (Button) findViewById(R.id.button_create);
        final FocusManagedEditText editText = (FocusManagedEditText) findViewById(R.id.editText_create_gameName);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (text.length()> BluetoothDeviceNameHandling.MAX_NAME_LENGTH){
                    editText.setError("Too long.");
                }
                for (char c: BluetoothDeviceNameHandling.FORBIDDEN_CHARS){
                    if (text.indexOf(c)!=-1){
                        editText.setError("Don't use '_' '-' '|' ',' '.'");
                        return;
                    }
                }
                editText.setError(null);
                DataCenter.setGameName(editText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
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
