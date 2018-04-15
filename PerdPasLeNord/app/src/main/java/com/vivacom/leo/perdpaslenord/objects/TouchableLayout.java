package com.vivacom.leo.perdpaslenord.objects;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * Created by Leo on 08/01/2018.
 */

public class TouchableLayout extends RelativeLayout {


    public TouchableLayout(Context context) {
        super(context);
    }

    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }


}
