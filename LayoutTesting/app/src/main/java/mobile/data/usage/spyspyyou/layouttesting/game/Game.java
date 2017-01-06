package mobile.data.usage.spyspyyou.layouttesting.game;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Entity;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Gum;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.LightBulb;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Player;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Slime;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.SlimeTrail;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.Sweet;
import mobile.data.usage.spyspyyou.layouttesting.game.entities.User;
import mobile.data.usage.spyspyyou.layouttesting.game.events.GameEvent;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.layouttesting.utils.BitmapManager;
import mobile.data.usage.spyspyyou.layouttesting.utils.Vector2D;

import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.game.Tick.ID_GHOST;

public class Game {

    private static Queue<GameEvent> eventQueue = new LinkedBlockingQueue<>();
    private Player[] players = new Player[0];
    private Sweet[] sweets = new Sweet[0];
    private ArrayList<SlimeTrail> slimes = new ArrayList<>();
    private ArrayList<Gum> gums = new ArrayList<>();
    private LightBulb[] lightBulbs = new LightBulb[0];
    private  User user;
    protected GameThread gameThread;
    private SurfaceViewGame gameSurface;
    private static SurfaceCoordinateCalculator screenCalculator;
    private GameMap gameMap;
    protected static boolean host;

    public Game(Resources resources, View rootView, Bitmap pixelMap, byte controlType, String[] players){
        host = false;
        GameEvent.setReceptors(players);
        new LoadingThread(resources, rootView, pixelMap, controlType).start();
    }

    public void addEvent(GameEvent gameEvent){
        eventQueue.offer(gameEvent);
    }

    protected void update() {
        while (!eventQueue.isEmpty()) {
            eventQueue.poll().apply(this);
        }

        for (Entity entity : players) {
            entity.update(this);
        }

        int size = slimes.size();
        for (int i = 0; i < size;) {
            slimes.get(i).update(this);
            if (size == slimes.size())++i;
            else size = slimes.size();
        }

        for (Entity gum:gums)gum.update(this);

        for (Sweet sweet:sweets)sweet.update(this);

        for (LightBulb lightBulb:lightBulbs){
            lightBulb.update(this);
        }
    }

    private void render(){
        //draw to the main surface
        Canvas c = gameSurface.getCanvas();
        if (c != null) {
            synchronized (gameSurface.getHolder()) {
                //todo:remove after debugging the game
                c.drawColor(Color.MAGENTA);

                gameMap.render(c, user.getPosition());

                for (Sweet sweet:sweets){
                    sweet.render(c);
                }

                for (Gum gum:gums)gum.render(c);

                for (SlimeTrail slime:slimes){
                    slime.render(c);
                }

                for (LightBulb lightBulb:lightBulbs){
                    lightBulb.render(c);
                }

                for (Entity entity : players) {
                    entity.render(c);
                }
            }
        }
        gameSurface.render(c);
    }

    public static void updateScreenPosition(Vector2D mapPosition, Vector2D vectorToWriteTo){
        screenCalculator.updateScreenPosition(mapPosition, vectorToWriteTo);
    }

    public static Vector2D getUserPosition(){
        return screenCalculator.getUserPosition();
    }

    public void activateSkill(){
        user.activateSkill(this);
    }

    public void shootParticle(){
        user.shootGum();
    }

    public void addSlime(Vector2D vector2D, int birthTick){
        slimes.add(new SlimeTrail(vector2D, birthTick));
    }

    public void removeSlime(SlimeTrail slimeTrail){
        slimes.remove(slimeTrail);
    }

    public void addGum(Vector2D position, Vector2D velocity){
        gums.add(new Gum(position, velocity));
    }

    public static boolean isHost(){
        return host;
    }

    public void setSynchronizedTick(int tick){
        gameThread.setSynchronizedTick(tick);
    }

    public int getSynchronizedTick(){
        return gameThread.getSynchronizedTick();
    }

    public void stopGame(){
        if (gameThread != null)gameThread.quitGame();
    }

    private class LoadingThread extends Thread {

        private final byte CONTROL_TYPE;
        private final Resources RESOURCES;
        private final View ROOT_VIEW;
        private final Bitmap PIXEL_MAP;


        private LoadingThread (Resources resources, View rootView, Bitmap pixelMap, byte controlType){
            RESOURCES = resources;
            ROOT_VIEW = rootView;
            PIXEL_MAP = pixelMap;
            CONTROL_TYPE = controlType;
        }

        @Override
        public void run() {
            BitmapManager.loadBitmaps(RESOURCES);

            gameSurface = (SurfaceViewGame) ROOT_VIEW.findViewById(R.id.surfaceView_game);
            while(!gameSurface.isCreated()) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            gameThread = new GameThread();
            gameMap = new GameMap(PIXEL_MAP, gameSurface.getTileSide());

            int tileSide = gameSurface.getTileSide();
            Vector2D userPosition = new Vector2D(1, 1);

            // todo: just for testing
            //-------------------------------------------------------------------------
            user = new Slime(userPosition, tileSide, gameSurface);
            players = new Player[3];
            players[0] = user;
            players[1] = new Player(new Vector2D(20, 15), ID_FLUFFY);
            players[2] = new Player(new Vector2D(18, 27), ID_GHOST);

            sweets = new Sweet[3];
            sweets[0] = new Sweet(new Vector2D(4.5, 4.5));
            sweets[1] = new Sweet(new Vector2D(20, 19));
            sweets[2] = new Sweet(new Vector2D(27, 20));

            lightBulbs = new LightBulb[1];
            lightBulbs[0] = new LightBulb(new Vector2D(1.5, 1.5));
            //-------------------------------------------------------------------------

            screenCalculator = new SurfaceCoordinateCalculator(userPosition, gameSurface);

            gameSurface.setup(CONTROL_TYPE, PIXEL_MAP, players);
            gameThread.start();
        }
    }

    protected class GameThread extends Thread implements Tick {

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
                }else{
                    Log.d("GameThread", "lag");
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

        private void quitGame(){running = false;}

        public int getSynchronizedTick() {
            return synchronizedTick;
        }

        public void setSynchronizedTick(int synchronizedTick) {
            this.synchronizedTick = synchronizedTick;
        }
    }

}
