package mobile.data.usage.spyspyyou.gametest.game;


import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import mobile.data.usage.spyspyyou.gametest.R;
import mobile.data.usage.spyspyyou.gametest.game.entities.Gum;
import mobile.data.usage.spyspyyou.gametest.game.entities.LightBulb;
import mobile.data.usage.spyspyyou.gametest.game.entities.Player;
import mobile.data.usage.spyspyyou.gametest.game.entities.SlimeTrail;
import mobile.data.usage.spyspyyou.gametest.game.entities.Sweet;
import mobile.data.usage.spyspyyou.gametest.game.entities.User;
import mobile.data.usage.spyspyyou.gametest.game.events.GameEvent;
import mobile.data.usage.spyspyyou.gametest.ui.views.SurfaceViewGame;
import mobile.data.usage.spyspyyou.gametest.utils.Vector2D;

import static mobile.data.usage.spyspyyou.gametest.teststuff.VARS.PREF_LAYOUT;


public class Game {

    private static Queue<GameEvent> eventQueue = new LinkedBlockingQueue<>();
    public static boolean HOST = false;

    //todo:better entity encapsulation
    private Map<String, Player> players = new LinkedHashMap<>();
    private User user;
    private LightBulb
            lightBulbBlue,
            lightBulbGreen;

    private SparseArray<Sweet> sweets = new SparseArray<>();
    private SparseArray<SlimeTrail> slimes = new SparseArray<>();
    private SparseArray<Gum> gums = new SparseArray<>();

    protected GameThread gameThread = new GameThread();
    private SurfaceViewGame gameSurface;
    private GameWorld gameWorld;

    public Game(View rootView){
        HOST = this instanceof GameServer;
        gameSurface = (SurfaceViewGame) rootView.findViewById(R.id.surfaceView_game);
    }

    public void prepare(GameData gameData){
        new LoadingThread(gameData);
    }

    public void start(){
        gameThread.start();
    }

    protected void update() {
        while (!eventQueue.isEmpty()) {
            eventQueue.poll().apply(this);
        }

        user.update(this);

        for (int i = 0; i < sweets.size(); ++i)sweets.valueAt(i).update(this);

        for (int i = 0; i < gums.size(); ++i)gums.valueAt(i).update(this);

        for (int i = 0; i < slimes.size(); ++i)slimes.valueAt(i).update(this);

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

                gameWorld.render(c, user.getPosition());

                for (int i = 0; i < sweets.size(); ++i)sweets.valueAt(i).render(c, user.getPosition());

                for (int i = 0; i < gums.size(); ++i)gums.valueAt(i).render(c, user.getPosition());

                for (int i = 0; i < slimes.size(); ++i)slimes.valueAt(i).render(c, user.getPosition());

                lightBulbBlue.render(c, user.getPosition());
                lightBulbGreen.render(c, user.getPosition());

                for (Player player:players.values())player.render(c, user.getPosition());
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

    public void addSlime(Vector2D vector2D, int birthTick, int id){
        slimes.put(id, new SlimeTrail(vector2D, birthTick));
    }

    public void removeSlime(int id){
        slimes.remove(id);
    }

    public void addGum(Vector2D position, Vector2D velocity, int id){
        gums.put(id, new Gum(position, velocity));
    }

    public void removeGum(int id){
        gums.remove(id);
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


        private LoadingThread (GameData gameData){
            GAME_DATA = gameData;
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

            gameWorld = new GameWorld(GAME_DATA.WORLD);
            players = GAME_DATA.PLAYER_DATA.generatePlayers();
            gameSurface.setup(PREF_LAYOUT, GAME_DATA.WORLD, players.values().toArray(new Player[players.size()]));

            lightBulbBlue = new LightBulb(gameWorld.getLightBulbStandBlue());
            lightBulbGreen = new LightBulb(gameWorld.getLightBulbStandGreen());
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
