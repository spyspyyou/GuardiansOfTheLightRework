package mobile.data.usage.spyspyyou.gametest.game;


import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
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

import static android.R.attr.id;
import static mobile.data.usage.spyspyyou.gametest.teststuff.VARS.PREF_LAYOUT;
import static mobile.data.usage.spyspyyou.gametest.teststuff.VARS.USER_AD;


public class Game {

    private static Queue<GameEvent> eventQueue = new LinkedBlockingQueue<>();
    public static boolean HOST = false;

    protected Map<String, Player> players = new LinkedHashMap<>();
    protected User user;
    private LightBulb
            lightBulbBlue,
            lightBulbGreen;

    private int
            nextGumId = 0,
            nextSweetId = 0;

    protected SparseArray<Sweet> sweets = new SparseArray<>();
    protected ArrayList<SlimeTrail> slimeTrails = new ArrayList<>();
    protected SparseArray<Gum> gums = new SparseArray<>();

    protected GameThread gameThread = new GameThread();
    private SurfaceViewGame gameSurface;
    protected GameWorld gameWorld;

    public Game(View rootView){
        HOST = this instanceof GameServer;
        gameThread.setPriority(Thread.NORM_PRIORITY + 1);
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

        gameSurface.update();
        user.update(this);

        for (int i = 0; i < gums.size(); ++i)gums.valueAt(i).update(this);
        for (int i = 0; i < sweets.size(); ++i)sweets.valueAt(i).update(this);

        Iterator<SlimeTrail> slimeTrailIterator = slimeTrails.iterator();
        SlimeTrail slimeTrail;
        while (slimeTrailIterator.hasNext()){
            slimeTrail = slimeTrailIterator.next();
            slimeTrail.update(this);
            if (slimeTrail.isDead(getSynchronizedTick()))slimeTrailIterator.remove();
        }

        lightBulbBlue.update(this);
        lightBulbGreen.update(this);
    }

    private void render(){
        Canvas c = gameSurface.startDrawing();

        if (c != null) {
            gameWorld.render(c);
            int i;
            for (i = 0; i < sweets.size(); ++i) sweets.valueAt(i).render(c);
            for (i = 0; i < gums.size(); ++i) gums.valueAt(i).render(c);
            for (SlimeTrail slimeTrail:slimeTrails) slimeTrail.render(c);

            lightBulbBlue.render(c);
            lightBulbGreen.render(c);

            for (Player player : players.values()) player.render(c);

            gameSurface.render(c);
        }
        gameSurface.finishDrawing(c);
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
        user.shootGum(this);
    }

    public void addSlimeTrail(Vector2D vector2D, int birthTick){
        Log.i("Game", "adding SlimeTrail to List");
        slimeTrails.add(new SlimeTrail(vector2D, birthTick));
    }

    public void removeSlime(SlimeTrail slimeTrail){
        slimeTrails.remove(slimeTrail);
    }

    public void addGum(Vector2D position, double direction, int shootTick, int id, boolean teamBlue){
        Log.i("Game", "adding gum");
        gums.put(id, new Gum(position, direction, teamBlue));
        gums.get(id).jump(this, getSynchronizedTick() - shootTick);
    }

    public void removeGum(){
        gums.remove(id);
    }

    public void setSynchronizedTick(int tick){
        gameThread.setSynchronizedTick(tick);
    }

    public int getSynchronizedTick(){
        return gameThread.getSynchronizedTick();
    }

    public int nextGumId(){
        return nextGumId++;
    }

    public int nextSweetId(){
        return nextSweetId++;
    }

    public void stopGame(){
        if (gameThread != null)gameThread.quitGame();
    }

    public void pause(){
        gameThread.pauseGame();
    }

    public void resume(){
        gameThread.resumeGame();
    }

    public boolean isPaused(){
        return gameThread.paused;
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

            players = GAME_DATA.PLAYER_DATA.generatePlayers();
            user = (User) players.get(USER_AD);
            gameSurface.setup(PREF_LAYOUT, GAME_DATA.WORLD, players.values().toArray(new Player[players.size()]));
            gameWorld = new GameWorld(GAME_DATA.WORLD, user.getPosition());

            lightBulbBlue = new LightBulb(gameWorld.getLightBulbStandBlue());
            lightBulbGreen = new LightBulb(gameWorld.getLightBulbStandGreen());

            gameThread.start();
        }
    }

    protected class GameThread extends Thread implements Tick {

        private static final byte PAUSE_CHECK_TIME = 100;

        private boolean running, paused;

        private long
                tickStartTime = 0,
                sleepTime = 0;

        private int
                synchronizedTick = 0;

        @Override
        public void run() {
            running = true;
            paused = false;

            Log.i("GameThread", "start with time per tick: " + TIME_PER_TICK);
            long lagSum = 0;
            int lagCount = 0;
            long startTime = System.currentTimeMillis();
            while (running) {
                while(paused && running){
                    try {
                        sleep(PAUSE_CHECK_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                tickStartTime = System.nanoTime();
                Log.d("GameThread", "tick start ---------------------------------------------------------------------------------------");

                update();
                render();
                ++synchronizedTick;

                long tickTime = (System.nanoTime() - tickStartTime);

                Log.d("GameThread", "tick took " + tickTime + " nanos, " + tickTime / 1000000 + " milis");

                sleepTime = (TIME_PER_TICK - (System.nanoTime() - tickStartTime));
                Log.d("GameThread", "sleepTime " + sleepTime + " nanos, " + sleepTime / 1000000 + " milis");

                if (sleepTime > 0) {
                    try {
                        sleep(sleepTime / 1000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    lagSum -= sleepTime;
                    ++lagCount;
                    Log.d("GameThread", "LAG:\nsum " + lagSum + " nanos\ncount " + lagCount + ",\nrun seconds " + (System.currentTimeMillis() - startTime) / 1000);
                }
            }
            Log.i("GameThread", "exit");
            Log.d("GameThread", "LAG:\nsum " + lagSum + " nanos, " + lagSum / 1000000 + " milis\ncount " + lagCount + ",\nrun seconds " + (System.currentTimeMillis() - startTime) / 1000);
        }

        private void pauseGame(){
            //todo:make better pause
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
