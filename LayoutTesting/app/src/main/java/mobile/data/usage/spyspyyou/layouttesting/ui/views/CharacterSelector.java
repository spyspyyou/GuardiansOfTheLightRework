package mobile.data.usage.spyspyyou.layouttesting.ui.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.global.App;
import mobile.data.usage.spyspyyou.layouttesting.ui.activities.PreparationActivity;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.DESCRIPTION_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.DESCRIPTION_GHOST;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.DESCRIPTION_NOX;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.DESCRIPTION_SLIME;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ICON_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ICON_GHOST;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ICON_NOX;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ICON_SLIME;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ID_GHOST;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ID_NOX;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ID_SLIME;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.NAME_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.NAME_GHOST;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.NAME_NOX;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.NAME_SLIME;

public class CharacterSelector extends HorizontalScrollView {
    private static final int
            SWIPE_DISTANCE_MIN = 6,
            SWIPE_VELOCITY_THRESHOLD = 500;

    private GestureDetector gestureDetector = new GestureDetector(new SwipeGestureDetector());
    private TextView textViewCharacterInfo;

    private byte[] characters = {};
    private byte position = 0;
    private int scrollViewWidth = 0, characterLayoutWidth, startPosition;
    private LinearLayout scrollViewLayout;
    private ArrayList<LinearLayout> childLayouts = new ArrayList<>();

    private final OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (gestureDetector.onTouchEvent(event)){
                return true;
            }
            setPosition(getScrollX() / characterLayoutWidth);
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                Log.i("fingerUp-position", ""+position);
                smoothScrollTo(position);
                return true;
            }
            return false;
        }
    };

    public CharacterSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(onTouchListener);

        characterLayoutWidth = getResources().getDimensionPixelSize(R.dimen.character_selection_length);
        startPosition = characterLayoutWidth/2;

        scrollViewLayout = new LinearLayout(getContext());
        scrollViewLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollViewLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(scrollViewLayout);
    }

    public void onContentFinished(){
        Activity activity = App.accessActiveActivity(null);
        if (characters.length == 0 && activity instanceof PreparationActivity){
            characters = ((PreparationActivity) activity).getCharacters();
        }else{
            return;
        }

        for (byte character:characters){
            childLayouts.add(createCharacterView(character));
        }

        textViewCharacterInfo = (TextView) getRootView().findViewById(R.id.textView_preparation_CharacterInfo);
        setPosition(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (scrollViewWidth != 0){
            smoothScrollTo(0);
            return;
        }
        scrollViewWidth = getMeasuredWidth();

        scrollViewLayout.addView(createSpaceWithWidth(scrollViewWidth/2));
        for (LinearLayout linearLayout: childLayouts){
            scrollViewLayout.addView(linearLayout);
        }
        scrollViewLayout.addView(createSpaceWithWidth(scrollViewWidth/2));
    }

    private Button createSpaceWithWidth(int width){
        Button space = new Button(getContext());
        space.setLayoutParams(new LayoutParams(width, 0));
        return space;
    }

    private LinearLayout createCharacterView(byte character){
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(App.accessActiveActivity(null)).inflate(R.layout.character_selection, null);
        ImageView iconCharacter = (ImageView) linearLayout.findViewById(R.id.imageView_characterSelection);
        TextView textView = (TextView) linearLayout.findViewById(R.id.textView_characterSelection);
        switch (character) {
            case ID_FLUFFY:
                iconCharacter.setImageDrawable(getResources().getDrawable(ICON_FLUFFY));
                textView.setText(NAME_FLUFFY);
                break;
            case ID_SLIME:
                iconCharacter.setImageDrawable(getResources().getDrawable(ICON_SLIME));
                textView.setText(NAME_SLIME);
                break;
            case ID_GHOST:
                iconCharacter.setImageDrawable(getResources().getDrawable(ICON_GHOST));
                textView.setText(NAME_GHOST);
                break;
            case ID_NOX:
                iconCharacter.setImageDrawable(getResources().getDrawable(ICON_NOX));
                textView.setText(NAME_NOX);
                break;
            default:
                //makes an invisible view to make scrolling player into center possible
        }
        return linearLayout;
    }

    private void setPosition(int newPosition){
        position = (byte) newPosition;
        if (position < 0)position = 0;
        else if (position >= characters.length){
            position = (byte) (characters.length - 1);
        }
        byte character = characters[position];
        switch(character){
            case ID_FLUFFY:
                textViewCharacterInfo.setText(DESCRIPTION_FLUFFY);
                break;
            case ID_SLIME:
                textViewCharacterInfo.setText(DESCRIPTION_SLIME);
                break;
            case ID_GHOST:
                textViewCharacterInfo.setText(DESCRIPTION_GHOST);
                break;
            case ID_NOX:
                textViewCharacterInfo.setText(DESCRIPTION_NOX);
                break;
        }
    }

    private void smoothScrollTo(int pos){
        super.smoothScrollTo(startPosition + pos * characterLayoutWidth, 0);
    }

    public byte getSelectedCharacter(){
        return characters[position];
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            try{
                //right to left
                if(e1.getX() - e2.getX() > SWIPE_DISTANCE_MIN && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    setPosition(++position);
                    Log.i("fling-position", ""+position);
                    smoothScrollTo(position);
                    return true;
                }
                //left to right
                else if (e2.getX() - e1.getX() > SWIPE_DISTANCE_MIN && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    setPosition(--position);
                    Log.i("fling-position", ""+position);
                    smoothScrollTo(position);
                    return true;
                }
            }catch (Exception e){
                Log.e("SHSView", "could not process fling Event");
                e.printStackTrace();
            }
            return false;
        }

    }
}
