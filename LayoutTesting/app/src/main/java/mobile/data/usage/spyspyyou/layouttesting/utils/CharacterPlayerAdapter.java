package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.global.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.PreparationActivity;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ICON_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ICON_GHOST;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ICON_NOX;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ICON_SLIME;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_GHOST;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_NOX;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_SLIME;

public class CharacterPlayerAdapter extends BaseAdapter {

    private CharacterPlayerInformation[] data;
    private LayoutInflater inflater = null;

    public CharacterPlayerAdapter(PreparationActivity activity, CharacterPlayerInformation[] data) {
        this.data=data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    @Nullable
    public PlayerInformation getItem(int position) {
        if (position >= data.length)return null;
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) view = inflater.inflate(R.layout.list_item_character, null);

        TextView username = (TextView) view.findViewById(R.id.textView_listItemCharacter_playerName);
        ImageView profilePicture = (ImageView) view.findViewById(R.id.imageView_listItemCharacter_picture);
        ImageView characterIcon = (ImageView) view.findViewById(R.id.imageView_listItemCharacter_icon);

        username.setText(data[position].getPLAYER_NAME());

        int picId = data[position].getPICTURE_ID();
        int picRecId = DataCenter.PROFILE_PICTURES[picId];
        profilePicture.setImageDrawable(App.getContext().getResources().getDrawable(picRecId));

        switch (data[position].getCharacter()){
            case ID_FLUFFY:
                characterIcon.setImageDrawable(App.getContext().getResources().getDrawable(ICON_FLUFFY));
                break;
            case ID_SLIME:
                characterIcon.setImageDrawable(App.getContext().getResources().getDrawable(ICON_SLIME));
                break;
            case ID_GHOST:
                characterIcon.setImageDrawable(App.getContext().getResources().getDrawable(ICON_GHOST));
                break;
            case ID_NOX:
                characterIcon.setImageDrawable(App.getContext().getResources().getDrawable(ICON_NOX));
                break;
        }
        return view;
    }
}
