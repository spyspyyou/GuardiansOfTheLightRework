package mobile.data.usage.spyspyyou.layouttesting.utils.paints;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class SrcOutPaint extends Paint {

    public SrcOutPaint(int color){
        setFlags(ANTI_ALIAS_FLAG);
        setStyle(Style.FILL);
        setColor(color);
        setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        setAntiAlias(true);
    }

}
