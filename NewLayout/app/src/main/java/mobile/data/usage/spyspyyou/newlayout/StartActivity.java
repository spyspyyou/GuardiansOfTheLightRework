package mobile.data.usage.spyspyyou.newlayout;

import android.content.res.Configuration;
import android.os.Bundle;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StartActivity extends AppCompatActivity {

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
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
                //todo:start  the lobby
                Snackbar.make(view, "Starting the game lobby", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        if (actionBar == null)return;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

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
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.start_pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
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
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        private String[] tabNames = {
                "Create",
                "Search",
                "Worlds"
        };

        /*package*/ DemoCollectionPagerAdapter(FragmentManager fm) {
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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tab_create, container, false);
        }
    }

    public static class SearchFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.tab_search, container, false);
            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Snackbar.make(swipeRefreshLayout, "On Refresh", Snackbar.LENGTH_LONG)
                            .setAction("Stop Refresh", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }).show();
                }
            });
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            return v;
        }
    }

    public static class WorldsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tab_worlds, container, false);
        }
    }
}
