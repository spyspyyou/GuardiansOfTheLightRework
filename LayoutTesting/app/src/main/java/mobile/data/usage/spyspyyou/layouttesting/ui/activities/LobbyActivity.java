package mobile.data.usage.spyspyyou.layouttesting.ui.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.FocusManagedEditText;
import mobile.data.usage.spyspyyou.layouttesting.utils.EditTextTextWatcher;
import mobile.data.usage.spyspyyou.layouttesting.utils.GameInformation;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerAdapter;
import mobile.data.usage.spyspyyou.layouttesting.utils.PlayerInformation;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.TEAM_BLUE;

public abstract class LobbyActivity extends GotLActivity {

    protected byte team;
    protected static GameInformation gameInformation;
    protected ArrayList<PlayerInformation> teamBlue = new ArrayList<>();
    protected ArrayList<PlayerInformation> teamGreen = new ArrayList<>();
    protected PlayerAdapter blueListAdapter, greenListAdapter;
    protected ListView blueTeamList, greenTeamList;
    protected DrawerLayout drawerLayout;
    private LinearLayout linearLayoutChat;
    protected RelativeLayout relativeLayoutGameInfo;
    protected Button buttonSend;
    protected FocusManagedEditText inputEditTextChat;
    private String lastMessageSender = "";
    private ScrollView scrollViewChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_lobby);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_lobby);
        relativeLayoutGameInfo = (RelativeLayout) findViewById(R.id.relativeLayout_gameInfo);
        linearLayoutChat = (LinearLayout) findViewById(R.id.linearLayout_chat);
        blueTeamList = (ListView) findViewById(R.id.listView_lobby_teamBlue);
        greenTeamList = (ListView) findViewById(R.id.listView_lobby_teamGreen);
        buttonSend = (Button) findViewById(R.id.button_chat_send);
        inputEditTextChat = (FocusManagedEditText) findViewById(R.id.editText_chat_message);
        inputEditTextChat.addTextChangedListener(new EditTextTextWatcher(inputEditTextChat, EditTextTextWatcher.CHAT_TEXT));
        scrollViewChat = (ScrollView) findViewById(R.id.scrollView_chat);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) Log.i("LTest", "Action Bar is null");

        if (team == TEAM_BLUE) teamBlue.add(new PlayerInformation());
        else teamGreen.add(new PlayerInformation());

        blueListAdapter = new PlayerAdapter(this, teamBlue);
        greenListAdapter = new PlayerAdapter(this, teamGreen);

        inputEditTextChat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    buttonSend.callOnClick();
                }
                return false;
            }
        });

        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                inputEditTextChat.releaseFocus();
                return false;
            }
        });

        blueTeamList.setAdapter(blueListAdapter);
        greenTeamList.setAdapter(greenListAdapter);
    }

    public void updateListViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                blueTeamList.setAdapter(blueListAdapter);
                greenTeamList.setAdapter(greenListAdapter);
                blueListAdapter.notifyDataSetChanged();
                greenListAdapter.notifyDataSetChanged();
            }
        });
    }

    public static void setGameInformation(GameInformation mGameInformation) {
        gameInformation = mGameInformation;
    }

    private View createMessageView(String message, boolean isMe){
        RelativeLayout textViewParent;
        if (isMe)textViewParent = (RelativeLayout) RelativeLayout.inflate(getBaseContext(), R.layout.textview_chat_message_right, null);
        else textViewParent = (RelativeLayout) RelativeLayout.inflate(getBaseContext(), R.layout.textview_chat_message_left, null);
        TextView messageView = (TextView) textViewParent.findViewById(R.id.textView_chat_message);
        messageView.setText(DataCenter.matchTextToStandards(message));
        return textViewParent;
    }

    private View createUsernameView(String username){
        LinearLayout textViewParent = (LinearLayout) LinearLayout.inflate(getBaseContext(), R.layout.textview_chat_username, null);
        TextView usernameView = (TextView) textViewParent.findViewById(R.id.textView_chat_username);
        usernameView.setText(DataCenter.matchTextToStandards(username));
        return textViewParent;
    }

    public void addMessage(final String senderName, final String message, final String senderAddress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isMe = senderAddress.equals(AppBluetoothManager.getAddress());
                if (!lastMessageSender.equals(senderAddress) && !isMe){
                    linearLayoutChat.addView(createUsernameView(senderName));
                }
                linearLayoutChat.addView(createMessageView(message, isMe));
                lastMessageSender = senderAddress;
                scrollViewChat.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
