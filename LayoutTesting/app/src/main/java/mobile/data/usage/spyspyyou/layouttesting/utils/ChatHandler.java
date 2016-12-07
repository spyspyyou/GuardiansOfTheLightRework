package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.global.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyClientActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.LobbyHostActivity;
import mobile.data.usage.spyspyyou.layouttesting.ui.ui_events.ChatEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.FocusManagedEditText;

public class ChatHandler {

    private final Activity ACTIVITY;
    private final LinearLayout LINEAR_LAYOUT;
    private final ScrollView SCROLL_VIEW;
    private String lastMessageSender = "";

    public ChatHandler(final Activity activity, DrawerLayout drawerChat) {
        ACTIVITY = activity;
        SCROLL_VIEW = (ScrollView) drawerChat.findViewById(R.id.scrollView_chat);
        LINEAR_LAYOUT = (LinearLayout) drawerChat.findViewById(R.id.linearLayout_chat);
        final FocusManagedEditText editText = (FocusManagedEditText) drawerChat.findViewById(R.id.editText_chat_message);
        final Button buttonSend = (Button) drawerChat.findViewById(R.id.button_chat_send);

        editText.addTextChangedListener(new EditTextTextWatcher(editText, EditTextTextWatcher.CHAT_TEXT));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    buttonSend.callOnClick();
                }
                return false;
            }
        });
        drawerChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.releaseFocus();
                return false;
            }
        });

        if (activity instanceof LobbyHostActivity){
            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText.getText().length() <= 0) return;
                    String text = DataCenter.matchTextToStandards(editText.getText().toString());
                    new ChatEvent(((LobbyHostActivity) activity).getAddresses(), text).send();
                    addMessage(DataCenter.getUserName(), text, AppBluetoothManager.getAddress());
                    editText.setText("");
                }
            });
        }else if (activity instanceof LobbyClientActivity){
            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText.getText().length() <= 0) return;
                    new ChatEvent(new String[]{((LobbyClientActivity) activity).getHostAddress()}, DataCenter.matchTextToStandards(editText.getText().toString())).send();
                    editText.setText("");
                }
            });
        }
    }

    private View createMessageView(String message, boolean isMe){
        RelativeLayout textViewParent;
        if (isMe)textViewParent = (RelativeLayout) RelativeLayout.inflate(ACTIVITY, R.layout.textview_chat_message_right, null);
        else textViewParent = (RelativeLayout) RelativeLayout.inflate(ACTIVITY, R.layout.textview_chat_message_left, null);
        TextView messageView = (TextView) textViewParent.findViewById(R.id.textView_chat_message);
        messageView.setText(DataCenter.matchTextToStandards(message));
        return textViewParent;
    }

    private View createUsernameView(String username){
        LinearLayout textViewParent = (LinearLayout) LinearLayout.inflate(ACTIVITY, R.layout.textview_chat_username, null);
        TextView usernameView = (TextView) textViewParent.findViewById(R.id.textView_chat_username);
        usernameView.setText(DataCenter.matchTextToStandards(username));
        return textViewParent;
    }

    public void addMessage(final String senderName, final String message, final String senderAddress) {
        ACTIVITY.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isMe = senderAddress.equals(AppBluetoothManager.getAddress());
                if (!lastMessageSender.equals(senderAddress) && !isMe){
                    LINEAR_LAYOUT.addView(createUsernameView(senderName));
                }
                LINEAR_LAYOUT.addView(createMessageView(message, isMe));
                lastMessageSender = senderAddress;
                SCROLL_VIEW.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

}
