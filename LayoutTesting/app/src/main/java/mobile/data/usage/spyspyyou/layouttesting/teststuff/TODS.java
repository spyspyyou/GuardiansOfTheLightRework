package mobile.data.usage.spyspyyou.layouttesting.teststuff;

import mobile.data.usage.spyspyyou.layouttesting.R;

public interface TODS {

    // startActivityForResult codes
    int REQUEST_COARSE_LOCATION_PERMISSION = 0;
    int REQUEST_START_DISCOVERABLE = 0;
    String APP_IDENTIFIER = "GuardiansOfTheLight";
    String ADDRESS_EXTRA = "address";
    String TEAM_EXTRA = "team";
    byte TEAM_BLUE = 0, TEAM_GREEN = 1, NO_TEAM = -1;

    int MIN_MAP_LENGTH = 20;
    int MAX_MAP_LENGTH = 100;

    String textEncoding = "UTF-8";

    /*private static final*/ byte
            ID_FLUFFY = 0,
            ID_SLIME = 1,
            ID_GHOST = 2,
            ID_NOX = 3;

    /*private static final*/ int
            ICON_FLUFFY = R.drawable.ic_account_box_black_24dp,
            ICON_SLIME = R.drawable.ic_cancel_black_48dp,
            ICON_GHOST = R.drawable.ic_fingerprint_black_36dp,
            ICON_NOX = R.drawable.ic_group_black_48dp;

    /*private static final*/ int
            NAME_FLUFFY = R.string.fluffy,
            NAME_SLIME = R.string.slime,
            NAME_GHOST = R.string.ghost,
            NAME_NOX = R.string.nox;

    /*private static final*/ int
            DESCRIPTION_FLUFFY = R.string.fluffy_description,
            DESCRIPTION_SLIME = R.string.slime_description,
            DESCRIPTION_GHOST = R.string.ghost_description,
            DESCRIPTION_NOX = R.string.nox_description;

    boolean stopBluetoothOnAppLeaving = false;
    boolean makeBluetoothLikeBeforeAppStart = true;
}
