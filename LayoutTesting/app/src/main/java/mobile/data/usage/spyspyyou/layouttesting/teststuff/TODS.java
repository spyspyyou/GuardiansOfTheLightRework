package mobile.data.usage.spyspyyou.layouttesting.teststuff;

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



    boolean stopBluetoothOnAppLeaving = false;
    boolean makeBluetoothLikeBeforeAppStart = true;
}
