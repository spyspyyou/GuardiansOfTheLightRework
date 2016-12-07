package mobile.data.usage.spyspyyou.layouttesting.ui.activities;


public class GameActivity extends GotLActivity {

    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }
}
