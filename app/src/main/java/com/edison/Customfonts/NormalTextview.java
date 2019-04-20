package com.edison.Customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Vengat G on 5/16/2018.
 */


public class NormalTextview extends android.support.v7.widget.AppCompatTextView {

    public NormalTextview(Context context, AttributeSet attrs, int defStyle) {
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


    public NormalTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NormalTextview(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        if (!isInEditMode())
        {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Helvetica1.ttf");
//            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/helveticaneue.ttf");
            setTypeface(tf);
        }
    }
}
