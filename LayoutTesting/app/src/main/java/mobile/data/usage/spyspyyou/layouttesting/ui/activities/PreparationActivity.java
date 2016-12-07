package mobile.data.usage.spyspyyou.layouttesting.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import mobile.data.usage.spyspyyou.layouttesting.R;
import mobile.data.usage.spyspyyou.layouttesting.ui.views.CharacterSelector;

import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ID_FLUFFY;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ID_GHOST;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ID_NOX;
import static mobile.data.usage.spyspyyou.layouttesting.teststuff.TODS.ID_SLIME;

public class PreparationActivity extends GotLActivity {

    private byte characters[] = {ID_FLUFFY, ID_SLIME, ID_GHOST, ID_NOX};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        CharacterSelector characterSelector = (CharacterSelector) findViewById(R.id.scrollView_preparation_characterTypes);
        characterSelector.onContentFinished();
    }

    @Override
    protected void onResume() {
        activeActivityRequiresServer = false;
        super.onResume();
    }

    public byte[] getCharacters(){
        return characters;
    }
}
