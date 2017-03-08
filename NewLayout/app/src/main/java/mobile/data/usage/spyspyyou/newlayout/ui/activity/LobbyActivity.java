package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;

public class LobbyActivity extends GotLActivity {

    private AppBluetoothManager.BluetoothActionListener listener = new AppBluetoothManager.BluetoothActionListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {
            Toast.makeText(getBaseContext(), "Bluetooth is required.", Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        public void onGameSearchStarted() {

        }

        @Override
        public void onGameSearchFinished() {

        }

        @Override
        public void onConnectionEstablished(String address) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activityLobby);
        setSupportActionBar(toolbar);

        final LinearLayout
                linearLayoutToggleChat = (LinearLayout) findViewById(R.id.linearLayout_activityLobby_toggle),
                linearLayoutChar = (LinearLayout) findViewById(R.id.linearLayout_activityLobby_chat);
        final ImageView imageViewArrow = (ImageView) findViewById(R.id.imageView_activityLobby_arrow);
        imageViewArrow.setRotation(180);
        linearLayoutToggleChat.setOnTouchListener(new View.OnTouchListener() {
            private boolean extended = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && event.getY() > 0 && event.getY() < linearLayoutToggleChat.getHeight()){
                    extended = !extended;
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayoutChar.getLayoutParams();
                    if (extended){
                        params.weight = 1;
                        imageViewArrow.animate().rotation(0).start();
                    }else{
                        params.weight = 4;
                        imageViewArrow.animate().rotation(180).start();
                    }
                    linearLayoutChar.setLayoutParams(params);
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBluetoothManager.addBluetoothListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBluetoothManager.removeBluetoothListener(listener);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Abort Game!")
                .setMessage("Are you sure that you want to leave the lobby?")
                .setPositiveButton("Yep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Nopes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }
        ).show();
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
