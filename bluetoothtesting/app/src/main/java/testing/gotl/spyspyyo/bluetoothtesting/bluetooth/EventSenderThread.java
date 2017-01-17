package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static testing.gotl.spyspyyo.bluetoothtesting.teststuff.TODS.TEXT_ENCODING;

/*package*/ class EventSenderThread extends Thread {

    private static final int
            /**
             * Amount of time in millis to wait until cancelling the polling of the next event
             */
            MAXIMUM_WAIT_TIME = 1000;

    private static final LinkedBlockingQueue<Event>
            events = new LinkedBlockingQueue<>();

    private static boolean
            activeThreadExisting = false;

    /*package*/ EventSenderThread(){
        start();
    }

    @Override
    public void run() {
        if (activeThreadExisting) return;
        activeThreadExisting = true;
        Log.i("ESThread", "started");

        Event event = null;
        while (ConnectionManager.hasConnections()) {
            try {
                event = events.poll(MAXIMUM_WAIT_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (event != null) {

                for (String address:event.getReceptors()) {
                    try {
                        ConnectionManager.send(event.getReceptors(), event.toString().getBytes(TEXT_ENCODING));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        activeThreadExisting = false;
    }

    /*package*/ static void send(Event event) {
        if (!events.offer(event)) {
            Log.w("ESThread", "Couldn't add Event.");
        }
    }
}

