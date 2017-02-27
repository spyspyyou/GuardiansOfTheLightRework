package mobile.data.usage.spyspyyou.newlayout.bluetooth;

public class GameInformation {
    private final String HOST_ADDRESS;

    public GameInformation(String address){
        HOST_ADDRESS = address;
    }

    public String getHostAddress(){
        return HOST_ADDRESS;
    }
}
