package mobile.data.usage.spyspyyou.newlayout.ui.adapters;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.AppBluetoothManager;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.PlayerInfo;
import mobile.data.usage.spyspyyou.newlayout.ui.messages.TeamRequest;

public class TeamAdapter extends BaseAdapter {

    private static final PlayerInfo CHANGE_TEAM = new PlayerInfo("", "", -1);
    private final View.OnClickListener changeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new TeamRequest(!TEAM_BLUE);
        }
    };
    private ArrayList<PlayerInfo> playerList = new ArrayList<>();
    private final ArrayList<String> addresses = new ArrayList<>();
    private final Handler HANDLER;
    private final LayoutInflater INFLATER;
    private boolean TEAM_BLUE;

    public TeamAdapter(Activity activity, boolean teamBlue){
        HANDLER = new Handler();
        INFLATER = activity.getLayoutInflater();
        TEAM_BLUE = teamBlue;
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Object getItem(int position) {
        return playerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return playerList.get(position).ADDRESS.hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (playerList.get(position).PIC == -1){
            if(view == null)view = INFLATER.inflate(R.layout.list_change_team, parent, false);
            ImageButton imageButtonChange = (ImageButton) view.findViewById(R.id.imageButton);
            imageButtonChange.setOnClickListener(changeClickListener);
        }else{
            if(view == null)view = INFLATER.inflate(R.layout.list_player, parent, false);
            ImageView imageViewWorld = (ImageView) view.findViewById(R.id.imageView_listPlayer_pic);
            imageViewWorld.setImageResource(playerList.get(position).PIC);

            TextView textViewName = (TextView) view.findViewById(R.id.textView_listPlayer_name);
            textViewName.setText(playerList.get(position).NAME);
        }
        return view;
    }

    public void addPlayer(String name, String address, int pic){
        addPlayer(new PlayerInfo(name, address, pic));
    }

    public void addPlayer(PlayerInfo player){
        playerList.add(player);
        addresses.add(player.ADDRESS);
        update();
    }

    @Nullable
    public PlayerInfo removePlayer(String address){
        PlayerInfo player = getPlayer(address);
        playerList.remove(player);
        if (player != null)addresses.remove(player.ADDRESS);
        update();
        return player;
    }

    public void changePlayerPic(String address, int pic){
        PlayerInfo player = getPlayer(address);
        if (player != null)player.PIC = pic;
        update();
    }

    private PlayerInfo getPlayer(String address){
        PlayerInfo p = null;
        for (PlayerInfo player:playerList){
            if (player.ADDRESS.equals(address)){
                p = player;
                break;
            }
        }
        return p;
    }

    public boolean hasPlayer(String address){
        return getPlayer(address) != null;
    }

    private void update(){
        playerList.remove(CHANGE_TEAM);
        if (!hasPlayer(AppBluetoothManager.getLocalAddress())){
            playerList.add(CHANGE_TEAM);
        }
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void setData(ArrayList<PlayerInfo> players){
        playerList = players;
        update();
    }

    public ArrayList<PlayerInfo> getData(){
        return playerList;
    }

    public ArrayList<String> getPlayerAddresses(){
        return addresses;
    }
}
