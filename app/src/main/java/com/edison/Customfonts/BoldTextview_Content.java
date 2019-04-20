package com.edison.Customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Vengat G on 5/16/2018.
 */


public class BoldTextview_Content extends android.support.v7.widget.AppCompatTextView {

    public BoldTextview_Content(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }



    public BoldTextview_Content(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldTextview_Content(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        if (!isInEditMode())
        {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/HelveticaNeueBold.ttf");
            setTypeface(tf);
        }
    }
}
