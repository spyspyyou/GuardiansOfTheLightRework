package mobile.data.usage.spyspyyou.newlayout.ui.views;

import android.content.Context;
import android.util.AttributeSet;

public class ToggleImageButton extends android.support.v7.widget.AppCompatImageButton {

    private boolean checked = false;

    public ToggleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setImageAlpha(0x0);
    }

    public void clicked(){
        checked = !checked;
        if (checked) setImageAlpha(0xff);
        else setImageAlpha(0x0);
    }

    public void setChecked(boolean checked){
        this.checked = !checked;
        clicked();
    }

    public boolean getChecked(){
        return checked;
    }

}
