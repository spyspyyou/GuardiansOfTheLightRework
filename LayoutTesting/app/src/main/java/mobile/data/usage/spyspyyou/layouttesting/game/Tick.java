package mobile.data.usage.spyspyyou.layouttesting.game;

import mobile.data.usage.spyspyyou.layouttesting.R;

public interface Tick {
    int TICK = 20;
    int TIME_PER_TICK = 1000 / TICK;

    byte
            ID_FLUFFY = 0,
            ID_SLIME = 1,
            ID_GHOST = 2,
            ID_NOX = 3;

    int
            ICON_FLUFFY = R.drawable.ic_account_box_black_24dp,
            ICON_SLIME = R.drawable.ic_cancel_black_48dp,
            ICON_GHOST = R.drawable.ic_fingerprint_black_36dp,
            ICON_NOX = R.drawable.ic_group_black_48dp;

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
