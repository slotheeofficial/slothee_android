package com.edison.Customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Vengat G on 5/16/2018.
 */


public class NormalTextview_Content extends android.support.v7.widget.AppCompatTextView {

    public NormalTextview_Content(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NormalTextview_Content(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NormalTextview_Content(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        if (!isInEditMode())
        {

            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Helvetica1.ttf");
            setTypeface(tf);
        }
    }
}
