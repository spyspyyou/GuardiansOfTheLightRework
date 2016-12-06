package mobile.data.usage.spyspyyou.layouttesting.bluetooth;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import mobile.data.usage.spyspyyou.layouttesting.bluetooth.events.HandshakeEvent;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.textEncoding;

//this thread also regularly sends the HandshakeEvents
/*package*/ class EventSenderThread extends Thread {

    private static final LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<>();
    private static final int MAXIMUM_WAIT_TIME = 500;
    //todo:lower the number
    private static final int HANDSHAKE_RATE = 10000;
    private static boolean running = false;

    /*package*/ EventSenderThread(){
        start();
    }

    @Override
    public void run() {
        if (running) return;
        running = true;
        Log.i("ESThread", "started");
        Event event = null;
        long lastTime = System.currentTimeMillis();
        while (ConnectionManager.hasConnections()) {
            try {
                event = events.poll(MAXIMUM_WAIT_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (event != null) {
                for (String address:event.getReceptors()) {
                    try {
                        if (!ConnectionManager.send(address, event.toString().getBytes(textEncoding))) event.onEventSendFailure(address);
                        else Log.i("ESThread", "sent Event: " + event.toString());
                    } catch (UnsupportedEncodingException e) {
                        Log.e("ESThread", "encoding not supported");
                        e.printStackTrace();
                    }
                }
                if (event instanceof OnPostEventSending){
                    ((OnPostEventSending) event).onPostSending();
                }
            }
            if (System.currentTimeMillis() - lastTime >= HANDSHAKE_RATE) {
                new HandshakeEvent(ConnectionManager.getAddresses()).send();
                lastTime = System.currentTimeMillis();
            }
        }
        running = false;
    }

    /*package*/ static void send(Event event) {
        if (!events.offer(event)) {
            event.onEventSendFailure(event.getReceptors());
            Log.w("ESThread", "Event queue is full. Event dumped.");
        }
    }
}
