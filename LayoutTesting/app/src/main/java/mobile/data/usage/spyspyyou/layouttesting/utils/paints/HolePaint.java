package mobile.data.usage.spyspyyou.layouttesting.utils.paints;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class HolePaint extends Paint {
    public HolePaint(){
        setAlpha(0);
        setAntiAlias(true);
        setColor(Color.TRANSPARENT);
        setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
}
