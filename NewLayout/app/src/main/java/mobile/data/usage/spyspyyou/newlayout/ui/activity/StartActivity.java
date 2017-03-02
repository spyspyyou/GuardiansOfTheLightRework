package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.tab_create, container, false);

            toggleButtonRandom = (ToggleButton) view.findViewById(R.id.toggleButton_tabCreate_random);
            toggleButtonLibrary = (ToggleButton) view.findViewById(R.id.toggleButton_tabCreate_library);
            final ViewSwitcher viewSwitcher = (ViewSwitcher) view.findViewById(R.id.viewSwitcher_tabCreate);

            Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
            viewSwitcher.setInAnimation(in);
            viewSwitcher.setOutAnimation(out);

            toggleButtonRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        toggleButtonLibrary.setChecked(false);
                        viewSwitcher.showPrevious();
                    }
                    else if (!toggleButtonLibrary.isChecked())toggleButtonRandom.setChecked(true);
                }
            });

            toggleButtonLibrary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        toggleButtonRandom.setChecked(false);
                        viewSwitcher.showNext();
                    }
                    else if (!toggleButtonRandom.isChecked())toggleButtonLibrary.setChecked(true);
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
