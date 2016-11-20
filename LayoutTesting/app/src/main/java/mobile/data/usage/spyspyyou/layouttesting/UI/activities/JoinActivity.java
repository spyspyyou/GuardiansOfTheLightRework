package mobile.data.usage.spyspyyou.layouttesting.UI.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import mobile.data.usage.spyspyyou.layouttesting.R;

public class JoinActivity extends GotLActivity {

    private ProgressBar progressBarSearching;
    private ImageButton imageButtonRepeat;
    private TextView textViewInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        progressBarSearching = (ProgressBar) findViewById(R.id.progressBar_join_searching);
        imageButtonRepeat = (ImageButton) findViewById(R.id.imageButton_join_repeat);
        textViewInfo = (TextView) findViewById(R.id.textView_join_info);

        imageButtonRepeat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showSearching();
                    }
                }
        );

        progressBarSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInactive();
            }
        });
    }

    private void showSearching(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int circleRadiusX = progressBarSearching.getWidth(), circleRadiusY = progressBarSearching.getHeight();
            float finalRadius = (float) Math.hypot(circleRadiusX, circleRadiusY);
            Animator animatorRevealProgressBar, animatorHideRepeatButton;
            animatorRevealProgressBar = ViewAnimationUtils.createCircularReveal(progressBarSearching, circleRadiusX, circleRadiusY, 0, finalRadius);
            animatorHideRepeatButton = ViewAnimationUtils.createCircularReveal(imageButtonRepeat, circleRadiusX, circleRadiusY, finalRadius, 0);
            animatorHideRepeatButton.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    imageButtonRepeat.setVisibility(View.INVISIBLE);
                }

            });
            progressBarSearching.setVisibility(View.VISIBLE);
            animatorRevealProgressBar.start();
            animatorHideRepeatButton.start();
        }
    }

    private void showInactive(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int circleRadiusX = progressBarSearching.getWidth(), circleRadiusY = progressBarSearching.getHeight();
            float finalRadius = (float) Math.hypot(circleRadiusX, circleRadiusY);
            Animator animatorHideProgressBar, animatorRevealRepeatButton;
            animatorRevealRepeatButton = ViewAnimationUtils.createCircularReveal(imageButtonRepeat, circleRadiusX, circleRadiusY, 0, finalRadius);
            animatorHideProgressBar = ViewAnimationUtils.createCircularReveal(progressBarSearching, circleRadiusX, circleRadiusY, finalRadius, 0);
            animatorHideProgressBar.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    progressBarSearching.setVisibility(View.INVISIBLE);
                }

            });
            imageButtonRepeat.setVisibility(View.VISIBLE);
            animatorHideProgressBar.start();
            animatorRevealRepeatButton.start();
        }
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = true;
        super.onResume();
    }
}
