package mobile.data.usage.spyspyyou.gametest.game;

import mobile.data.usage.spyspyyou.gametest.R;

public interface Tick {
    int TICK = 40;
    long TIME_PER_TICK = 1000000000 / TICK;

    int
            COLOR_VALUE_BAR_BACKGROUND = 0x50000000,
            COLOR_VALUE_USER = 0xff0d6ffc,
            COLOR_VALUE_ALLY = 0xff24de15,
            COLOR_VALUE_ENEMY = 0xfff62b2b;

    int
            MAX_TILES_IN_WIDTH = 10,
            MAX_TILES_IN_HEIGHT = 5,
            HALF_TILES_IN_WIDTH = MAX_TILES_IN_WIDTH / 2 + ((MAX_TILES_IN_WIDTH % 2 != 0)?1:0),
            HALF_TILES_IN_HEIGHT = MAX_TILES_IN_HEIGHT / 2 + ((MAX_TILES_IN_HEIGHT % 2 != 0)?1:0);

    byte
            ID_FLUFFY = 0,
            ID_SLIME = 1,
            ID_GHOST = 2,
            ID_NOX = 3;

    int
            ICON_FLUFFY = R.drawable.fluffy,
            ICON_SLIME = R.drawable.slime,
            ICON_GHOST = R.drawable.ghost,
            ICON_NOX = "NOX".getBytes().length;

    int
            NAME_FLUFFY = R.string.fluffy,
            NAME_SLIME = R.string.slime,
            NAME_GHOST = R.string.ghost,
            NAME_NOX = R.string.nox;

    int
            DESCRIPTION_FLUFFY = R.string.fluffy_description,
            DESCRIPTION_SLIME = R.string.slime_description,
            DESCRIPTION_GHOST = R.string.ghost_description,
            DESCRIPTION_NOX = R.string.nox_description;
}
