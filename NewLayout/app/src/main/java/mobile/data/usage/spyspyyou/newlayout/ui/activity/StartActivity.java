package mobile.data.usage.spyspyyou.newlayout.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;
import mobile.data.usage.spyspyyou.newlayout.game.World;
import mobile.data.usage.spyspyyou.newlayout.game.WorldVars;
import mobile.data.usage.spyspyyou.newlayout.ui.adapters.GameInformationAdapter;
import mobile.data.usage.spyspyyou.newlayout.ui.views.ToggleImageButton;

import static mobile.data.usage.spyspyyou.newlayout.game.WorldVars.BASE_MAP;

public class StartActivity extends GotLActivity {

    private ActionBarDrawerToggle drawerToggle;
    private static GameInformation gameInformation;
    private CreateFragment createFragment;
    private SearchFragment searchFragment;
    private EditText editTextUserName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activityStart);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout_activityStart);


        final FloatingActionButton fabCreate = (FloatingActionButton) findViewById(R.id.fab_create);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameInformation = createFragment.getGameInformation();
                startActivity(new Intent(getBaseContext(), ServerLobbyActivity.class));
            }
        });

        final FloatingActionButton fabWorlds = (FloatingActionButton) findViewById(R.id.fab_worlds);
        fabWorlds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:start world edit
                Snackbar.make(view, "In dev", Snackbar.LENGTH_LONG).show();
            }
        });

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextUserName = (EditText) findViewById(R.id.editText_drawerProfile);
        editTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextUserName.getWindowToken(), 0);
                }
            }
        });

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.hello_world, R.string.hello_world) {
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

            @Override
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
                editTextUserName.setFocusable(false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager_activityStart);
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
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof CreateFragment) createFragment = (CreateFragment) fragment;
        if (fragment instanceof SearchFragment) searchFragment = (SearchFragment) fragment;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBluetoothManager.releaseRequirements(getBaseContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppBluetoothManager.REQUEST_BLUETOOTH){
            if (resultCode == RESULT_CANCELED)
                Toast.makeText(getBaseContext(), "Bluetooth is required.", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppBluetoothManager.REQUEST_CL_PERMISSION && grantResults.length > 0 && permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            searchFragment.refresh();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
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

    public static class CreateFragment extends Fragment {

        private static final String
                CREATE_PREFS = "createPrefs",
                PREF_GAME_NAME = "gameName",
                PREF_WORLD = "worldPref",
                PREF_SIZE = "size",
                PREF_WALL_RATIO = "wallRatio",
                PREF_VOID_RATIO = "voidRatio",
                PREF_UNIQUE_CHAR = "uniqueChar",
                PREF_ALLOWED_FLUFFY = "allowedFluffy",
                PREF_ALLOWED_SLIME = "allowedSlime",
                PREF_ALLOWED_GHOST = "allowedGhost",
                PREF_ALLOWED_NOX = "allowedNox",
                PREF_PLAYER_MAX = "playerMax",
                PREF_SWEET_REGEN = "sweetRegen",
                PREF_MANA_REGEN = "manaRegen",
                PREF_SELECTION_TIME = "selectionTime";

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

        private Switch
                switchUniqueCharacters;

        private ImageButton
                imageButtonRefresh,
                imageButtonEditWorld;

        private ToggleImageButton
                imageButtonFluffy,
                imageButtonSlime,
                imageButtonGhost,
                imageButtonNox;

        private ImageView
                imageViewWorld;

        private EditText
                editTextGameName;

        private ProgressBar
                progressBarWorldLoading;

        private LinearLayout
                linearLayoutRandom,
                linearLayoutList;

        private SharedPreferences
                sharedPreferences;

        private SharedPreferences.Editor
                editor;

        private World
                world = new World(BASE_MAP);

        private Runnable
                worldGenerator = new Runnable() {
                    @Override
                    public void run() {
                        byte[][] baseWorld = BASE_MAP.clone();
                        Random random = new Random(System.currentTimeMillis());
                        for (int y = 3; y < baseWorld[0].length - 3; ++y){
                            for (int x = 3; x < baseWorld.length - 3; ++x){
                                int newRand = random.nextInt(5);
                                if (newRand == 0)baseWorld[x][y] = WorldVars.VOID;
                                else if (newRand == 1)baseWorld[x][y] = WorldVars.WALL;
                                else baseWorld[x][y] = WorldVars.FLOOR;
                            }
                        }
                        world = new World(baseWorld);
                        Activity activity = getActivity();
                        if (activity != null) activity.runOnUiThread(setWorldImage);
                    }
                },
                setWorldImage = new Runnable() {
                    @Override
                    public void run() {
                        if (toggleButtonRandom.isChecked()){
                            imageViewWorld.setImageBitmap(world.getBitmapRepresentation());
                            progressBarWorldLoading.setVisibility(View.GONE);
                        }
                    }
                };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(CREATE_PREFS, Activity.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            final View view = inflater.inflate(R.layout.tab_create, container, false);

            toggleButtonRandom = (ToggleButton) view.findViewById(R.id.toggleButton_tabCreate_random);
            toggleButtonLibrary = (ToggleButton) view.findViewById(R.id.toggleButton_tabCreate_library);

            imageViewWorld = (ImageView) view.findViewById(R.id.worldView_tabCreate_world);

            linearLayoutRandom = (LinearLayout) view.findViewById(R.id.linearLayout_tabCreate_random);
            linearLayoutList = (LinearLayout) view.findViewById(R.id.linearLayout_tabCreate_list);

            imageButtonEditWorld = (ImageButton) view.findViewById(R.id.imageButton_tabCreate_edit);

            editTextGameName = (EditText) view.findViewById(R.id.editText_tabCreate_gameName);
            String name = sharedPreferences.getString(PREF_GAME_NAME, "");
            if (!name.equals("")){
                editTextGameName.setText(name);
                editTextGameName.setFocusable(false);
            }
            editTextGameName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    editor.putString(PREF_GAME_NAME, String.valueOf(s));
                    editor.apply();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            progressBarWorldLoading = (ProgressBar) view.findViewById(R.id.progressBar_tabCreate_worldRefresh);
            progressBarWorldLoading.setVisibility(View.GONE);

            imageButtonRefresh = (ImageButton) view.findViewById(R.id.imageButton_tabCreate_refreshWorld);
            imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBarWorldLoading.setVisibility(View.VISIBLE);
                    new Thread(worldGenerator).start();
                }
            });


            imageButtonEditWorld.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(imageButtonEditWorld, "in dev", Snackbar.LENGTH_SHORT).show();
                }
            });

            toggleButtonRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        toggleButtonLibrary.setChecked(false);
                        showRandom();
                        editor.putBoolean(PREF_WORLD, false);
                        editor.apply();
                    } else if (!toggleButtonLibrary.isChecked()) toggleButtonRandom.setChecked(true);
                }
            });

            toggleButtonLibrary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        toggleButtonRandom.setChecked(false);
                        showLibrary();
                        editor.putBoolean(PREF_WORLD, true);
                        editor.apply();
                    } else if (!toggleButtonRandom.isChecked()) toggleButtonLibrary.setChecked(true);
                }
            });

            if (!sharedPreferences.getBoolean(PREF_WORLD, false)) {
                toggleButtonRandom.setChecked(true);
                new Thread(worldGenerator).start();
                progressBarWorldLoading.setVisibility(View.VISIBLE);
            } else {
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
                    editor.putInt(PREF_WALL_RATIO, progress);
                    editor.apply();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBarWallRatio.setProgress(sharedPreferences.getInt(PREF_WALL_RATIO, 50));

            textViewVoidRatio = (TextView) view.findViewById(R.id.textView_tabCreate_voidRatio);
            seekBarVoidRatio = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_voidRatio);
            seekBarVoidRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewVoidRatio.setText("" + progress);
                    editor.putInt(PREF_VOID_RATIO, progress);
                    editor.apply();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBarVoidRatio.setProgress(sharedPreferences.getInt(PREF_VOID_RATIO, 40));

            textViewPlayerMaximum = (TextView) view.findViewById(R.id.textView_tabCreate_playerMaximum);
            seekBarPlayerMaximum = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_playerMaximum);
            seekBarPlayerMaximum.setMax(7);
            seekBarPlayerMaximum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewPlayerMaximum.setText("" + (progress + 1));
                    editor.putInt(PREF_PLAYER_MAX, progress);
                    editor.apply();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBarPlayerMaximum.setProgress(sharedPreferences.getInt(PREF_PLAYER_MAX, 7));

            textViewSweetRegen = (TextView) view.findViewById(R.id.textView_tabCreate_sweetRegen);
            seekBarSweetRegen = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_sweetRegen);
            seekBarSweetRegen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewSweetRegen.setText("" + progress);
                    editor.putInt(PREF_SWEET_REGEN, progress);
                    editor.apply();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBarSweetRegen.setProgress(sharedPreferences.getInt(PREF_SWEET_REGEN, 15));

            textViewManaRegen = (TextView) view.findViewById(R.id.textView_tabCreate_manaRegen);
            seekBarManaRegen = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_manaRegen);
            seekBarManaRegen.setMax(200);
            seekBarManaRegen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewManaRegen.setText("" + progress);
                    editor.putInt(PREF_MANA_REGEN, progress);
                    editor.apply();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBarManaRegen.setProgress(sharedPreferences.getInt(PREF_SWEET_REGEN, 100));

            textViewSelectionTime = (TextView) view.findViewById(R.id.textView_tabCreate_selectionTime);
            seekBarSelectionTime = (SeekBar) view.findViewById(R.id.seekBar_tabCreate_selectionTime);
            seekBarSelectionTime.setMax(20);
            seekBarSelectionTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textViewSelectionTime.setText("" + progress);
                    editor.putInt(PREF_SELECTION_TIME, progress);
                    editor.apply();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBarSelectionTime.setProgress(sharedPreferences.getInt(PREF_SELECTION_TIME, 10));

            imageButtonFluffy = (ToggleImageButton) view.findViewById(R.id.imageButton_tabCreate_fluffy);
            imageButtonFluffy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageButtonFluffy.clicked();
                    editor.putBoolean(PREF_ALLOWED_FLUFFY, imageButtonFluffy.getChecked());
                    editor.apply();
                }
            });
            imageButtonFluffy.setChecked(sharedPreferences.getBoolean(PREF_ALLOWED_FLUFFY, false));

            imageButtonSlime = (ToggleImageButton) view.findViewById(R.id.imageButton_tabCreate_slime);
            imageButtonSlime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageButtonSlime.clicked();
                    editor.putBoolean(PREF_ALLOWED_SLIME, imageButtonSlime.getChecked());
                    editor.apply();
                }
            });
            imageButtonSlime.setChecked(sharedPreferences.getBoolean(PREF_ALLOWED_SLIME, false));

            imageButtonGhost = (ToggleImageButton) view.findViewById(R.id.imageButton_tabCreate_ghost);
            imageButtonGhost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageButtonGhost.clicked();
                    editor.putBoolean(PREF_ALLOWED_GHOST, imageButtonGhost.getChecked());
                    editor.apply();
                }
            });
            imageButtonGhost.setChecked(sharedPreferences.getBoolean(PREF_ALLOWED_GHOST, false));

            imageButtonNox = (ToggleImageButton) view.findViewById(R.id.imageButton_tabCreate_nox);
            imageButtonNox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageButtonNox.clicked();
                    editor.putBoolean(PREF_ALLOWED_NOX, imageButtonNox.getChecked());
                    editor.apply();
                }
            });
            imageButtonNox.setChecked(sharedPreferences.getBoolean(PREF_ALLOWED_NOX, false));

            switchUniqueCharacters = (Switch) view.findViewById(R.id.switch_tabSearch_uniqueCharacter);
            switchUniqueCharacters.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean(PREF_UNIQUE_CHAR, isChecked);
                    editor.apply();
                }
            });
            switchUniqueCharacters.setChecked(sharedPreferences.getBoolean(PREF_UNIQUE_CHAR, false));

            return view;
        }

        private void showRandom() {
            linearLayoutRandom.setVisibility(View.VISIBLE);
            linearLayoutList.setVisibility(View.GONE);
            imageButtonRefresh.setVisibility(View.VISIBLE);
        }

        private void showLibrary() {
            linearLayoutRandom.setVisibility(View.GONE);
            linearLayoutList.setVisibility(View.VISIBLE);
            imageButtonRefresh.setVisibility(View.GONE);
        }

        public GameInformation getGameInformation() {
            return new GameInformation(AppBluetoothManager.getLocalAddress(), editTextGameName.getText().toString(), world, switchUniqueCharacters.isChecked(), imageButtonFluffy.getChecked(), imageButtonSlime.getChecked(), imageButtonGhost.getChecked(), imageButtonNox.getChecked(), seekBarPlayerMaximum.getProgress() + 1, seekBarSweetRegen.getProgress(), seekBarManaRegen.getProgress(), seekBarSelectionTime.getProgress());
        }
    }

    public static class SearchFragment extends Fragment {

        //todo:remove connectionListener after joining the game
        private ListView gameList;
        private GameInformationAdapter adapter;
        private TextView textViewInfo;
        private ImageButton imageButtonSearch;
        private SwipeRefreshLayout swipeRefreshLayout;
        private AppBluetoothManager.BluetoothActionListener btListener = new AppBluetoothManager.BluetoothActionListener() {

            @Override
            public void onStart() {
                refresh();
            }

            @Override
            public void onStop() {
                showStartSearch();
            }

            @Override
            public void onGameSearchStarted() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onGameSearchFinished() {
                if (!swipeRefreshLayout.isRefreshing())return;
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
        };

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
            adapter = new GameInformationAdapter(getActivity());
            gameList.setAdapter(adapter);

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

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            AppBluetoothManager.addBluetoothListener(btListener);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            AppBluetoothManager.removeBluetoothListener(btListener);
        }

        private void refresh(){
            if (AppBluetoothManager.searchGames(getActivity(), adapter)) {
                adapter.notifyDataSetChanged();
                hideStartSearch();
            }else{
                showStartSearch();
            }
        }

        private void showStartSearch() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameList.setVisibility(View.VISIBLE);
                    imageButtonSearch.setVisibility(View.VISIBLE);
                    textViewInfo.setVisibility(View.VISIBLE);
                }
            });
        }

        private void hideStartSearch(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameList.setVisibility(View.VISIBLE);
                    imageButtonSearch.setVisibility(View.INVISIBLE);
                    textViewInfo.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public static class WorldsFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tab_worlds, container, false);
        }
    }

    public static GameInformation getGameInformation(){
        return gameInformation;
    }
}
