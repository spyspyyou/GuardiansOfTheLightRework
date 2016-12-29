package mobile.data.usage.spyspyyou.layouttesting.game;

import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

public class SurfaceCoordinateCalculator {

    private final Vector2D
            USER_POSITION,
            SURFACE_CENTER;

    private final int TILE_SIDE;

    public SurfaceCoordinateCalculator(Vector2D userPosition, GameUIManager gameUIManager){
        USER_POSITION = userPosition;
        SURFACE_CENTER = gameUIManager.getGameSurfaceCenter();
        TILE_SIDE = gameUIManager.getTileSide();
    }

    public void updateScreenPosition(Vector2D mapPosition, Vector2D vectorToWriteTo){
        vectorToWriteTo.set(SURFACE_CENTER.x + (mapPosition.x - USER_POSITION.x) * TILE_SIDE, SURFACE_CENTER.y + (mapPosition.y - USER_POSITION.y) * TILE_SIDE);
    }
}