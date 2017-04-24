package com.example.akash.m_ps;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    Button b1;
    ImageView v1;
    CountDownTimer ct=null;
    int i=0;

    int[] draww={R.mipmap.i6,R.mipmap.i5,R.mipmap.i11,R.mipmap.i9,R.mipmap.i10,R.mipmap.i8,R.mipmap.i4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
       // getSupportActionBar().hide();

        v1=(ImageView)findViewById(R.id.iv1);
        b1=(Button)findViewById(R.id.bb1);

        b1.setOnClickListener(this);


            ct =new CountDownTimer(30000,4000) {
            @Override
            public void onTick(long millisUntilFinished) {
               v1.setBackgroundResource(draww[i%draww.length]);
                i++;
            }

            @Override
            public void onFinish() {

                ct.start();

            }
        };
        ct.start();
    }

    @Override
    public void onClick(View v) {

        startActivity(new Intent(Main2Activity.this,MapsActivity.class));
        finish();
    }
}
