package testing.gotl.spyspyyo.bluetoothtesting.global;

/**
 * Created by Sandro on 07/09/2016.
 */
public interface TODS {

    // startActivityForResult codes
    int REQUEST_START_DISCOVERABLE = 0;
    String APP_IDENTIFIER = "GuardiansOfTheLight";
    String playerName = "spieler 1";
    String gameName = "Spiel 1";


    boolean startBluetoothOnAppEntering = true;
    boolean stopBluetoothOnAppLeaving = true;
    boolean alertBluetoothTurnedOff = true;
    boolean hostingGame = true;
    boolean allowInsecureConnections = true;
}
