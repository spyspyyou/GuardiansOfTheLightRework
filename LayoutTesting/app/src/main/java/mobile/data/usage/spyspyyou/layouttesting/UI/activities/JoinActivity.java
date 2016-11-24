package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.utils.DeviceAdapter;

public class JoinActivity extends GotLActivity {

    private ProgressBar progressBarSearching;
    private ImageButton imageButtonRepeat, imageButtonCancel;
    private TextView textViewInfo;
    private ListView listView;
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        progressBarSearching = (ProgressBar) findViewById(R.id.progressBar_join_searching);
        imageButtonRepeat = (ImageButton) findViewById(R.id.imageButton_join_repeat);
        imageButtonCancel = (ImageButton) findViewById(R.id.imageButton_join_cancel);
        textViewInfo = (TextView) findViewById(R.id.textView_join_info);
        listView = (ListView) findViewById(R.id.listView_join_clients );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_join);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)Log.i("LTest", "Action Bar is null");
        else actionBar.setDisplayHomeAsUpEnabled(true);

        imageButtonRepeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showSearching();
                    }
                });

        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInactive();
            }
        });

        ArrayList<BluetoothDevice> arrayList = AppBluetoothManager.getClientList();
        deviceAdapter = new DeviceAdapter(this, arrayList);
        listView.setAdapter(deviceAdapter);
    }

    private void showSearching(){
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
                    textViewInfo.setText("Searching for games.");
                }
            });

            animatorHideRepeat.start();
        }
    }

    private void showInactive(){
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
                    textViewInfo.setText("Search finished.");
                }
            });
            progressBarSearching.setVisibility(View.INVISIBLE);
            animatorHideCancel.start();
        }
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = true;
        super.onResume();
    }
}
