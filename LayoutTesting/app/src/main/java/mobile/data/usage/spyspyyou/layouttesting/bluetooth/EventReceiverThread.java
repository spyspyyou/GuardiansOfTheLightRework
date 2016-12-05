package mobile.data.usage.spyspyyou.layouttesting.bluetooth;

/*package*/ class EventReceiverThread extends Thread {

    private static boolean running = false;

    /*package*/ EventReceiverThread(){
        start();
    }

    @Override
    public void run(){
        if (running)return;
        running = true;
        // this Thread's purpose is to receive the incoming Events through the InputStream of the BluetoothSocket
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
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        running = false;
    }
}
