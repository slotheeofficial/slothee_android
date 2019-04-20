package com.edison;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.edison.Utils.SessionManager;

public class SplashScreen extends AppCompatActivity {

    Handler handler;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();
        sm  = new SessionManager(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();

//                if(sm.getUserObject()!=null)
//                {
//                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//                else
//                {
//                    Intent intent = new Intent(SplashScreen.this,SignUpManually.class);
//                    startActivity(intent);
//                    finish();
//                }


            }
        },3000);

    }
}
