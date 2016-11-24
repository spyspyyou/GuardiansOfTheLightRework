package mobile.data.usage.spyspyyou.layouttesting.global;

import android.content.Intent;

public interface GlobalTrigger {

    void onAppStart();

    void onAppResume();

    void onAppStop();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
