package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.global.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyActivity;

public class PlayerAdapter extends BaseAdapter {

    private ArrayList<PlayerInformation> data;
    private LayoutInflater inflater = null;

    public PlayerAdapter(LobbyActivity activity, ArrayList<PlayerInformation> data) {
        this.data=data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    @Nullable
    public PlayerInformation getItem(int position) {
        if (position >= data.size())return null;
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) view = inflater.inflate(R.layout.list_item_player, null);

        TextView username = (TextView) view.findViewById(R.id.textView_listItemPlayer_playerName);
        ImageView profilePicture = (ImageView) view.findViewById(R.id.imageView_listItemPlayer_picture);

        username.setText(data.get(position).getPLAYER_NAME());

        int picId = data.get(position).getPICTURE_ID();
        int picRecId = DataCenter.PROFILE_PICTURES[picId];
        Drawable drawable = App.getContext().getResources().getDrawable(picRecId);
        profilePicture.setImageDrawable(drawable);
        return view;
    }
}
