package mobile.data.usage.spyspyyou.layouttesting.ui.views;

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
        int height = getMeasuredHeight();
        if (getId() == R.id.imageButton_main_friend_list) {
            setMeasuredDimension(width, width);
            setImageResource(R.drawable.ic_group_black_48dp);
        }else if (getId() == R.id.imageButton_main_connections){
            Log.i("LTest", "width: " + width);
            setMeasuredDimension(width, width);
            setImageResource(R.drawable.ic_bluetooth_searching_black_48dp);
        }else Log.w("SquareImageButton", "invalid resource id, image was set");
    }
}
