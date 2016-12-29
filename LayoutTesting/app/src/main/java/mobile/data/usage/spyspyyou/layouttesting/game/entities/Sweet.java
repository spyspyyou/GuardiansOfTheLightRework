package mobile.data.usage.spyspyyou.layouttesting.game.entities;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class Sweet extends Entity {

    public Sweet(Vector2D entityPosition, int size) {
        super(entityPosition, size, size, R.drawable.sweet);
    }

    @Override
    public void update() {}
}
