package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
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
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.BluetoothDeviceNameHandling;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.global.DataCenter;

public class DeviceAdapterGame extends BaseAdapter {

    private ArrayList<BluetoothDevice> data;

    private static LayoutInflater inflater = null;

    public DeviceAdapterGame(Activity activity, ArrayList<BluetoothDevice> data) {
        this.data = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    @Nullable
    public BluetoothDevice getItem(int position) {
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
        if(convertView == null) view = inflater.inflate(R.layout.list_item_game, null);

        TextView username = (TextView)view.findViewById(R.id.textView_listItemGame_playerName);
        TextView gameName = (TextView)view.findViewById(R.id.textView_listItemGame_gameName);
        ImageView profilePicture = (ImageView) view.findViewById(R.id.imageView_listItemGame_picture);

        gameName.setText(BluetoothDeviceNameHandling.getGameName(data.get(position)));
        username.setText(BluetoothDeviceNameHandling.getUsername(data.get(position)));

        int picId = BluetoothDeviceNameHandling.getPictureId(data.get(position));
        int picRecId = DataCenter.PROFILE_PICTURES[picId];
        Drawable drawable = App.getContext().getResources().getDrawable(picRecId);
        profilePicture.setImageDrawable(drawable);
        return view;
    }
}
