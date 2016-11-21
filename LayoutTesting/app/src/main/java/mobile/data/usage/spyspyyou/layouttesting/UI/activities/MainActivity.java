package mobile.data.usage.spyspyyou.layouttesting.UI.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Space;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.UI.views.FocusManagedEditText;

public class MainActivity extends GotLActivity {

    private DrawerLayout drawerLayout;
    private ImageButton imageButtonProfile, imageButtonSettings, imageButtonJoin, imageButtonCreate, imageButtonFriendList, imageButtonConnections, imageButtonStatisticsSettings;
    private ImageView imageViewProfilePicture;
    private CoordinatorLayout coordinatorLayout;
    private FocusManagedEditText editTextUsername;
    private ListView listViewStatistics;
    private Space space;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initializeViewVariables();

        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() != R.id.editText_main_username){
                    editTextUsername.releaseFocus();
                }
                return false;
            }
        });

        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drawerLayout.openDrawer(Gravity.LEFT);
                    }
                });

        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:open settings
            }
        });

        imageButtonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), JoinActivity.class));
            }

        });
        imageButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:open Create Activity
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

        //todo: set to the chose profile pic imageViewProfilePicture.setImageDrawable();

        //todo: set to the chosen username editTextUsername.setText();

        //todo: add the items to the list listViewStatistics.setAdapter();
    }

    private void initializeViewVariables(){
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_main);
        space = (Space) findViewById(R.id.space_main);
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
    }

    //todo: finish stats list items, using the stats
    //todo: make v21+ layouts

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
