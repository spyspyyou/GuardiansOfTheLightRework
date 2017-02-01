package mobile.data.usage.spyspyyou.gametest.game;


import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.entities.Entity;
import mobile.data.usage.spyspyyou.gametest.game.entities.Gum;
import mobile.data.usage.spyspyyou.gametest.game.entities.LightBulb;
import mobile.data.usage.spyspyyou.gametest.game.entities.Player;
import mobile.data.usage.spyspyyou.gametest.game.entities.Slime;
import mobile.data.usage.spyspyyou.gametest.game.entities.SlimeTrail;
import mobile.data.usage.spyspyyou.gametest.game.entities.Sweet;
import mobile.data.usage.spyspyyou.gametest.game.entities.User;
import mobile.data.usage.spyspyyou.gametest.game.events.GameEvent;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.GameData;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.gametest.game.Tick.ID_GHOST;
import static mobile.data.usage.spyspyyou.gametest.teststuff.VARS.PREF_LAYOUT;


public class Game {

    private static Queue<GameEvent> eventQueue = new LinkedBlockingQueue<>();

    //todo:better entity encapsulation
    private Player[] players = new Player[0];
    private User user;
    private LightBulb
            lightBulbBlue,
            lightBulbGreen;

    private SparseArray<Sweet> sweets = new SparseArray<>();
    private SparseArray<SlimeTrail> slimes = new SparseArray<>();
    private SparseArray<Gum> gums = new SparseArray<>();

    protected GameThread gameThread;
    private SurfaceViewGame gameSurface;
    private GameWorld gameWorld;

    public Game(GameData gameData){
        new LoadingThread(gameData);
    }

    protected void update() {
        while (!eventQueue.isEmpty()) {
            eventQueue.poll().apply(this);
        }

        user.update(this);

        Iterator<Entity> iterator;

        for (Entity gum:gums.)gum.update(this);

        for (Sweet sweet:sweets)sweet.update(this);

        lightBulbBlue.update(this);
        lightBulbGreen.update(this);
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

    public static void addEvent(GameEvent gameEvent){
        eventQueue.offer(gameEvent);
    }

    public  Vector2D getUserPosition(){
        return user.getPosition();
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

    public void setSynchronizedTick(int tick){
        gameThread.setSynchronizedTick(tick);
    }

    public int getSynchronizedTick(){
        return gameThread.getSynchronizedTick();
    }

    public void stopGame(){
        if (gameThread != null)gameThread.quitGame();
    }

    public GameWorld getWorld(){
        return gameWorld;
    }

    private class LoadingThread extends Thread {

        private final GameData GAME_DATA;


        private LoadingThread (GameData gameData, View rootView){
            GAME_DATA = gameData;
            gameSurface = (SurfaceViewGame) rootView.findViewById(R.id.surfaceView_game);
            start();
        }

        @Override
        public void run() {
            while(!gameSurface.isCreated()) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            gameThread = new GameThread();
            gameWorld = new GameWorld(GAME_DATA.WORLD);

            int tileSide = SurfaceViewGame.getTileSide();
            Vector2D userPosition = new Vector2D(1, 1);

            user = new Slime(userPosition, tileSide, gameSurface);
            players = new Player[3];
            players[0] = user;
            players[1] = new Player(new Vector2D(20, 15), ID_FLUFFY);
            players[2] = new Player(new Vector2D(18, 27), ID_GHOST);

            lightBulbBlue = new LightBulb(new Vector2D(10, 10));
            lightBulbGreen = new LightBulb(new Vector2D(1.5, 1.5));

            gameSurface.setup(PREF_LAYOUT, GAME_DATA.WORLD, players);
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
