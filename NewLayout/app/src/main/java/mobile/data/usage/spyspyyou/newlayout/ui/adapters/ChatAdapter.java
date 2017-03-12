package mobile.data.usage.spyspyyou.newlayout.ui.adapters;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.ChatMessage;

public class ChatAdapter extends BaseAdapter {

    private final ArrayList<ChatMessage> messages = new ArrayList<>();
    private final Handler HANDLER;
    private final LayoutInflater INFLATER;
    private final String LOCAL_ADDRESS;

    public ChatAdapter(Activity activity) {
        HANDLER = new Handler();
        INFLATER = activity.getLayoutInflater();
        LOCAL_ADDRESS = AppBluetoothManager.getLocalAddress();
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
        boolean isMe = messages.get(position).ADDRESS.equals(LOCAL_ADDRESS);
        if (view == null) {
            if (isMe) {
                view = INFLATER.inflate(R.layout.list_chat_me, parent, false);
            } else{
                view = INFLATER.inflate(R.layout.list_chat_other, parent, false);
            }
        }

        TextView textViewMessage;
        if (isMe){
            textViewMessage = (TextView) view.findViewById(R.id.textView_chatMe_message);

        }else{
            textViewMessage = (TextView) view.findViewById(R.id.textView_chatOther_message);
            TextView senderName = (TextView) view.findViewById(R.id.textView_chatOther_name);
            senderName.setText(messages.get(position).NAME);
        }
        textViewMessage.setText(messages.get(position).MESSAGE);

        return view;
    }

    public void addMessage(ChatMessage chatMessage) {
        messages.add(chatMessage);
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
