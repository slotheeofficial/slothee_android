package com.edison.Customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Vengat G on 5/23/2018.
 */


public class Button extends android.support.v7.widget.AppCompatButton {

    public Button(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Button(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
//            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/BoldHelvetica-Neue-_22498.ttf");
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/HelveticaNeueBold.ttf");
            setTypeface(tf);
        }
    }

}
