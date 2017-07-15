package com.ioapp.steppercounter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anantshah on 01/04/17.
 */

public class StepperCounter extends LinearLayout implements Stepper {

    int maxValue = Integer.MAX_VALUE;
    int minValue = Integer.MIN_VALUE;
    TextView viewStepCounter;
    int count = 0;


    public StepperCounter(Context context) {
        super(context);
        //if (viewStepCounter == null) {
        viewStepCounter = (TextView) findViewById(R.id.viewTextStepperCount);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            setElevation(4f);
        }
        //}
        Log.e("viewStepCounter", " StepperCounter called 0:" + viewStepCounter);
    }

    public StepperCounter(Context context, AttributeSet attrs) {
        super(context, attrs);

        viewStepCounter = (TextView) findViewById(R.id.viewTextStepperCount);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            setElevation(4f);
        }
        //}
        Log.e("viewStepCounter", " StepperCounter called 1:" + viewStepCounter);

    }

    public StepperCounter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        viewStepCounter = (TextView) findViewById(R.id.viewTextStepperCount);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            setElevation(4f);
        }
        //}
        Log.e("viewStepCounter", " StepperCounter called 2:" + viewStepCounter);
    }

    ArrayList<OnStepCallback> callbacks = new ArrayList<OnStepCallback>();
    /*
    missing here let todo:update this code
    var count: Int by Delegates.observable(0, {
        prop, old, new -> updateView(new); notifyStepCallback(new, new > old)
    })
    */
    @Override
    public void addStepCallback(OnStepCallback callback) {
        callbacks.add(callback);

    }

    @Override
    public void removeStepCallback(OnStepCallback callback) {
        callbacks.remove(callback);

    }

    @Override
    public void notifyStepCallback(int value, Boolean positive) {
        Log.e("notifyStepCallback ","value:"+value +" Boolean:"+positive);
        for (OnStepCallback oneStepCall :
                callbacks) {
            oneStepCall.onStep(value, positive);
        }

    }

    @Override
    public void setMax(int value) {
        maxValue = value;
    }

    @Override
    public void setMin(int value) {
        minValue = value;
    }

    @Override
    public int getValue() {
        Log.e("getValue",":"+count);
        return count;
    }

    @Override
    public void setValue(int value) {
        count = value;
        updateView(count);

    }

    @Override
    public void setStepperTextColor(int color) {
        Log.e("viewStepCounter", " StepperCounter called before:" + viewStepCounter);
        if (viewStepCounter == null) {
            viewStepCounter = (TextView) findViewById(R.id.viewTextStepperCount);

        }
        Log.e("viewStepCounter", " StepperCounter called after:" + viewStepCounter);

        viewStepCounter.setTextColor(color);
    }


    public void add() {
        Log.e("count - maxValue:" ,maxValue+ " added before:" + count);
        int oldCount = count;
        if (count != maxValue) count += 1;
        Log.e("count - maxValue:" ,maxValue+ " added after:" + count);
       // setValue(count);
        updateView(count);
        notifyStepCallback(count,count>oldCount);
    }

    public void subtract() {
        int oldCount = count;
        Log.e("count - minValue:" ,minValue+ " sub before:" + count);
        if (count != minValue) count--;
        Log.e("count - minValue:" ,minValue+ " sub after:" + count);
      //  setValue(count);
        updateView(count);
        notifyStepCallback(count,count>oldCount);
    }

    private void updateView(int value) {
        viewStepCounter.setText("" + value);
        Log.e("value"," on updateView:"+viewStepCounter.getText().toString());
    }
}
