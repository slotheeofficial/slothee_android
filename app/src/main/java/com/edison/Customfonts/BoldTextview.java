package com.edison.Customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Vengat G on 5/16/2018.
 */


public class BoldTextview extends android.support.v7.widget.AppCompatTextView {

    public BoldTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text.length() > 0) {
            text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());
        }
        super.setText(text, type);
    }

    public BoldTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldTextview(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        if (!isInEditMode())
        {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/HelveticaNeueBold.ttf");
//            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/BoldHelvetica-Neue-_22498.ttf");
            setTypeface(tf);
        }
    }
}
