package com.edison.Utils;

import android.content.Context;
import android.content.Intent;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.edison.HashTagFeedsActivity;

/**
 * Created by Vengat G on 1/8/2019.
 */

public class Hashtag_yt extends ClickableSpan {
    Context context;
    TextPaint textPaint;
    public Hashtag_yt(Context ctx) {
        super();
        context = ctx;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        textPaint = ds;
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(true);
        ds.setFakeBoldText(true);
        ds.setARGB(255, 255, 255, 255);
    }

    @Override
    public void onClick(View widget)
    {

        TextView tv = (TextView) widget;
        Spanned s = (Spanned) tv.getText();
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);
        final String theWord = s.subSequence(start + 1, end).toString();

        // you can start another activity here

//        Realm realm = Realm.getDefaultInstance();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.where(HastagFeedObject.class).findAll().clear();
//                Intent intent = new Intent(context, HashTagFeedsActivity.class);
//                intent.putExtra("HashtagKey","#"+theWord);
//                context.startActivity(intent);
//            }
//        });

//        Toast.makeText(context, "Clicked ytt "+theWord, Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(context, HashTagFeedsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("HashtagKey","#"+theWord);
        context.startActivity(intent);

    }

}