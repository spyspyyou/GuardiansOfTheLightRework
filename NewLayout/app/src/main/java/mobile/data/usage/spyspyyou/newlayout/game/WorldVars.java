package mobile.data.usage.spyspyyou.newlayout.game;

import mobile.data.usage.spyspyyou.newlayout.R;

public interface WorldVars {

    byte
            VOID = 0,
            FLOOR = 1,
            WALL = 2,
            SPAWN_BLUE = 3,
            SPAWN_GREEN = 4,
            LIGHT_BULB_STAND_BLUE = 5,
            LIGHT_BULB_STAND_GREEN = 6;

    int[] DRAWABLE_RESOURCES = {
            R.drawable.void_tile,
            R.drawable.floor_tile,
            R.drawable.wall_tile,
            R.drawable.spawn_tile,
            R.drawable.spawn_tile,
            R.drawable.light_bulb_tile,
            R.drawable.light_bulb_tile
    };
}
