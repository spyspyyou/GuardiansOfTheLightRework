package testing.gotl.spyspyyo.bluetoothtesting.bluetooth;

/*package*/ class EventReceiverThread extends Thread {
    // the Events are stored in this ArrayList of Queues, so they can get processed in the same order they got received at the beginning of every loop of the GameThread
    public static boolean active = false;

    @Override
    public void run(){
        active = true;
        // this Thread's purpose is to receive the incoming Events through the InputStream of the BluetoothSocket
        while(ConnectionManager.hasConnections()) {
            Connection[] connections = ConnectionManager.getConnections();
            for (Connection connection : connections) {
                if (connection == null) continue;
                for (Event event:connection.readEvents()){
                    event.handle();
                }
            }
        }
        active = false;
    }
}
