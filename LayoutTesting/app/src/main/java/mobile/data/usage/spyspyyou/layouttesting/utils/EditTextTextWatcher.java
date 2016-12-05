package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.BluetoothDeviceNameHandling;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.FocusManagedEditText;

public class EditTextTextWatcher implements TextWatcher {

    public static final int
            USERNAME = 0,
            GAME_NAME = 1;

    private final FocusManagedEditText EDIT_TEXT;
    private final int CONTENT;

    public EditTextTextWatcher(FocusManagedEditText editText, int content){
        EDIT_TEXT = editText;
        CONTENT = content;
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
            default:
                Log.i("ETTWatcher", "invalid content");
        }
        if (text.length()> BluetoothDeviceNameHandling.MAX_NAME_LENGTH){
            EDIT_TEXT.setError("Too long.");
            return;
        }
        for (char c: BluetoothDeviceNameHandling.FORBIDDEN_CHARS){
            if (text.indexOf(c)!=-1){
                EDIT_TEXT.setError("Don't use '_' '-' '|' ',' '.'");
                return;
            }
        }
        EDIT_TEXT.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
