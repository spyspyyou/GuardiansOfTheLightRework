package mobile.data.usage.spyspyyou.newlayout.game;

import mobile.data.usage.spyspyyou.newlayout.R;

public interface GameVars {

    int MAX_TEAM_SIZE = 4;

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
            DESCRIPTION_FLUFFY = R.string.description_fluffy,
            DESCRIPTION_SLIME = R.string.description_slime,
            DESCRIPTION_GHOST = R.string.description_ghost,
            DESCRIPTION_NOX = R.string.description_nox;

}
