package com.edison.Customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Vengat G on 5/23/2018.
 */


public class EditText extends android.support.v7.widget.AppCompatEditText {

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Helvetica1.ttf");
//            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/helveticaneue.ttf");
            setTypeface(tf);

        }
    }

}
