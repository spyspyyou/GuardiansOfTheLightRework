package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;
import mobile.data.usage.spyspyyou.newlayout.game.World;
import mobile.data.usage.spyspyyou.newlayout.ui.adapters.GameInformationAdapter;

public class StartActivity extends GotLActivity {

    PagerAdapter mPagerAdapter;
    ViewPager mViewPager;
    ActionBarDrawerToggle mDrawerToggle;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        final FloatingActionButton fabCreate = (FloatingActionButton) findViewById(R.id.fab_create);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), ServerLobbyActivity.class));
            }
        });

        final FloatingActionButton fabWorlds = (FloatingActionButton) findViewById(R.id.fab_worlds);
        fabWorlds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:start  the lobby
                Snackbar.make(view, "Create a new World", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.hello_world, R.string.hello_world) {
            float  lastOffset = 0;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > lastOffset) {
                    fabCreate.hide();
                    fabWorlds.hide();
                }else{
                    if (tabLayout.getSelectedTabPosition() == 0)fabCreate.show();
                    if (tabLayout.getSelectedTabPosition() == 2)fabWorlds.show();
                }
                lastOffset = slideOffset;
            }

            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.start_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)fabCreate.show();
                else fabCreate.hide();

                if (position == 2)fabWorlds.show();
                else fabWorlds.hide();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public class PagerAdapter extends FragmentStatePagerAdapter {
        private String[] tabNames = {
                "Create",
                "Search",
                "Worlds"
        };

        /*package*/ PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0)
                return new CreateFragment();
            else if (i == 1)
                return new SearchFragment();
            return new WorldsFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= tabNames.length)return "";
            return tabNames [position];
        }
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class CreateFragment extends Fragment {

        private static final String
                CREATE_PREFS = "createPrefs",
                PREF_WORLD = "worldPref",
                PREF_SIZE = "size",
                PREF_WALL_RATIO = "wallRatio",
                PREF_VOID_RATIO = "voidRatio";

        private ToggleButton
                toggleButtonRandom,
                toggleButtonLibrary;

        private SeekBar
                seekBarSize,
                seekBarWallRatio,
                seekBarVoidRatio,
                seekBarPlayerMaximum,
                seekBarSweetRegen,
                seekBarManaRegen,
                seekBarSelectionTime;

        private TextView
                textViewSize,
                textViewWallRatio,
                textViewVoidRatio,
                textViewPlayerMaximum,
                textViewSweetRegen,
                textViewManaRegen,
                textViewSelectionTime;

        private Switch switchUniqueCharacters;

        private ImageButton
                imageButtonFluffy,
                imageButtonSlime,
                imageButtonGhost,
                imageButtonNox;

        private SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(CREATE_PREFS, Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            final View view = inflater.inflate(R.layout.tab_create, container, false);

            toggleButtonRandom = (ToggleButton) view.findViewById(R.id.toggleButton_tabCreate_random);
            toggleButtonLibrary = (ToggleButton) view.findViewById(R.id.toggleButton_tabCreate_library);
            final LinearLayout
                    linearLayoutRandom = (LinearLayout) view.findViewById(R.id.linearLayout_tabCreate_random),
                    linearLayoutList = (LinearLayout) view.findViewById(R.id.linearLayout_tabCreate_list);

            toggleButtonRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        toggleButtonLibrary.setChecked(false);
                        linearLayoutRandom.setVisibility(View.VISIBLE);
                        linearLayoutList.setVisibility(View.GONE);
                        editor.putBoolean(PREF_WORLD, false);
                        editor.apply();
                    }
                    else if (!toggleButtonLibrary.isChecked())toggleButtonRandom.setChecked(true);
                }
            });

            toggleButtonLibrary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        toggleButtonRandom.setChecked(false);
                        linearLayoutRandom.setVisibility(View.GONE);
                        linearLayoutList.setVisibility(View.VISIBLE);
                        editor.putBoolean(PREF_WORLD, true);
                        editor.apply();
                    }
                    else if (!toggleButtonRandom.isChecked())toggleButtonLibrary.setChecked(true);
                }
            });

            if (!sharedPreferences.getBoolean(PREF_WORLD, false)){
                toggleButtonRandom.setChecked(true);
            }else{
                toggleButtonLibrary.setChecked(true);
            }

            textViewSize = (TextView) view.findViewById(R.id.textView_tabCreate_size);
            seekBarSize = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_size);
            seekBarSize.setMax(World.MAX_SIZE - World.MIN_SIZE);
            seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewSize.setText("" + (progress + World.MIN_SIZE));
                    editor.putInt(PREF_SIZE, progress);
                    editor.apply();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBarSize.setProgress(sharedPreferences.getInt(PREF_SIZE, 40));

            textViewWallRatio = (TextView) view.findViewById(R.id.textView_tabCreate_wallRatio);
            seekBarWallRatio = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_wallRatio);
            seekBarWallRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewWallRatio.setText("" + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            textViewVoidRatio = (TextView) view.findViewById(R.id.textView_tabCreate_voidRatio);
            seekBarVoidRatio = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_voidRatio);
            seekBarVoidRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewVoidRatio.setText(""+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            textViewPlayerMaximum = (TextView) view.findViewById(R.id.textView_tabCreate_playerMaximum);
            seekBarPlayerMaximum = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_playerMaximum);
            seekBarPlayerMaximum.setMax(7);
            seekBarPlayerMaximum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewPlayerMaximum.setText("" + (progress + 1));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            textViewSweetRegen = (TextView) view.findViewById(R.id.textView_tabCreate_sweetRegen);
            seekBarSweetRegen = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_sweetRegen);
            seekBarSweetRegen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewSweetRegen.setText("" + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            textViewManaRegen = (TextView) view.findViewById(R.id.textView_tabCreate_manaRegen);
            seekBarManaRegen = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_manaRegen);
            seekBarManaRegen.setMax(200);
            seekBarManaRegen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewManaRegen.setText("" + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            textViewSelectionTime = (TextView) view.findViewById(R.id.textView_tabCreate_selectionTime);
            seekBarSelectionTime = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_selectionTime);
            seekBarSelectionTime.setMax(20);
            seekBarSelectionTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewSelectionTime.setText("" + progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


            return view;
        }

        public GameInformation getGameInformation(){
            return null;
        }
    }

    public static class SearchFragment extends Fragment {

        private ListView gameList;
        private GameInformationAdapter adapter;
        private TextView textViewInfo;
        private ImageButton imageButtonSearch;
        private SwipeRefreshLayout swipeRefreshLayout;

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            adapter = new GameInformationAdapter(getActivity().getApplicationContext());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.tab_search, container, false);

            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout_tabSearch);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                refresh();
                }
            });
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

            gameList = (ListView) v.findViewById(R.id.listView_tabSearch);
            gameList.setAdapter(adapter);

            AppBluetoothManager.addBluetoothListener(new AppBluetoothManager.BluetoothActionListener() {

                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {

                }

                @Override
                public void onGameSearchStarted() {
                    swipeRefreshLayout.setRefreshing(true);
                }

                @Override
                public void onGameSearchFinished() {
                    swipeRefreshLayout.setRefreshing(false);
                    if (adapter.getCount() == 0){
                        Snackbar.make(swipeRefreshLayout, "No Games Found", Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        refresh();
                                    }
                                }).show();
                        showStartSearch();
                    }
                }

                @Override
                public void onConnectionEstablished(String address) {

                }
            });

            imageButtonSearch = (ImageButton) v.findViewById(R.id.imageButton_tabSearch);
            imageButtonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh();
                }
            });

            textViewInfo = (TextView) v.findViewById(R.id.textView_tabSearch);
            return v;
        }

        private void refresh(){
            if (adapter.setData(AppBluetoothManager.searchGames(getActivity()))) {
                adapter.notifyDataSetChanged();
                hideStartSearch();
            }else{
                showStartSearch();
            }
        }

        private void showStartSearch(){
            gameList.setVisibility(View.INVISIBLE);
            imageButtonSearch.setVisibility(View.VISIBLE);
            textViewInfo.setVisibility(View.VISIBLE);
        }

        private void hideStartSearch(){
            gameList.setVisibility(View.VISIBLE);
            imageButtonSearch.setVisibility(View.INVISIBLE);
            textViewInfo.setVisibility(View.INVISIBLE);
        }
    }

    public static class WorldsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tab_worlds, container, false);
        }
    }
}
