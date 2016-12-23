package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.BitmapManager;

public class Sweet extends Entity {

    public Sweet(int entityX, int entityY) {
        super(entityX, entityY, true, BitmapManager.getBitmap(R.drawable.sweet));
    }

    @Override
    public void update() {}
}
