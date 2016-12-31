package mobile.data.usage.spyspyyou.layouttesting.utils;

import android.graphics.Paint;

public class RingPaint extends Paint {

    public RingPaint(int ringWidth, int color){
        setFlags(ANTI_ALIAS_FLAG);
        setStyle(Style.STROKE);
        setStrokeWidth(ringWidth);
        setColor(color);
    }
}
