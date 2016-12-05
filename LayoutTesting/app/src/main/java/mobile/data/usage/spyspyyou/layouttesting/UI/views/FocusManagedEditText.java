package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import mobile.data.usage.spyspyyou.layouttesting.global.App;

public class FocusManagedEditText extends TextInputEditText {

    public FocusManagedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        return getText().toString();
    }
}
