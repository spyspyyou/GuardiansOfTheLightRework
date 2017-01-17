package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;


/*package*/ class EventReceiverThread extends Thread {

    private static final int
            SLEEP_TIME = 100;

    private static boolean
            activeThreadExisting = false;

    /*package*/ EventReceiverThread(){
        start();
    }

    @Override
    public void run(){
        if (activeThreadExisting)return;
        activeThreadExisting = true;

        while(ConnectionManager.hasConnections()) {
            boolean received = false;
            Connection[] connections = ConnectionManager.getConnections();
            for (Connection connection : connections) {
                if (connection == null) continue;
                for (Event event:connection.readEvents()){
                    received = true;
                    event.handle();
                }
            }
            if (!received) try {
                sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        activeThreadExisting = false;
    }
}
