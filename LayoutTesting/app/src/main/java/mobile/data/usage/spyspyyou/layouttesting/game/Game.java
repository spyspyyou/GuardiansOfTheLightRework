package mobile.data.usage.spyspyyou.layouttesting.game;


import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.View;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import mobile.data.usage.spyspyyou.layouttesting.game.entities.Entity;
import mobile.data.usage.spyspyyou.layouttesting.game.events.GameEvent;

public class Game {

    private static Queue<GameEvent> eventQueue = new LinkedBlockingQueue<>();
    private ArrayList<Entity> entities = new ArrayList<>();
    private GameThread gameThread;
    private GameUIManager gameUIManager;

    public Game(Resources resources,View rootView){
        gameThread = new GameThread();
        gameUIManager = new GameUIManager(rootView);
        BitmapManager.loadBitmaps(resources);
    }

    public void addEvent(GameEvent gameEvent){
        eventQueue.offer(gameEvent);
    }

    public void startGame(){
        gameThread.start();
    }

    private void update() {
        while (!eventQueue.isEmpty()) {
            eventQueue.poll().apply();
        }

        for (Entity entity : entities) {
            entity.update();
        }
    }

    private void render(){
        Canvas c = gameUIManager.getGameCanvas();
        for (Entity entity:entities){
            entity.render(c);
        }
        gameUIManager.renderGame(c);
        gameUIManager.renderJoystick();
        gameUIManager.renderMiniMap();
    }

    private class GameThread extends Thread implements Tick {

        private static final byte PAUSE_CHECK_TIME = 100;

        private boolean running, paused;

        private long tickStartTime;
        private int synchronizedTick;

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

                try {
                    sleep(TIME_PER_TICK - (System.currentTimeMillis() - tickStartTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
