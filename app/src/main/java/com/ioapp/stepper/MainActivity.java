package com.ioapp.stepper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ioapp.steppercounter.OnStepCallback;
import com.ioapp.steppercounter.StepperTouch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        StepperTouch stepperTouch = (StepperTouch)findViewById(R.id.stepperTouch);
        stepperTouch.stepper.setMin(0);
        stepperTouch.stepper.setMax(5);
        stepperTouch.stepper.addStepCallback(new OnStepCallback() {
            @Override
            public void onStep(int value, boolean positive) {
                Log.e("value",":"+value +" positive:"+positive);
                Toast.makeText(MainActivity.this,positive +"," + value, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
