package mobile.data.usage.spyspyyou.newlayout.ui.views;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.data.usage.spyspyyou.newlayout.R;
import mobile.data.usage.spyspyyou.newlayout.bluetooth.GameInformation;

import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.DESCRIPTION_FLUFFY;
import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.DESCRIPTION_GHOST;
import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.DESCRIPTION_NOX;
import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.DESCRIPTION_SLIME;
import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.ID_GHOST;
import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.ID_NOX;
import static mobile.data.usage.spyspyyou.newlayout.game.GameVars.ID_SLIME;

public class ScrollViewCharacter extends HorizontalScrollView implements GestureDetector.OnGestureListener{

    private static final int
            SWIPE_DISTANCE_MIN = 5,
            SWIPE_VELOCITY_THRESHOLD = 400;

    private static final float
            FACTOR_CHILD = 0.65f,
            FACTOR_HEIGHT = 0.9f;

    private GestureDetectorCompat gestureDetector;

    private TextView textViewCharacterInfo;

    private int position = 0;

    private Byte[] characters;

    boolean resized = false;

    private int
            width = 0,
            height = 0,
            childSize = 0;

    public ScrollViewCharacter(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetectorCompat(context, this);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (resized)return;
                resized = true;
                width = getMeasuredWidth();
                height = getMeasuredHeight();
                childSize = (int) (FACTOR_CHILD * Math.min(width, height));

                ViewGroup.LayoutParams params;
                int spaceSize = width / 2 - childSize / 2;
                View
                        spaceLeft = findViewById(R.id.space_left),
                        spaceRight = findViewById(R.id.space_right);
                final ImageView
                        fluffy = (ImageView) findViewById(R.id.imageView_dialogSelection_fluffy),
                        slime = (ImageView) findViewById(R.id.imageView_dialogSelection_slime),
                        ghost = (ImageView) findViewById(R.id.imageView_dialogSelection_ghost),
                        nox = (ImageView) findViewById(R.id.imageView_dialogSelection_nox);

                params = spaceLeft.getLayoutParams();
                params.width = spaceSize;

                params = spaceRight.getLayoutParams();
                params.width = spaceSize;

                params = fluffy.getLayoutParams();
                params.width = childSize;
                params.height = (int) (FACTOR_HEIGHT * childSize);

                params = slime.getLayoutParams();
                params.width = childSize;
                params.height = (int) (FACTOR_HEIGHT * childSize);

                params = ghost.getLayoutParams();
                params.width = childSize;
                params.height = (int) (FACTOR_HEIGHT * childSize);

                params = nox.getLayoutParams();
                params.width = childSize;
                params.height = (int) (FACTOR_HEIGHT * childSize);

                params = getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))return true;

        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
            smoothScrollTo(position);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        setPosition((int) (Math.round((getScrollX() * 1.0) / childSize)));
        if (textViewCharacterInfo == null)return;
        switch(characters[position]){
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
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void setPosition(int position){
        if (position == this.position)return;
        if (position < 0)position = 0;
        else if (position >= characters.length)position = characters.length - 1;
        this.position = position;
    }

    private void smoothScrollTo(int pos){
        super.smoothScrollTo(pos * childSize, 0);
    }

    public void setup(GameInformation gameInformation, TextView textViewInfo){
        Log.i("SVCharacter", "setup");

        textViewCharacterInfo = textViewInfo;

        ArrayList<Byte> tempCharacters = new ArrayList<>();
        final ImageView
                fluffy = (ImageView) findViewById(R.id.imageView_dialogSelection_fluffy),
                slime = (ImageView) findViewById(R.id.imageView_dialogSelection_slime),
                ghost = (ImageView) findViewById(R.id.imageView_dialogSelection_ghost),
                nox = (ImageView) findViewById(R.id.imageView_dialogSelection_nox);
        Handler handler = new Handler();

        if (gameInformation.ALLOWED_FLUFFY){
            tempCharacters.add(ID_FLUFFY);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    fluffy.setVisibility(View.VISIBLE);
                }
            });
        }

        if (gameInformation.ALLOWED_SLIME){
            tempCharacters.add(ID_SLIME);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    slime.setVisibility(View.VISIBLE);
                }
            });
        }

        if (gameInformation.ALLOWED_GHOST){
            tempCharacters.add(ID_GHOST);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ghost.setVisibility(View.VISIBLE);
                }
            });
        }

        if (gameInformation.ALLOWED_NOX){
            tempCharacters.add(ID_NOX);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    nox.setVisibility(View.VISIBLE);
                }
            });
        }

        characters = tempCharacters.toArray(new Byte[tempCharacters.size()]);
        smoothScrollTo(0);
    }

    public byte getSelectedCharacter(){
        return characters[position];
    }

    //catching the Fling movement

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try{
            //right to left
            if(e1.getX() - e2.getX() > SWIPE_DISTANCE_MIN && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                setPosition(++position);
                smoothScrollTo(position);
                return true;
            }
            //left to right
            else if (e2.getX() - e1.getX() > SWIPE_DISTANCE_MIN && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                setPosition(--position);
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
