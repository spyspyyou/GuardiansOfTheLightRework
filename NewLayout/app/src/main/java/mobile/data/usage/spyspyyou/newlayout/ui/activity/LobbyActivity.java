package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.ui.adapters.ChatAdapter;
import mobile.data.usage.spyspyyou.newlayout.ui.adapters.TeamAdapter;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.ChatMessage;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.TeamRequest;

public abstract class LobbyActivity extends GotLActivity {

    private AppBluetoothManager.BluetoothActionListener btStopListener = new AppBluetoothManager.BluetoothActionListener() {
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
    protected static TeamAdapter
            teamBlue,
            teamGreen;
    private DrawerLayout chatDrawer;
    protected EditText editTextMessage;
    public static boolean HOST = false;
    private static ChatAdapter chatAdapter;
    protected static String HOST_ADDRESS;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        sharedPreferences = getSharedPreferences(StartActivity.PREF_PROFILE, MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activityLobby);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_chat_bubble_outline_white_24dp);
        }

        ListView
                listBlue = (ListView) findViewById(R.id.listView_activityLobby_blue),
                listGreen = (ListView) findViewById(R.id.listView_activityLobby_green),
                listChat = (ListView) findViewById(R.id.listView_activityLobby_chat);

        editTextMessage = (EditText) findViewById(R.id.editText_activityLobby_message);

        teamBlue = new TeamAdapter(this, true);
        teamGreen = new TeamAdapter(this, false);
        chatAdapter = new ChatAdapter(this);
        listBlue.setAdapter(teamBlue);
        listGreen.setAdapter(teamGreen);
        listChat.setAdapter(chatAdapter);
        listChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextMessage.setFocusable(false);
                return true;
            }
        });

        chatDrawer = (DrawerLayout) findViewById(R.id.drawerLayout_activityLobby);
        editTextMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)chatDrawer.openDrawer(Gravity.LEFT);
                else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);
                }
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, chatDrawer, toolbar, R.string.hello_world, R.string.hello_world){
            @Override
            public void onDrawerClosed(View drawerView) {
                editTextMessage.setFocusable(false);
                super.onDrawerClosed(drawerView);
            }
        };
        chatDrawer.addDrawerListener(drawerToggle);
        ImageButton
                imageButtonSend = (ImageButton) findViewById(R.id.imageButton_activityLobby_send),
                imageButtonBlue = (ImageButton) findViewById(R.id.imageButton_activityLobby_blue),
                imageButtonGreen = (ImageButton) findViewById(R.id.imageButton_activityLobby_green);
        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextMessage.getText().length() > 0) send();
            }
        });
        imageButtonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TeamRequest(true).send(HOST_ADDRESS);
            }
        });
        imageButtonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TeamRequest(false).send(HOST_ADDRESS);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBluetoothManager.addBluetoothListener(btStopListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBluetoothManager.removeBluetoothListener(btStopListener);
        AppBluetoothManager.releaseRequirements(this);
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
        if (item.getItemId() == R.id.menu_close)finish();
        if (item.getItemId() == android.R.id.home)chatDrawer.openDrawer(Gravity.LEFT);
        return false;
    }

    public static void messageReceived(ChatMessage chatMessage){
        chatAdapter.addMessage(chatMessage);
        if (HOST){
            chatMessage.send(teamBlue.getPlayerAddresses());
            chatMessage.send(teamGreen.getPlayerAddresses());
        }
    }

    protected void startSelection(){

    }

    protected abstract void send();
}
