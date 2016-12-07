package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.FocusManagedEditText;
import mobile.data.usage.spyspyyou.layouttesting.utils.EditTextTextWatcher;

public class MainActivity extends GotLActivity {

    private DrawerLayout drawerLayout;
    private ImageButton
            imageButtonProfile,
            imageButtonSettings,
            imageButtonJoin,
            imageButtonCreate,
            imageButtonFriendList,
            imageButtonConnections,
            imageButtonStatisticsSettings;
    private ImageView imageViewProfilePicture;
    private FocusManagedEditText editTextUsername;
    private TextView textViewRandInfo;
    private RelativeLayout
            relativeLayoutProfilePictureSelection;
    private Button buttonCancelSelection;
    private LinearLayout linearLayout;

    private int[]randInfoStrings = {
            R.string.about,
            R.string.allowed_characters,
            R.string.game_map,
            R.string.join,
            R.string.app_name
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initializeViewVariables();

        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() != R.id.editText_main_username)editTextUsername.releaseFocus();
                return false;
            }
        });

        editTextUsername.addTextChangedListener(new EditTextTextWatcher(editTextUsername, EditTextTextWatcher.USERNAME));

        initializeButtons();
        initializeProfilePicButtons();

        //todo: finish stats list items, using the stats
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

    @Override
    public void onConnectionLost() {}

    @Override
    protected void onStart() {
        super.onStart();
        AppBluetoothManager.checkBluetoothOn();
        setRandText();
        initializeProfile();
    }

    private void initializeButtons(){
        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:add settings link
                // startActivity(new Intent(getBaseContext(), CreateActivity.class))
            }
        });

        imageButtonStatisticsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:add stats settings link
            }
        });

        imageButtonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {startActivity(new Intent(getBaseContext(), JoinActivity.class));
            }

        });

        imageButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {startActivity(new Intent(getBaseContext(), CreateActivity.class));
            }
        });

        imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelection();
            }
        });

        imageButtonFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:open friendListFragment
            }
        });

        imageButtonConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:open connectionsFragment
            }
        });

        buttonCancelSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSelection();
            }
        });
    }

    private void initializeViewVariables(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_profile);
        imageButtonProfile = (ImageButton) findViewById(R.id.imageButton_main_profile);
        imageButtonSettings = (ImageButton) findViewById(R.id.imageButton_main_settings);
        imageButtonJoin = (ImageButton) findViewById(R.id.imageButton_main_join);
        imageButtonCreate = (ImageButton) findViewById(R.id.imageButton_main_create);
        imageButtonFriendList = (ImageButton) findViewById(R.id.imageButton_main_friend_list);
        imageButtonConnections = (ImageButton) findViewById(R.id.imageButton_main_connections);
        imageButtonStatisticsSettings = (ImageButton) findViewById(R.id.imageButton_main_statistics_settings);
        imageViewProfilePicture = (ImageView) findViewById(R.id.imageView_main_profilePicture);
        editTextUsername = (FocusManagedEditText) findViewById(R.id.editText_main_username);
        textViewRandInfo = (TextView) findViewById(R.id.textView_main_info);
        relativeLayoutProfilePictureSelection = (RelativeLayout) findViewById(R.id.relativeLayout_profilePicture_selection);
        buttonCancelSelection = (Button) findViewById(R.id.button_main_cancel);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout_profilePicture_options);
    }

    private void initializeProfile(){
        byte profilePictureId = DataCenter.getPictureId();
        Log.i("MainAc", "profile pic id is: " + profilePictureId);
        if (profilePictureId != -1) {
            changeProfilePicture(getResources().getDrawable(DataCenter.PROFILE_PICTURES[profilePictureId]), profilePictureId);
        }
        editTextUsername.setText(DataCenter.getUserName());
    }

    private void initializeProfilePicButtons(){
        for (byte i = 0; i < DataCenter.PROFILE_PICTURES.length; ++i){
            final byte y = i;
            LinearLayout buttonParent = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.button_profile_pic_selection, null);
            final ImageButton imageButton = (ImageButton) buttonParent.findViewById(R.id.imageButton_selectProfilePic);
            try {
                imageButton.setImageDrawable(getResources().getDrawable(DataCenter.PROFILE_PICTURES[i]));
            }catch (Exception e){
                e.printStackTrace();
            }
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("MainAc", "chosen Pb with id: " + y);
                    changeProfilePicture(imageButton.getDrawable(), y);
                    closeSelection();
                }
            });
            linearLayout.addView(buttonParent);
        }
    }

    private void setRandText(){
        int rand = new Random().nextInt(randInfoStrings.length-1);
        textViewRandInfo.setText(getResources().getString(randInfoStrings[rand]));
    }

    private void openSelection(){
        relativeLayoutProfilePictureSelection.setVisibility(View.VISIBLE);
        relativeLayoutProfilePictureSelection.bringToFront();
    }

    private void closeSelection(){
        relativeLayoutProfilePictureSelection.setVisibility(View.GONE);
    }

    private void changeProfilePicture(Drawable drawable, byte picID){
        imageViewProfilePicture.setImageDrawable(drawable);
        DataCenter.setPictureId(picID);
    }
}
