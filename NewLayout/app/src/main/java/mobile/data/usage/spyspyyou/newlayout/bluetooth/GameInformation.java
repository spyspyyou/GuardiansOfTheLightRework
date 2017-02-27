package mobile.data.usage.spyspyyou.newlayout.bluetooth;

import mobile.data.usage.spyspyyou.newlayout.game.World;

public class GameInformation {
    private final String HOST_ADDRESS;
    private final World WORLD;

    public GameInformation(String address, World world){
        HOST_ADDRESS = address;
        WORLD = world;
    }

    public String getHostAddress(){
        return HOST_ADDRESS;
    }
}
