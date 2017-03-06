package mobile.data.usage.spyspyyou.gametest.game.entities;

import android.graphics.Canvas;
import android.graphics.Paint;

import mobile.data.usage.spyspyyou.gametest.game.Game;
import mobile.data.usage.spyspyyou.gametest.game.Tick;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_GHOST;

public class Ghost extends User {

    private static final float
            MANA_LOSS_PER_TICK = MAX_MANA / (7f  * Tick.TICK),
            MANA_GAIN_PER_TICK = MANA_LOSS_PER_TICK * 0.87f;
    private final Paint INVISIBLE_PAINT = new Paint();

    public Ghost(boolean teamBlue) {
        super(teamBlue, ID_GHOST, MAX_MANA /10);
        INVISIBLE_PAINT.setAlpha(0x55);
    }

    @Override
    public void update(Game game) {
        super.update(game);
        if (!visible){
            mana -= MANA_LOSS_PER_TICK;
            if (mana < 0){
                mana = 0;
                visible = true;
            }
        }
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        if (!visible)canvas.drawBitmap(bitmap, (float) (screenPosition.x - bitmap.getWidth() / 2.0), (float) (screenPosition.y - bitmap.getHeight() / 2.0), INVISIBLE_PAINT);
    }

    @Override
    protected void addMana() {
        if (visible)mana += MANA_GAIN_PER_TICK;
    }

    @Override
    public boolean activateSkill(Game game) {
        if (!visible){
            visible = true;
            return true;
        }

        if(super.activateSkill(game)) {
            visible = false;
            return true;
        }
        return false;
    }
}
