package mobile.data.usage.spyspyyou.newlayout.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Objects;

import mobile.data.usage.spyspyyou.newlayout.R;

public class ChatAdapter extends BaseAdapter {

    private Context appContext;

    private ArrayList<Objects>messages = new ArrayList<>();

    public ChatAdapter(Context appContext){
        this.appContext = appContext;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) view = RelativeLayout.inflate(appContext, R.layout.list_game, parent);



        return view;
    }
}
