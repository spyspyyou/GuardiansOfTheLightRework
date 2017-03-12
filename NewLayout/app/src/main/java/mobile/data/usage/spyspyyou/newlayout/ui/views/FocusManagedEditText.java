package mobile.data.usage.spyspyyou.newlayout.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;

public class FocusManagedEditText extends android.support.v7.widget.AppCompatEditText {

    public FocusManagedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        return super.onTouchEvent(event);
    }

    @Override
    public void onEditorAction(int actionCode) {
        super.onEditorAction(actionCode);
        if (actionCode == EditorInfo.IME_ACTION_DONE){
            setFocusable(false);
        }
    }
}
