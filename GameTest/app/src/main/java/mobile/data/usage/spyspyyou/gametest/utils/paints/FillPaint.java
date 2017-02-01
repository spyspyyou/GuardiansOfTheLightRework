package mobile.data.usage.spyspyyou.gametest.utils.paints;

import android.graphics.Paint;

public class FillPaint extends Paint {

    public FillPaint(int color){
        setColor(color);
        setAntiAlias(true);
        setStyle(Style.FILL);
    }
}
