package mobile.data.usage.spyspyyou.gametest.game;

import mobile.data.usage.spyspyyou.gametest.R;

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

    int[][] DRAWABLE_RESOURCES = {
            {R.drawable.void_tile},
            {R.drawable.floor_tile},
            {R.drawable.wall_tile},
            {R.drawable.spawn_tile, R.drawable.spawn_tile},
            {R.drawable.light_bulb_tile, R.drawable.light_bulb_tile}
    };
}
