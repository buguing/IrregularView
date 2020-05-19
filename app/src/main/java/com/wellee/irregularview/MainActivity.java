package com.wellee.irregularview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IrregularDrawView irr = findViewById(R.id.irr);
        IrregularDraw2View irr2 = findViewById(R.id.irr2);
        irr.setOnClickListener(this);
        irr2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.irr:
            case R.id.irr2:
                Toast.makeText(MainActivity.this, v.getTag(v.getId()) + "", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
