package mobile.data.usage.spyspyyou.newlayout.ui.messages;

import java.io.Serializable;

public class PlayerInfo implements Serializable {

    public final String
            NAME,
            ADDRESS;
    public int PIC;

    public PlayerInfo (String name, String address, int pic){
        NAME = name;
        ADDRESS = address;
        PIC = pic;
    }

}
