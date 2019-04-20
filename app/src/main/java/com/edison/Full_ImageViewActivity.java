package com.edison;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.edison.Utils.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Full_ImageViewActivity extends AppCompatActivity {

    ImageView back;
    String url;
    TouchImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full__image_view);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        image = (TouchImageView) findViewById(R.id.fullimage_iew);
//        cmt_img_progress_ = (ProgressBar) findViewById(R.id.cmt_img_progress_);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        cmt_img_progress_.setVisibility(View.VISIBLE);

        /////picasso
        Picasso.with(Full_ImageViewActivity.this).load(url)
//                .resize(400,400)
//                .placeholder(R.drawable.profile_img)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
//                        cmt_img_progress_.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
//                        cmt_img_progress_.setVisibility(View.GONE);
                    }
                });

    }
}