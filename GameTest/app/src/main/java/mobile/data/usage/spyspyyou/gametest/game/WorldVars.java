package mobile.data.usage.spyspyyou.gametest.game;

import android.graphics.Color;

public interface WorldVars {

    byte
            VOID = 0,
            FLOOR = 1,
            WALL = 2,
            SPAWN = 3,
            LIGHT_BULB_STAND = 4;

    byte
            BLUE = 0,
            GREEN = 1;

    int[] PIXEL_COLORS = {
            Color.BLACK,
            Color.WHITE,
            Color.RED,
            Color.GRAY,
            Color.YELLOW
    };
}
