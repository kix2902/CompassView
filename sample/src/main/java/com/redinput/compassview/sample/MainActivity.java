package com.redinput.compassview.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.redinput.compassview.CompassView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CompassView compass = (CompassView) findViewById(R.id.compass);

        compass.setDegrees(57);
        compass.setBackgroundColor(Color.YELLOW);
        compass.setLineColor(Color.RED);
        compass.setShowMarker(false);
        compass.setRangeDegrees(270);

        compass.setOnCompassDragListener(new CompassView.OnCompassDragListener() {
            @Override
            public void onCompassDragListener(float degrees) {
                // Do what you want with the degrees
            }
        });
    }
}
