package mobile.data.usage.spyspyyou.layouttesting.game;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import mobile.data.usage.spyspyyou.layouttesting.game.entities.Entity;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Fluffy;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Player;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Sweet;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.User;
import mobile.data.usage.spyspyyou.layouttesting.game.events.GameEvent;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_GHOST;

public class Game {

    private static Queue<GameEvent> eventQueue = new LinkedBlockingQueue<>();
    private Player[] players = new Player[0];
    private Sweet[] sweets = new Sweet[0];
    private User user;
    private GameThread gameThread;
    private GameUIManager gameUIManager;
    private static SurfaceCoordinateCalculator screenCalculator;
    private GameMap gameMap;

    public Game(Resources resources, View rootView, Bitmap pixelMap){
        new LoadingThread(resources, rootView, pixelMap).start();
    }

    public void addEvent(GameEvent gameEvent){
        eventQueue.offer(gameEvent);
    }

    private void update() {
        while (!eventQueue.isEmpty()) {
            eventQueue.poll().apply();
        }

        gameUIManager.update();

        for (Entity entity : players) {
            entity.update();
        }
    }

    private void render(){
        //draw to the main surface
        Canvas c = gameUIManager.getGameCanvas();
        if (c != null) {
            synchronized (gameUIManager.getGameSurfaceHolder()) {
                //todo:remove after debugging the game
                c.drawColor(Color.MAGENTA);

                gameMap.render(c, user.getPosition());

                for (Sweet sweet:sweets){
                    sweet.render(c);
                }

                for (Entity entity : players) {
                    entity.render(c);
                }
            }
        }
        gameUIManager.render(c);
    }

    public static void updateScreenPosition(Vector2D mapPosition, Vector2D vectorToWriteTo){
        screenCalculator.updateScreenPosition(mapPosition, vectorToWriteTo);
    }

    private class LoadingThread extends Thread {
        private final Resources RESOURCES;
        private final View ROOT_VIEW;
        private final Bitmap PIXEL_MAP;


        private LoadingThread (Resources resources, View rootView, Bitmap pixelMap){
            RESOURCES = resources;
            ROOT_VIEW = rootView;
            PIXEL_MAP = pixelMap;
        }

        @Override
        public void run() {
            BitmapManager.loadBitmaps(RESOURCES);

            gameUIManager = new GameUIManager(ROOT_VIEW);
            while(!gameUIManager.areSurfacesCreated()) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            gameThread = new GameThread();
            gameMap = new GameMap(gameUIManager, PIXEL_MAP, gameUIManager.getTileSide());

            players = new Player[3];
            Vector2D userPosition = new Vector2D(1, 1);
            user = new Fluffy(userPosition, gameUIManager.getTileSide(), gameUIManager);
            players[0] = user;
            players[1] = new Player(new Vector2D(20, 15), gameUIManager.getTileSide(), ID_FLUFFY);
            players[2] = new Player(new Vector2D(18, 27), gameUIManager.getTileSide(), ID_GHOST);

            sweets = new Sweet[3];
            sweets[0] = new Sweet(new Vector2D(40, 23), gameUIManager.getTileSide());
            sweets[1] = new Sweet(new Vector2D(20, 19), gameUIManager.getTileSide());
            sweets[2] = new Sweet(new Vector2D(27, 20), gameUIManager.getTileSide());

            screenCalculator = new SurfaceCoordinateCalculator(userPosition, gameUIManager);
            gameUIManager.setData(PIXEL_MAP, players);

            gameThread.start();
        }
    }

    private class GameThread extends Thread implements Tick {

        private static final byte PAUSE_CHECK_TIME = 100;

        private boolean running, paused;

        private long tickStartTime = 0;
        private int
                synchronizedTick = 0,
                sleepTime = 0;

        @Override
        public void run() {
            running = true;
            paused = false;

            while (running) {
                while(paused && running){
                    try {
                        sleep(PAUSE_CHECK_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                tickStartTime = System.currentTimeMillis();

                update();
                render();
                ++synchronizedTick;

                sleepTime = (int) (TIME_PER_TICK - (System.currentTimeMillis() - tickStartTime));
                if (sleepTime > 0) {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            //kill any static object references so garbage collection can clean up
            screenCalculator = null;
        }

        private void pauseGame(){
            paused = true;
        }

        private void resumeGame(){
            paused = false;
        }

        public int getSynchronizedTick() {
            return synchronizedTick;
        }

        public void setSynchronizedTick(int synchronizedTick) {
            this.synchronizedTick = synchronizedTick;
        }
    }

}
