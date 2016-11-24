package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.FocusManagedEditText;

public class MainActivity extends GotLActivity {

    private DrawerLayout drawerLayout;
    private ImageButton imageButtonProfile, imageButtonSettings, imageButtonJoin, imageButtonCreate, imageButtonFriendList, imageButtonConnections, imageButtonStatisticsSettings;
    private ImageView imageViewProfilePicture;
    private CoordinatorLayout coordinatorLayout;
    private FocusManagedEditText editTextUsername;
    private ListView listViewStatistics;
    private TextView textViewRandInfo;
    private RelativeLayout relativeLayoutProfilePictureSelection, relativeLayoutUserInfo;
    private Button buttonCancelSelection;
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreferences;

    private String profilePictureIdString = "profilePictureId", userNameIdString = "userNameId";
    private int profilePictureId = -1;

    private int[]randInfoStrings = {
            R.string.about,
            R.string.allowed_characters,
            R.string.game_map,
            R.string.join
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

        editTextUsername.on

        initializeButtons();

        sharedPreferences = getPreferences(MODE_PRIVATE);
        profilePictureId = sharedPreferences.getInt(profilePictureIdString, -1);

        initializeProfile();
        setProfileButtonAction();

        //todo: set to the chose profile pic imageViewProfilePicture.setImageDrawable();

        //todo: set to the chosen username editTextUsername.setText();

        //todo: add the items to the list listViewStatistics.setAdapter();
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
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_main);
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
        listViewStatistics = (ListView) findViewById(R.id.listView_main_stats);
        textViewRandInfo = (TextView) findViewById(R.id.textView_main_info);
        relativeLayoutProfilePictureSelection = (RelativeLayout) findViewById(R.id.relativeLayout_profilePicture_selection);
        buttonCancelSelection = (Button) findViewById(R.id.button_main_cancel);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout_profilePicture_options);
        relativeLayoutUserInfo = (RelativeLayout) findViewById(R.id.relativeLayout_main_userInfo);
    }

    private void initializeProfile(){
        if (profilePictureId != -1) {
            final Object object = linearLayout.getChildAt(profilePictureId);
            if (!(object instanceof ImageButton)) return;
            final ImageButton imageButton = (ImageButton) object;
            changeProfilePicture(imageButton.getDrawable(), profilePictureId);
        }
        editTextUsername.setText(sharedPreferences.getString(userNameIdString, ""));
    }

    //todo: finish stats list items, using the stats

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
    protected void onStart() {
        super.onStart();
        setRandText();
    }

    private void setRandText(){
        int rand = new Random().nextInt(randInfoStrings.length-1);
        textViewRandInfo.setText(getResources().getString(randInfoStrings[rand]));
    }

    private void openSelection(){
        relativeLayoutProfilePictureSelection.setVisibility(View.VISIBLE);
    }

    private void closeSelection(){
        relativeLayoutProfilePictureSelection.setVisibility(View.GONE);
    }

    private void changeProfilePicture(Drawable drawable, int picID){
        imageViewProfilePicture.setImageDrawable(drawable);
        profilePictureId = picID;
        sharedPreferences.edit().putInt(profilePictureIdString, picID).apply();
    }

    private void setProfileButtonAction(){
        for (int i = 0; i < linearLayout.getChildCount(); ++i){
            final Object object = linearLayout.getChildAt(i);
            final int y = i;
            if (!(object instanceof ImageButton)){
                continue;
            }
            final ImageButton imageButton = (ImageButton) object;
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeProfilePicture(imageButton.getDrawable(), y);
                    closeSelection();
                }
            });
        }
    }
}
