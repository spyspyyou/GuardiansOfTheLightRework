package mobile.data.usage.spyspyyou.newlayout.ui.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;

public class GameInformationAdapter extends BaseAdapter {

    private ArrayList<GameInformation> games = new ArrayList<>();
    private Activity activity;

    public GameInformationAdapter(Activity usingActivity){
        activity = usingActivity;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        GameInformation gameInformation = games.get(position);
        if(view == null) view = activity.getLayoutInflater().inflate(R.layout.list_game, parent, false);

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

        return view;
    }

    public void clear(){
        games.clear();
    }

    public void addGame(GameInformation gameInformation){
        if (hasGame(gameInformation))return;
        games.add(gameInformation);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
