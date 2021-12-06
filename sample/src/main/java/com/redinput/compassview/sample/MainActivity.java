package com.redinput.compassview.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.redinput.compassview.CompassView;

public class MainActivity extends Activity {
    float mDegrees=0;
    CompassView compass;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compass = (CompassView) findViewById(R.id.compass);
        text = (TextView) findViewById(R.id.text);

        compass.setBackgroundColor(Color.YELLOW);
        compass.setLineColor(Color.RED);
        compass.setShowMarker(true);
        compass.setRangeDegrees(270);
        compass.setDegrees(0);
        text.setText("0 degrees");

        compass.setOnCompassDragListener(new CompassView.OnCompassDragListener() {
            @Override
            public void onCompassDragListener(float degrees) {
                // Do what you want with the degrees
                text.setText(Float.toString(Math.round(degrees))+" degrees");
            }
        });
    }

        public void plus(View view) {
        mDegrees = (mDegrees+360+10)%360;
        compass.setDegrees(mDegrees,true);
        text.setText(Float.toString(mDegrees)+" degrees");
    }
    public void minus(View view) {
        mDegrees = (mDegrees+360-10)%360;
        compass.setDegrees(mDegrees,true);
        text.setText(Float.toString(mDegrees)+" degrees");
    }
}
