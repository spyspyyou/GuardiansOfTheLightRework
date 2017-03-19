package mobile.data.usage.spyspyyou.newlayout.ui.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import mobile.data.usage.spyspyyou.newlayout.R;

import static mobile.data.usage.spyspyyou.newlayout.teststuff.VARS.PROFILE_PICTURES;

public class ProfilePicAdapter extends BaseAdapter {

    private final LayoutInflater INFLATER;
    private final Resources RESOURCES;
    private final Handler HANDLER;
    private int selection = 0;

    public ProfilePicAdapter(Activity activity){
        INFLATER = activity.getLayoutInflater();
        RESOURCES = activity.getResources();
        HANDLER = new Handler();
    }

    @Override
    public int getCount() {
        return PROFILE_PICTURES.length;
    }

    @Override
    public Object getItem(int position) {
        return PROFILE_PICTURES[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)view = INFLATER.inflate(R.layout.list_profile_pic, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_listProfile);
        imageView.setImageResource(PROFILE_PICTURES[position]);
        if (selection == position) imageView.setBackgroundColor(RESOURCES.getColor(R.color.colorSelected));
        else imageView.setBackgroundColor(0x00000000);

        return view;
    }

    public void setSelection(int selection){
        Log.d("PPAdapter", "Selection set to: " + selection);
        this.selection = selection;
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
