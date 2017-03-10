package mobile.data.usage.spyspyyou.newlayout.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;
import mobile.data.usage.spyspyyou.newlayout.ui.activity.ClientLobbyActivity;

public class GameInformationAdapter extends BaseAdapter {

    public static final String HOST_EXTRA = "host_extra";

    private static ArrayList<GameInformation> games = new ArrayList<>();
    private final Handler HANDLER;
    private final LayoutInflater INFLATER;

    public GameInformationAdapter(Activity activity){
        HANDLER = new Handler();
        INFLATER = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        GameInformation gameInformation = games.get(position);
        if(view == null) view = INFLATER.inflate(R.layout.list_game, parent, false);

        ImageView imageViewWorld = (ImageView) view.findViewById(R.id.imageView_gameInformation_world);
        imageViewWorld.setImageBitmap(gameInformation.WORLD.getBitmapRepresentation());

        TextView textViewName = (TextView) view.findViewById(R.id.textView_gameInformation_name);
        textViewName.setText(gameInformation.GAME_NAME);

        if (!gameInformation.CHARACTERS_UNIQUE){
            TextView textViewUniqueChar = (TextView) view.findViewById(R.id.textView_gameInformation_unique);
            textViewUniqueChar.setVisibility(View.INVISIBLE);
        }

        ImageView imageViewFluffy = (ImageView) view.findViewById(R.id.imageView_gameInformation_fluffy);
        if (gameInformation.ALLOWED_FLUFFY)imageViewFluffy.setImageResource(R.drawable.ic_close_black_24dp);
        ImageView imageViewSlime = (ImageView) view.findViewById(R.id.imageView_gameInformation_slime);
        if (gameInformation.ALLOWED_SLIME)imageViewSlime.setImageResource(R.drawable.ic_close_black_24dp);
        ImageView imageViewGhost = (ImageView) view.findViewById(R.id.imageView_gameInformation_ghost);
        if (gameInformation.ALLOWED_GHOST)imageViewGhost.setImageResource(R.drawable.ic_close_black_24dp);
        ImageView imageViewNox = (ImageView) view.findViewById(R.id.imageView_gameInformation_nox);
        if (gameInformation.ALLOWED_NOX)imageViewNox.setImageResource(R.drawable.ic_close_black_24dp);

        TextView textViewSweetRegen = (TextView) view.findViewById(R.id.textView_gameInformation_sweetRegen);
        textViewSweetRegen.setText(""+gameInformation.SWEET_REGEN);
        TextView textViewManaRegen = (TextView) view.findViewById(R.id.textView_gameInformation_manaRegen);
        textViewManaRegen.setText(""+gameInformation.MANA_REGEN);
        TextView textViewSelectionTime = (TextView) view.findViewById(R.id.textView_gameInformation_selectionTime);
        textViewSelectionTime.setText(""+gameInformation.SELECTION_TIME);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_gameInformation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fab.getContext(), ClientLobbyActivity.class);
                intent.putExtra(HOST_EXTRA, games.get(position).HOST_ADDRESS);
                fab.getContext().startActivity(intent);
            }
        });

        return view;
    }

    public void clear(){
        games.clear();
    }

    public void addGame(GameInformation gameInformation){
        if (hasGame(gameInformation))return;
        games.add(gameInformation);
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                Log.i("GIAdapter", "notifying adapter data size = " + games.size());
                notifyDataSetChanged();
            }
        });
    }

    private boolean hasGame(GameInformation gameInformation){
        ArrayList<GameInformation>copyList = (ArrayList<GameInformation>) games.clone();
        for (GameInformation gameInfo:copyList) {
            if (gameInformation.HOST_ADDRESS.equals(gameInfo.HOST_ADDRESS))return true;
        }
        return false;
    }

}
