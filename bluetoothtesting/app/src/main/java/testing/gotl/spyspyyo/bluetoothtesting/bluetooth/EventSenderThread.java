package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//this thread also regularly sends the HandshakeEvents
/*package*/ class EventSenderThread extends Thread {


        start();
    }

    @Override
    public void run() {
            try {
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

        if (!events.offer(event)) {
            event.onEventSendFailure(event.getReceptors());
        }
    }
}
