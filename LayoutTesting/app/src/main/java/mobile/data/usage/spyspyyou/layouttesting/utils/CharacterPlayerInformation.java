package mobile.data.usage.spyspyyou.layouttesting.utils;

public class CharacterPlayerInformation extends PlayerInformation {

    private byte character;

    public CharacterPlayerInformation(String name, int picId, byte character){
        super(name, picId);
    }

    public byte getCharacter(){
        return character;
    }
}
