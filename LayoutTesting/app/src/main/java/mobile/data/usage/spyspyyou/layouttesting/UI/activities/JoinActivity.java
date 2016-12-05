package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Notificator;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.GameInformationRequestEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.JoinRequestEvent;
import mobile.data.usage.spyspyyou.layouttesting.utils.DeviceAdapterGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.GameInformation;
import mobile.data.usage.spyspyyou.layouttesting.utils.ViewDataSetters;

public class JoinActivity extends GotLActivity implements Notificator, TODS {

    private ProgressBar progressBarSearching, progressBarGameInfoStatus;

    private Button buttonCancel, buttonJoin;

    private ImageButton imageButtonRepeat, imageButtonCancel;

    private TextView
            textViewInfo,
            textViewGameInfoStatus;

    private static GameInformation currentGameInformation;

    private ListView listView;
    private DrawerLayout drawerLayout;
    private RelativeLayout gameInfoLayout;
    private static Map<String, GameInformation> gameInformationMap;
    private DeviceAdapterGame deviceAdapter;
    private String currentlyDisplayedGame = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        initializeViewVariables();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_join);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)Log.i("LTest", "Action Bar is null");
        else actionBar.setDisplayHomeAsUpEnabled(true);

        imageButtonRepeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        search();
                    }
                });

        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bluetoothDevice = deviceAdapter.getItem(position);
                if (bluetoothDevice != null) showGameInfo(bluetoothDevice);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                new JoinRequestEvent(new String []{currentlyDisplayedGame}).send();
            }
        });
    }

    private void initializeViewVariables(){
        progressBarSearching = (ProgressBar) findViewById(R.id.progressBar_join_searching);
        progressBarGameInfoStatus= (ProgressBar) findViewById(R.id.progressBar_join_statusGameData);
        imageButtonRepeat = (ImageButton) findViewById(R.id.imageButton_join_repeat);
        imageButtonCancel = (ImageButton) findViewById(R.id.imageButton_join_cancel);
        textViewInfo = (TextView) findViewById(R.id.textView_join_info);
        textViewGameInfoStatus = (TextView) findViewById(R.id.textView_join_statusInfoGameData);
        listView = (ListView) findViewById(R.id.listView_join_clients );
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_gameInfo);
        gameInfoLayout = (RelativeLayout) findViewById(R.id.relativeLayout_gameInfo);
        buttonCancel = (Button) findViewById(R.id.button_gameInfo_cancel);
        buttonJoin = (Button) findViewById(R.id.button_gameInfo_join);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<BluetoothDevice> arrayList = AppBluetoothManager.getServerList(this);
        deviceAdapter = new DeviceAdapterGame(this, arrayList);
        listView.setAdapter(deviceAdapter);
        gameInformationMap = new HashMap<>();
    }

    private void search(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int circleRadiusX = imageButtonRepeat.getWidth()/2, circleRadiusY = imageButtonRepeat.getHeight()/2;
            final float finalRadius = (float) Math.hypot(circleRadiusX, circleRadiusY);
            final Animator animatorHideRepeat, animatorRevealCancel;

            animatorRevealCancel = ViewAnimationUtils.createCircularReveal(imageButtonCancel, circleRadiusX, circleRadiusY, 0, finalRadius);
            animatorHideRepeat = ViewAnimationUtils.createCircularReveal(imageButtonRepeat, circleRadiusX, circleRadiusY, finalRadius, 0);
            animatorHideRepeat.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    imageButtonRepeat.setVisibility(View.INVISIBLE);
                    progressBarSearching.setVisibility(View.VISIBLE);
                    imageButtonCancel.setVisibility(View.VISIBLE);
                    animatorRevealCancel.start();
                    textViewInfo.setText(R.string.game_searching_info);
                }
            });

            animatorHideRepeat.start();
        }else {
            imageButtonRepeat.setVisibility(View.INVISIBLE);
            progressBarSearching.setVisibility(View.VISIBLE);
            imageButtonCancel.setVisibility(View.VISIBLE);
            textViewInfo.setText(R.string.game_searching_info);
        }
        ArrayList<BluetoothDevice> arrayList = AppBluetoothManager.getServerList(this);
        deviceAdapter = new DeviceAdapterGame(this, arrayList);
        listView.setAdapter(deviceAdapter);
    }

    private void showGameInfo(BluetoothDevice bluetoothDevice){
        currentlyDisplayedGame = bluetoothDevice.getAddress();
        drawerLayout.openDrawer(GravityCompat.START);
        textViewGameInfoStatus.setText("Connecting to the game Host.");
        if (AppBluetoothManager.connectTo(bluetoothDevice, this)){
            textViewGameInfoStatus.setText("Already connected. Requesting up-to-date gameInfo.");
            new GameInformationRequestEvent(new String[]{currentlyDisplayedGame}).send();
            GameInformation gameInformation = gameInformationMap.get(bluetoothDevice.getAddress());
            if (gameInformation != null){
                ViewDataSetters.setGameInfo(gameInformation, gameInfoLayout);
            } else showGameInfoLoading();
        } else showGameInfoLoading();
    }

    private void showGameInfoLoading(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarGameInfoStatus.setVisibility(View.VISIBLE);
                textViewGameInfoStatus.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setDrawerGameInfo(GameInformation gameInfo) {
        if (gameInfo.getGAME_ADDRESS().equals(currentlyDisplayedGame)) {
            ViewDataSetters.setGameInfo(gameInfo, gameInfoLayout);
        }
        gameInformationMap.put(gameInfo.getGAME_ADDRESS(), gameInfo);
        currentGameInformation = gameInfo;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarGameInfoStatus.setVisibility(View.INVISIBLE);
                textViewGameInfoStatus.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(Gravity.LEFT)) drawerLayout.closeDrawers();
        else super.onBackPressed();
    }

    private void cancel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int circleRadiusX = progressBarSearching.getWidth()/2, circleRadiusY = progressBarSearching.getHeight()/2;
            final float finalRadius = (float) Math.hypot(circleRadiusX, circleRadiusY);
            final Animator animatorRevealRepeat, animatorHideCancel;

            animatorRevealRepeat = ViewAnimationUtils.createCircularReveal(imageButtonRepeat, circleRadiusX, circleRadiusY, 0, finalRadius);
            animatorHideCancel = ViewAnimationUtils.createCircularReveal(imageButtonCancel, circleRadiusX, circleRadiusY, finalRadius, 0);
            animatorHideCancel.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    imageButtonCancel.setVisibility(View.INVISIBLE);
                    imageButtonRepeat.setVisibility(View.VISIBLE);
                    animatorRevealRepeat.start();
                    textViewInfo.setText(R.string.search_finished_info);
                }
            });
            progressBarSearching.setVisibility(View.INVISIBLE);
            animatorHideCancel.start();
        }else{
            imageButtonCancel.setVisibility(View.INVISIBLE);
            imageButtonRepeat.setVisibility(View.VISIBLE);
            textViewInfo.setText(R.string.search_finished_info);
        }
        AppBluetoothManager.cancelSearch();
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = true;
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawerLayout.closeDrawers();
        cancel();
        if (App.accessActiveActivity(null) instanceof LobbyClientActivity) {
            AppBluetoothManager.disconnectExcept(currentlyDisplayedGame);
        }else{
            AppBluetoothManager.disconnect();
        }
    }

    @Override
    public void onConnectionLost() {

    }

    @Override
    public void notifyChange() {
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void discoveryFinished() {
        cancel();
    }

    @Override
    public void connectionRequestResult(BluetoothDevice bluetoothDevice) {
        if (App.accessActiveActivity(null) == this){
            if(bluetoothDevice == null){
                //todo:snackbar info
            }else {
                new GameInformationRequestEvent(new String[]{bluetoothDevice.getAddress()}).send();
                if (bluetoothDevice.getAddress().equals(currentlyDisplayedGame)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewGameInfoStatus.setText("Connected to device. Requesting up-to-date gameInfo.");
                        }
                    });
                }
            }
        }
    }

    public static GameInformation getCurrentGameInformation(){
        return currentGameInformation;
    }
}
