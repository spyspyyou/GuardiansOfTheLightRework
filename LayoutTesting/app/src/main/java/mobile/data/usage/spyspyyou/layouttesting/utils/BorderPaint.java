package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.graphics.Paint;

public class BorderPaint extends Paint {

    public BorderPaint(int borderWidth, int color){
        setFlags(ANTI_ALIAS_FLAG);
        setStyle(Style.STROKE);
        setStrokeWidth(borderWidth);
        setColor(color);
    }
}
