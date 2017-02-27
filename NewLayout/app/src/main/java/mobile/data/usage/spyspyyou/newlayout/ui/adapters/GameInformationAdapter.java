package mobile.data.usage.spyspyyou.newlayout.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;

public class GameInformationAdapter extends BaseAdapter {

    private ArrayList<GameInformation> games;
    private Context appContext;

    public GameInformationAdapter(Activity activity){
        appContext = activity.getApplicationContext();
    }

    public void setData(ArrayList<GameInformation> mGames){
        games = mGames;
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) view = RelativeLayout.inflate(appContext, R.layout.game_information, parent);

        return view;
    }
}
