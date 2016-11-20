package mobile.data.usage.spyspyyou.layouttesting.UI.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;

import mobile.data.usage.spyspyyou.layouttesting.R;

public class SquareImageButton extends ImageButton {

    public SquareImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
        if (getId() == R.id.imageButton_main_friend_list) {
            setImageResource(R.drawable.ic_group_black_48dp);
        }else if (getId() == R.id.imageButton_main_connections){
            setImageResource(R.drawable.ic_bluetooth_searching_black_48dp);
        }else Log.w("SquareImageButton", "invalid resource id, image was set");
    }
}
