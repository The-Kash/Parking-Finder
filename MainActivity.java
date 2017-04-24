package com.example.akash.m_ps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

   // ProgressDialog p;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

//        p = new ProgressDialog(MainActivity.this);
//        p.setTitle("Starting");
//        p.setMessage("Initialising the App");
//        p.setCancelable(false);
//        p.show();


        CountDownTimer ct = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
               // p.dismiss();
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
                finish();

            }
        };

        ct.start();
    }

}
