package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.BluetoothDeviceNameHandling;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;

public class EditTextTextWatcher implements TextWatcher {

    public static final int
            USERNAME = 0,
            GAME_NAME = 1,
            CHAT_TEXT = 2;

    private final EditText EDIT_TEXT;
    private final int CONTENT;
    private final int MAX_LENGTH;

    public EditTextTextWatcher(EditText editText, int content){
        EDIT_TEXT = editText;
        CONTENT = content;
        switch (content){
            case CHAT_TEXT:
                MAX_LENGTH = BluetoothDeviceNameHandling.MAX_TEXT_LENGTH;
                break;
            case USERNAME:
            case GAME_NAME:
            default:
                MAX_LENGTH = BluetoothDeviceNameHandling.MAX_NAME_LENGTH;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = s.toString();
        switch (CONTENT) {
            case USERNAME:
                DataCenter.setUserName(EDIT_TEXT);
                break;
            case GAME_NAME:
                DataCenter.setGameName(EDIT_TEXT);
                break;
            case CHAT_TEXT:
                break;
            default:
                Log.i("ETTWatcher", "invalid content");
        }
        if (text.length() > MAX_LENGTH){
            EDIT_TEXT.setError("Too long.");
            return;
        }
        for (char c: BluetoothDeviceNameHandling.FORBIDDEN_CHARS){
            if (text.indexOf(c)!=-1){
                EDIT_TEXT.setError("Don't use '_' '-' '|' ',' '.' or enter");
                return;
            }
        }
        EDIT_TEXT.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
