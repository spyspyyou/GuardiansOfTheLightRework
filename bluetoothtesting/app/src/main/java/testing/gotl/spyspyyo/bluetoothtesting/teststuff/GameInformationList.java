package testing.gotl.spyspyyo.bluetoothtesting.teststuff;

import android.widget.BaseAdapter;

import java.util.ArrayList;

public class GameInformationList extends ArrayList<GameInformation> {

    private final BaseAdapter ADAPTER;

    public GameInformationList(BaseAdapter adapter){
        ADAPTER = adapter;
    }


    @Override
    public boolean add(GameInformation gameInformation) {
        boolean b = super.add(gameInformation);
        ADAPTER.notifyDataSetChanged();
        return b;
    }
}
