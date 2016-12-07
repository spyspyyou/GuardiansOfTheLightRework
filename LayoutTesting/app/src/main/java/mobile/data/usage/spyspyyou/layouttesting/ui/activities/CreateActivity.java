package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.FocusManagedEditText;
import mobile.data.usage.spyspyyou.layouttesting.utils.EditTextTextWatcher;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.MAX_MAP_LENGTH;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.MIN_MAP_LENGTH;

public class CreateActivity extends GotLActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create);
        Button buttonCreate = (Button) findViewById(R.id.button_create);
        SeekBar seekBarWidth = (SeekBar) findViewById(R.id.seekBar_create_width);
        SeekBar seekBarHeight = (SeekBar) findViewById(R.id.seekBar_create_height);
        ImageButton fluffy = (ImageButton) findViewById(R.id.imageButton_create_fluffy);
        ImageButton slime = (ImageButton) findViewById(R.id.imageButton_create_slime);
        ImageButton ghost = (ImageButton) findViewById(R.id.imageButton_create_ghost);
        ImageButton nox = (ImageButton) findViewById(R.id.imageButton_create_nox);
        FocusManagedEditText editText = (FocusManagedEditText) findViewById(R.id.editText_create_gameName);
        final TextView widthData = (TextView) findViewById(R.id.textView_create_widthData);
        final TextView heightData = (TextView) findViewById(R.id.textView_create_heightData);
        ImageView map = (ImageView) findViewById(R.id.imageView_create_map);
        final ImageView fluffyCross = (ImageView) findViewById(R.id.imageView_create_crossFluffy);
        final ImageView slimeCross = (ImageView) findViewById(R.id.imageView_create_crossSlime);
        final ImageView ghostCross = (ImageView) findViewById(R.id.imageView_create_crossGhost);
        final ImageView noxCross = (ImageView) findViewById(R.id.imageView_create_crossNox);
        final CheckBox publicly = (CheckBox) findViewById(R.id.checkBox_create_public);
        final CheckBox friendly = (CheckBox) findViewById(R.id.checkBox_create_friends);
        final CheckBox privately = (CheckBox) findViewById(R.id.checkBox_create_private);

        widthData.setText(""+DataCenter.getMapWidth());
        seekBarWidth.setProgress(DataCenter.getMapWidth());
        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                byte width = (byte) (MIN_MAP_LENGTH+(MAX_MAP_LENGTH-MIN_MAP_LENGTH)/100f*progress);
                widthData.setText("" + width);
                DataCenter.setMapWidth(width);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        heightData.setText(""+DataCenter.getMapHeight());
        seekBarHeight.setProgress(DataCenter.getMapHeight());
        seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                byte height = (byte) (MIN_MAP_LENGTH+(MAX_MAP_LENGTH-MIN_MAP_LENGTH)/100f*progress);
                heightData.setText("" + height);
                DataCenter.setMapHeight(height);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        fluffyCross.setVisibility((DataCenter.getFluffyAllowed())?View.INVISIBLE:View.VISIBLE);
        slimeCross.setVisibility((DataCenter.getSlimeAllowed())?View.INVISIBLE:View.VISIBLE);
        ghostCross.setVisibility((DataCenter.getGhostAllowed())?View.INVISIBLE:View.VISIBLE);
        noxCross.setVisibility((DataCenter.getNoxAllowed())?View.INVISIBLE:View.VISIBLE);
        switch (DataCenter.getJoinRights()) {
            case DataCenter.PUBLIC:
                publicly.setChecked(true);
                break;
            case DataCenter.FRIENDLY:
                friendly.setChecked(true);
                break;
            case DataCenter.PRIVATE:
                privately.setChecked(true);
                break;
        }

        fluffy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fluffyCross.getVisibility() == View.VISIBLE){
                    fluffyCross.setVisibility(View.INVISIBLE);
                    DataCenter.setFluffyAllowed(true);
                }else{
                    fluffyCross.setVisibility(View.VISIBLE);
                    DataCenter.setFluffyAllowed(false);
                }
            }
        });

        slime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slimeCross.getVisibility() == View.VISIBLE){
                    slimeCross.setVisibility(View.INVISIBLE);
                    DataCenter.setSlimeAllowed(true);
                }else{
                    slimeCross.setVisibility(View.VISIBLE);
                    DataCenter.setSlimeAllowed(false);
                }
            }
        });

        ghost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ghostCross.getVisibility() == View.VISIBLE){
                    ghostCross.setVisibility(View.INVISIBLE);
                    DataCenter.setGhostAllowed(true);
                }else{
                    ghostCross.setVisibility(View.VISIBLE);
                    DataCenter.setGhostAllowed(false);
                }
            }
        });

        nox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noxCross.getVisibility() == View.VISIBLE){
                    noxCross.setVisibility(View.INVISIBLE);
                    DataCenter.setNoxAllowed(true);
                }else{
                    noxCross.setVisibility(View.VISIBLE);
                    DataCenter.setNoxAllowed(false);
                }
            }
        });

        publicly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    friendly.setChecked(false);
                    privately.setChecked(false);
                    DataCenter.setJoinRights(DataCenter.PRIVATE);
                }
            }
        });

        friendly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publicly.setChecked(false);
                    privately.setChecked(false);
                    DataCenter.setJoinRights(DataCenter.FRIENDLY);
                }
            }
        });

        privately.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publicly.setChecked(false);
                    friendly.setChecked(false);
                    DataCenter.setJoinRights(DataCenter.PRIVATE);
                }
            }
        });

        editText.setText(DataCenter.getGameName());
        editText.addTextChangedListener(new EditTextTextWatcher(editText, EditTextTextWatcher.GAME_NAME));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) Log.i("LTest", "Action Bar is null");
        else actionBar.setDisplayHomeAsUpEnabled(true);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), LobbyHostActivity.class));
            }
        });

    }


    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }
}
