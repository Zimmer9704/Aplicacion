package com.example.aleex.musicavolley;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main3Activity extends AppCompatActivity {

    Button  repro,cancio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        repro=(Button)findViewById(R.id.reproductor);
        cancio=(Button)findViewById(R.id.canciones);

        repro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent con1 = new Intent(Main3Activity.this, MainActivity.class);
                startActivity(con1);
            }
        });

        cancio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent con2 = new Intent(Main3Activity.this, Main2Activity.class);
                startActivity(con2);
            }
        });
    }
}
