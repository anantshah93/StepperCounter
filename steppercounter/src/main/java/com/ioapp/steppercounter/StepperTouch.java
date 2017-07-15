package com.ioapp.steppercounter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.animation.SpringAnimation;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import static com.ioapp.steppercounter.UtilsTag.dp2px;

/**
 * Created by anantshah on 01/04/17.
 */

public class StepperTouch extends FrameLayout implements OnStepCallback {
    Context context;

    public StepperTouch(@NonNull Context context) {
        super(context);
        this.context = context;
        initSetup();

        prepareElements();

    }


    public StepperTouch(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initSetup();
        handleAttrs(attrs);

    }

    public StepperTouch(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initSetup();
        handleAttrs(attrs);
    }

    private FrameLayout root;
    // Stepper view
    public Stepper stepper;
    private StepperCounter viewStepper;
    private TextView textViewNegative;
    private TextView textViewPositive;


    // Drawing properties
    private Path clipPath = new Path();
    private RectF rect = null;

    private int defaultHeight;
    private int newHeight = 0;

    // Animation properties
    private float stiffness = 200f;
    private float damping = 0.6f;
    private float startX = 0f;

    // Style properties
    private int stepperBackground = R.color.stepper_background;
    private int stepperActionColor = R.color.stepper_actions;
    private int stepperActionColorDisabled = R.color.stepper_actions_disabled;
    private int stepperTextColor = R.color.stepper_text;
    private int stepperButtonColor = R.color.stepper_button;
    private int stepperTextSize = 20;

    private void initSetup() {
        if (context != null) {
            Log.d("context", " not null anant");
            defaultHeight = (int) dp2px(context, 40f);
        }
        setClipChildren(true);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                getViewTreeObserver().removeOnPreDrawListener(this);
                newHeight = getHeight();
                setStepperSize(viewStepper);
                return true;
            }
        });

    }

    private void handleAttrs(AttributeSet attrs) {
        TypedArray styles = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StepperTouch, 0, 0);

        try {
            stepperBackground = styles.getResourceId(R.styleable.StepperTouch_stepperBackgroundColor, R.color.stepper_background);
            stepperActionColor = styles.getResourceId(R.styleable.StepperTouch_stepperActionsColor, R.color.stepper_actions);
            stepperActionColorDisabled = styles.getResourceId(R.styleable.StepperTouch_stepperActionsDisabledColor, R.color.stepper_actions_disabled);
            stepperTextColor = styles.getResourceId(R.styleable.StepperTouch_stepperTextColor, R.color.stepper_text);
            stepperButtonColor = styles.getResourceId(R.styleable.StepperTouch_stepperButtonColor, R.color.stepper_button);
            stepperTextSize = styles.getDimensionPixelSize(R.styleable.StepperTouch_stepperTextSize, (int) getResources().getDimension(R.dimen.st_textsize));
        } finally {
            styles.recycle();
            prepareElements();
        }
    }


    private void prepareElements() {
        // Set width based on height
        if (getHeight() == 0)
            newHeight = defaultHeight;
        else
            getHeight();

        // Set radius based on height
        GradientDrawable parentRadius = getRadiusBackgroundShape((float) newHeight);
        this.setBackground(parentRadius);
        parentRadius.setColor(ContextCompat.getColor(context, stepperBackground));

        textViewNegative = createTextView(1, "-", Gravity.START, stepperActionColorDisabled);
        addView(textViewNegative);
        textViewPositive = createTextView(0, "+", Gravity.END, stepperActionColor);
        addView(textViewPositive);

        // Add draggable viewStepper to the container
        viewStepper = createStepper();
        addView(viewStepper);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                viewStepper.setTranslationX(event.getX() - startX);
                return true;
            case MotionEvent.ACTION_UP:
                if (viewStepper.getTranslationX() > viewStepper.getWidth() * 0.5) {
                    viewStepper.add();
                } else if (viewStepper.getTranslationX() < -(viewStepper.getWidth() * 0.5)) {
                    viewStepper.subtract();
                }
                if (viewStepper.getTranslationX() != 0f) {
                    SpringAnimation animX = new SpringAnimation(viewStepper, SpringAnimation.TRANSLATION_X, 0f);
                    animX.getSpring().setStiffness(stiffness);
                    animX.getSpring().setDampingRatio(damping);
                    animX.start();
                }
                return true;
            default:
                return false;
        }
    }


    private StepperCounter createStepper() {
        StepperCounter view = (StepperCounter) LayoutInflater.from(context).inflate(R.layout.view_step_counter, null);
        setStepperSize(view);
        view.addStepCallback(this);
        view.setStepperTextColor(ContextCompat.getColor(context, stepperTextColor));
        // Set stepper interface
        this.stepper = view;
        return view;
    }


    private void setStepperSize(StepperCounter view) {
        view.setLayoutParams(new LayoutParams(newHeight, newHeight, Gravity.CENTER));
        GradientDrawable stepperRadius = getRadiusBackgroundShape((float) newHeight);
        stepperRadius.setColor(ContextCompat.getColor(context, stepperButtonColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(stepperRadius);
        } else {
            view.setBackgroundDrawable(stepperRadius);

        }
    }


    @Override
    public void onStep(int value, boolean positive) {
        int negativeColor = stepperActionColorDisabled;
        int positiveColor = stepperActionColorDisabled;

        if (value == viewStepper.minValue) {
            negativeColor = stepperActionColorDisabled;
        } else {
            negativeColor = stepperActionColor;
        }
        if (value == viewStepper.maxValue) {
            positiveColor = stepperActionColorDisabled;
        } else {
            positiveColor = stepperActionColor;
        }

        textViewNegative.setTextColor(ContextCompat.getColor(context, negativeColor));

        textViewPositive.setTextColor(ContextCompat.getColor(context, positiveColor));

    }


    private TextView createTextView(int id, String text, int gravity, @ColorRes int color) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(20f);
        textView.setId(id);
        textView.setTextColor(ContextCompat.getColor(context, color));
        ViewGroup.LayoutParams paramsTextView = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, gravity);
        textView.setLayoutParams(paramsTextView);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        int margin = pxFromDp(12f);

        final ViewGroup.MarginLayoutParams lpt = (MarginLayoutParams) textView.getLayoutParams();
//        lpt.setMargins(leftMargin,lpt.topMargin,lpt.rightMargin,lpt.bottomMargin);
        lpt.setMargins(margin, 0, margin, 0);

        return textView;
    }


    private GradientDrawable getRadiusBackgroundShape(Float radius) {
        GradientDrawable radiusBackground = new GradientDrawable();
        radiusBackground.setCornerRadius(radius);
        return radiusBackground;
    }


    /**
     * Handle clipping of the rounded container
     */
    @Override
    protected void onDraw(Canvas canvas) {
        rect = new RectF(canvas.getClipBounds());
        // Clipping rounded corner
        Log.e("getHeight", ":" + canvas.getHeight());
        Float r = Float.valueOf(canvas.getHeight() / 2);
        Log.e("onDraw ", "r:" + r);
        clipPath.addRoundRect(rect, r, r, Path.Direction.CW);
        Log.e("clipPath", ":" + clipPath);

        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }


    private int pxFromDp(Float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
