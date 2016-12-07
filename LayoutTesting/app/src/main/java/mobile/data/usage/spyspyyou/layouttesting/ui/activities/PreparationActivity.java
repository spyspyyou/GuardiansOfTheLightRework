package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import mobile.data.usage.spyspyyou.layouttesting.R;

public abstract class PreparationActivity extends GotLActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparation);
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }

    @Override
    public void onConnectionLost() {

    }
}
