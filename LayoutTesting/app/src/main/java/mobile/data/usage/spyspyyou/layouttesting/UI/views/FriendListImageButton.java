package mobile.data.usage.spyspyyou.layouttesting.UI.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import mobile.data.usage.spyspyyou.layouttesting.R;

public class FriendListImageButton extends ImageButton {

    public FriendListImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
        setImageResource(R.drawable.ic_group_black_48dp);
    }
}
