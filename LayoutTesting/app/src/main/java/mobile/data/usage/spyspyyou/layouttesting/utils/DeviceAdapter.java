package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.R;

public class DeviceAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> data;
    private static LayoutInflater inflater = null;
    private Activity activity;

    public DeviceAdapter(Activity activity, ArrayList<BluetoothDevice> data) {
        this.data=data;
        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) view = inflater.inflate(R.layout.list_item_player, parent);

        TextView username = (TextView)view.findViewById(R.id.textView_listItemPlayer);
        ImageView profilePicture = (ImageView) view.findViewById(R.id.imageView_listItemPlayer_ProfilePicture);

        username.setText(BluetoothDeviceNameHandling.getUsername(data.get(position)));
        profilePicture.setImageDrawable(activity.getResources().getDrawable(BluetoothDeviceNameHandling.getPictureId(data.get(position))));
        return view;
    }
}
