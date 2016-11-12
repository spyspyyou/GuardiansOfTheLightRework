package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

public class EventSenderThread extends Thread {

    private static final LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        //todo:change while condition
        while(true){
            try {
                events.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void send(Event event){
        if (!events.offer(event)) {
            event.onEventSendFailure(event.getReceptors());
            Log.w("ESThread", "Event queue is full. Event dumped.");
        }
    }
}
