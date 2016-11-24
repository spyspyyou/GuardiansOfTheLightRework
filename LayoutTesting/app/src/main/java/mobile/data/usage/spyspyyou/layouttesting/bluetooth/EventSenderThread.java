package mobile.data.usage.spyspyyou.layouttesting.bluetooth;


import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//this thread also regularly sends the HandshakeEvents
/*package*/ class EventSenderThread extends Thread {

    private static final LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<>();
    private static final int MAXIMUM_WAIT_TIME = 500;

    public EventSenderThread(){
        start();
    }

    @Override
    public void run() {
        while(ConnectionManager.hasConnections()){
            try {
                events.poll(MAXIMUM_WAIT_TIME, TimeUnit.MILLISECONDS).send();
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
