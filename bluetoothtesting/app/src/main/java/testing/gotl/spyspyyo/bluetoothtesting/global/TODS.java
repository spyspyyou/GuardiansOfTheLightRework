package testing.gotl.spyspyyo.bluetoothtesting.global;

public interface TODS {

    // startActivityForResult codes
    int REQUEST_START_DISCOVERABLE = 0;
    String APP_IDENTIFIER = "GuardiansOfTheLight";
    String playerName = "spieler 1";
    String gameName = "Spiel 1";
    char EVENT_STRING_FINAL_CHAR = '|';


    boolean startBluetoothOnAppEntering = true;
    boolean stopBluetoothOnAppLeaving = true;
    boolean alertBluetoothTurnedOff = true;
    boolean hostingGame = true;
    boolean allowInsecureConnections = true;
}
