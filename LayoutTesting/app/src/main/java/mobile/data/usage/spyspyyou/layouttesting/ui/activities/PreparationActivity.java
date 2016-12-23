package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.CharacterSelector;
import mobile.data.usage.spyspyyou.layouttesting.utils.CharacterPlayerAdapter;
import mobile.data.usage.spyspyyou.layouttesting.utils.CharacterPlayerInformation;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_GHOST;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_NOX;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_SLIME;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.PREPARATION_TIME;

public class PreparationActivity extends GotLActivity {

    private ListView listViewTeam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparation);
        TextView textViewTimeLeft = (TextView) findViewById(R.id.textView_preparation_timeLeft);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_preparation_timeLeft);
        listViewTeam = (ListView) findViewById(R.id.listView_preparation);
        new TimeLeftThread(textViewTimeLeft, progressBar, PREPARATION_TIME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCharacters(new byte[]{ID_FLUFFY, ID_SLIME, ID_GHOST, ID_NOX});
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }

    public void setCharacters(byte[]characters){
        CharacterSelector characterSelector = (CharacterSelector) findViewById(R.id.scrollView_preparation_characterTypes);
        characterSelector.onContentFinished(characters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preparation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_close:
                //
        }
        return super.onOptionsItemSelected(item);
    }

    private class TimeLeftThread extends Thread {

        private final int START_TIME;
        private int timeLeft = 0;
        private final TextView TEXT_VIEW;
        private final ProgressBar PROGRESS_BAR;
        // every n milliseconds it updates;

        private TimeLeftThread(TextView textViewTimeLeft, ProgressBar progressBar, int startTime){
            timeLeft = START_TIME = startTime;
            TEXT_VIEW = textViewTimeLeft;
            PROGRESS_BAR = progressBar;
            start();
        }

        @Override
        public void run() {
            super.run();
            while(timeLeft >= 0 && App.accessActiveActivity(null) instanceof PreparationActivity){
                updateUI();
                timeLeft--;
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            startActivity(new Intent(getBaseContext(), GameActivity.class));
            //todo:startGameLoadingActivity
        }

        private void updateUI(){
            final int time = timeLeft;
            if (time < 0) timeLeft = 0;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PROGRESS_BAR.setProgress((int) ((100f * (START_TIME - time)) / START_TIME));
                    int minutes = time / 60;
                    int seconds = time % 60;
                    String text = "";
                    if (minutes > 0)text += minutes + ":";
                    text += seconds;
                    TEXT_VIEW.setText(text);
                }
            });
        }

    }

    public void setTeamInformation(CharacterPlayerInformation[] information){
        listViewTeam.setAdapter(new CharacterPlayerAdapter(this, information));
    }
}
