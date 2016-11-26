package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.DataCenter;
import mobile.data.usage.spyspyyou.layouttesting.utils.BluetoothDeviceNameHandling;

public class FocusManagedEditText extends TextInputEditText {

    public FocusManagedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        final FocusManagedEditText editTextThis = this;
        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    Log.i("LTest", "action done");
                    releaseFocus();
                }
                return false;
            }
        });
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                DataCenter.setUserName(editTextThis);
                if (text.length()>BluetoothDeviceNameHandling.MAX_NAME_LENGTH){
                    editTextThis.setError("Too long.");
                    return;
                }
                for (char c: BluetoothDeviceNameHandling.FORBIDDEN_CHARS){
                    if (text.indexOf(c)!=-1){
                        editTextThis.setError("Don't use '_' '-' '|' ',' '.'");
                        return;
                    }
                }
                editTextThis.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setFocusableInTouchMode(true);
        return super.onTouchEvent(event);
    }

    public void releaseFocus(){
        Activity a = App.accessActiveActivity(null);
        View view = a.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        setFocusable(false);
        clearFocus();
    }

    public String getStringText(){
        String text = getText().toString();
        int maxLength = BluetoothDeviceNameHandling.MAX_NAME_LENGTH;
        if (text.length()>maxLength) text = text.substring(0, maxLength);
        for(char c:BluetoothDeviceNameHandling.FORBIDDEN_CHARS){
            text = text.replace(c, '?');
        }
        return text;
    }
}
