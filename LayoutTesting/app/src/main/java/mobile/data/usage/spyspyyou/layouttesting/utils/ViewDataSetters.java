package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.App;

public abstract class ViewDataSetters {

    public static void setGameInfo(final GameInformation gameInfo, final RelativeLayout relativeLayout) {
        try {
            final Activity activity = App.accessActiveActivity(null);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    TextView textViewGameName = (TextView) relativeLayout.findViewById(R.id.textView_gameInfo_gameName);
                    TextView textViewUsername = (TextView) relativeLayout.findViewById(R.id.textView_gameInfo_hostData);
                    TextView textViewWidth = (TextView) relativeLayout.findViewById(R.id.textView_gameInfo_widthData);
                    TextView textViewHeight = (TextView) relativeLayout.findViewById(R.id.textView_gameInfo_heightData);
                    ImageView imageViewCrossFluffy = (ImageView) relativeLayout.findViewById(R.id.imageButton_gameInfo_crossFluffy);
                    ImageView imageViewCrossSlime = (ImageView) relativeLayout.findViewById(R.id.imageButton_gameInfo_crossSlime);
                    ImageView imageViewCrossGhost = (ImageView) relativeLayout.findViewById(R.id.imageButton_gameInfo_crossGhost);
                    ImageView imageViewCrossNox = (ImageView) relativeLayout.findViewById(R.id.imageButton_gameInfo_crossNox);

                    textViewGameName.setText(gameInfo.GAME_NAME);
                    textViewUsername.setText(gameInfo.GAME_HOST);
                    textViewWidth.setText(String.valueOf(gameInfo.WIDTH));
                    textViewHeight.setText(String.valueOf(gameInfo.HEIGHT));

                    boolean[] b = gameInfo.PLAYER_TYPES;
                    if (b[0]) imageViewCrossFluffy.setVisibility(View.INVISIBLE);
                    else imageViewCrossFluffy.setVisibility(View.VISIBLE);
                    if (b[1]) imageViewCrossSlime.setVisibility(View.INVISIBLE);
                    else imageViewCrossSlime.setVisibility(View.VISIBLE);
                    if (b[2]) imageViewCrossGhost.setVisibility(View.INVISIBLE);
                    else imageViewCrossGhost.setVisibility(View.VISIBLE);
                    if (b[3]) imageViewCrossNox.setVisibility(View.INVISIBLE);
                    else imageViewCrossNox.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e){
            Log.e("ViewDataSetters", "failed to set game info");
            e.printStackTrace();
        }
    }
}
