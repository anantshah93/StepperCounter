package com.ioapp.steppercounter;

/**
 * Created by anantshah on 01/04/17.
 */

public interface Stepper {
    int value = 0;

    public void addStepCallback(OnStepCallback callback);

    public void removeStepCallback(OnStepCallback callback);

    public void notifyStepCallback(int value, Boolean positive);

    public void setMax(int value);

    public void setMin(int value);

    public int getValue();

    public void setValue(int value);

    public void setStepperTextColor(int color);
}
