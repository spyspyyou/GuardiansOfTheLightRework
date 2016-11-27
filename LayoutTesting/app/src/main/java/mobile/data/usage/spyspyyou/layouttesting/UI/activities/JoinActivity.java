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
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.Connection;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.DeviceFoundNotificator;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.GameInformationRequestEvent;
import mobile.data.usage.spyspyyou.layouttesting.utils.DeviceAdapterGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.GameInformation;

public class JoinActivity extends GotLActivity implements DeviceFoundNotificator{

    private ProgressBar progressBarSearching, progressBarGameInfoStatus;

    private Button buttonCancel, buttonJoin;

    private ImageButton imageButtonRepeat, imageButtonCancel;

    private TextView
            textViewInfo,
            textViewGameInfoStatus,
            textViewGameName,
            textViewUsername,
            textViewWidth,
            textViewHeight,
            textViewPlayerCount;

    private ImageView
            imageViewCrossFluffy,
            imageViewCrossSlime,
            imageViewCrossGhost,
            imageViewCrossNox;

    private ListView listView;
    DrawerLayout drawerLayout;
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
        textViewGameName = (TextView) findViewById(R.id.textView_gameInfo_gameName);
        textViewUsername = (TextView) findViewById(R.id.textView_gameInfo_hostData);
        textViewWidth = (TextView) findViewById(R.id.textView_gameInfo_widthData);
        textViewHeight = (TextView) findViewById(R.id.textView_gameInfo_heightData);
        textViewPlayerCount = (TextView) findViewById(R.id.textView_gameInfo_playerCountData);
        imageViewCrossFluffy = (ImageView) findViewById(R.id.imageButton_gameInfo_crossFluffy);
        imageViewCrossSlime = (ImageView) findViewById(R.id.imageButton_gameInfo_crossSlime);
        imageViewCrossGhost = (ImageView) findViewById(R.id.imageButton_gameInfo_crossGhost);
        imageViewCrossNox = (ImageView) findViewById(R.id.imageButton_gameInfo_crossNox);
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
        Connection connection = AppBluetoothManager.connectTo(bluetoothDevice, this);
        if (connection != null){
            new GameInformationRequestEvent(new  Connection[]{connection}).send();
        }
        GameInformation gameInformation = gameInformationMap.get(bluetoothDevice.getAddress());
        if (gameInformation != null){
            setDrawerGameInfo(gameInformation);
        }
    }

    public void setDrawerGameInfo(final GameInformation gameInfo){
        if (gameInfo.GAME_ADDRESS.equals(currentlyDisplayedGame)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarGameInfoStatus.setVisibility(View.INVISIBLE);
                    textViewGameInfoStatus.setVisibility(View.INVISIBLE);
                    textViewGameName.setText(gameInfo.GAME_NAME);
                    textViewUsername.setText(gameInfo.GAME_HOST);
                    textViewWidth.setText(gameInfo.WIDTH);
                    textViewHeight.setText(gameInfo.HEIGHT);
                    textViewPlayerCount.setText(gameInfo.PLAYER_COUNT);

                    boolean[]b = gameInfo.PLAYER_TYPES;
                    if(b[0])imageViewCrossFluffy.setVisibility(View.VISIBLE);
                    else imageViewCrossFluffy.setVisibility(View.INVISIBLE);
                    if(b[1])imageViewCrossSlime.setVisibility(View.VISIBLE);
                    else imageViewCrossSlime.setVisibility(View.INVISIBLE);
                    if(b[2])imageViewCrossGhost.setVisibility(View.VISIBLE);
                    else imageViewCrossGhost.setVisibility(View.INVISIBLE);
                    if(b[3])imageViewCrossNox.setVisibility(View.VISIBLE);
                    else imageViewCrossNox.setVisibility(View.INVISIBLE);
                }
            });
        }
        gameInformationMap.put(gameInfo.GAME_ADDRESS, gameInfo);
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
    public void notifyChange() {
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void discoveryFinished() {
        cancel();
    }

    @Override
    public void connectionRequestResult(Connection connection) {
        if (App.accessActiveActivity(null) == this)new GameInformationRequestEvent(new  Connection[]{connection}).send();
    }
}
