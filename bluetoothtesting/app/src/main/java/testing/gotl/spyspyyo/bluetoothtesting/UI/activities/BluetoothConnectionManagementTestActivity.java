package testing.gotl.spyspyyo.bluetoothtesting.UI.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import testing.gotl.spyspyyo.bluetoothtesting.R;
import testing.gotl.spyspyyo.bluetoothtesting.bluetooth.AppBluetoothManager;

public class BluetoothConnectionManagementTestActivity extends GotLActivity{

    Button findServers, findClients;
    ListView listViewAv, listViewCon;
    static ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.btconman_test_layout);
        findServers = (Button) findViewById(R.id.button16);
        findServers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, AppBluetoothManager.getServerList());
                listViewAv.setAdapter(adapter);
            }
        });
        findClients = (Button) findViewById(R.id.button13);
        findClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, AppBluetoothManager.getClientList());
                listViewAv.setAdapter(adapter);
            }
        });
        listViewAv = (ListView) findViewById(R.id.listView1);
        listViewCon = (ListView) findViewById(R.id.listView2);
    }

    public static void notifyChange(){
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}
