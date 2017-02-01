package mobile.data.usage.spyspyyou.gametest.game;

public class IdLinker implements Tick {

    public static int getBitmapId(int characterID){
        switch (characterID){
            case ID_FLUFFY:
                return ICON_FLUFFY;
            case ID_SLIME:
                return ICON_SLIME;
            case ID_GHOST:
                return ICON_GHOST;
            case ID_NOX:
                return ICON_NOX;
            default:
                //this will cause the default bitmap to appear
                return 0;
        }
    }
}
