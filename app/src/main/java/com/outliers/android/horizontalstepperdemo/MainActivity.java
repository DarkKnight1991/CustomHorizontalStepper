package com.outliers.android.horizontalstepperdemo;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //stepper variables
    final int NUMBER_OF_STEPS = 3;
    final float PERCENTAGE_WIDTH = 80;
    final float FACTOR_RADIUS_INCREASE_ACTIVE_STEP = 0.2f;
    final float FACTOR_RADIUS_INCREASE_INACTIVE_STEP = 1.1f;
    final int HEIGHT_OF_LINE = 5; //in dp
    float lineLength;
    int diameterActive,diameterInactive;
    ArrayList<View> circles,lines;
    RelativeLayout rl_stepper;
    int widthOfEachComponent;
    int activeStep = 0;
    TextView tv;
    String[] STEP_NAMES = {"Step1\nName","Step2\nName","Step3\nName"};
    String initialColor = "#D9FC8404",done = "#D93DF706", active = "#D9F7EC06";//D9
    int STEP_NAME_TEXT_SIZE = 12;
    int screenH,screenW;
    boolean allStepsOver;
    float unUsedSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics outMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        }
        else{
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        }
        screenH = outMetrics.heightPixels;
        screenW = outMetrics.widthPixels;
        Log.d("onCreate","H:"+screenH+",W:"+screenW);

        rl_stepper = (RelativeLayout) findViewById(R.id.rl_stepper);
        findViewById(R.id.btn_next).setOnClickListener(this);

        initializeStepper();
    }

    private void initializeStepper(){
        rl_stepper.removeAllViews();
        allStepsOver = false;
        float stepperWidth = screenW * PERCENTAGE_WIDTH/(float)100;
        Log.e("initializeStepper","stepperWidth:"+stepperWidth+","+screenW);
        int numberOfLines = NUMBER_OF_STEPS -1;
        circles = new ArrayList<>();
        lines = new ArrayList<>();
        //Log.e(TAG,"numberOfLines:"+numberOfLines);
        widthOfEachComponent = (int) stepperWidth / (NUMBER_OF_STEPS + numberOfLines);
        unUsedSpace = screenW - stepperWidth;
        //Log.e(TAG,"widthOfEachComp:"+widthOfEachComponent);
        diameterInactive = (int)(widthOfEachComponent/FACTOR_RADIUS_INCREASE_INACTIVE_STEP);
        diameterActive = (int) (diameterInactive + (diameterInactive * FACTOR_RADIUS_INCREASE_ACTIVE_STEP));
        rl_stepper.setMinimumHeight(diameterActive);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_stepper.getLayoutParams();
        params.height = diameterActive + convertDpToPx(10);
        rl_stepper.setLayoutParams(params);
        rl_stepper.getParent().requestLayout();
        //Log.e(TAG,"diaInact:"+diameterInactive+",diaAct:"+diameterActive);
        addSteps();
    }

    private void addSteps(){
        for(int i=0; i<(NUMBER_OF_STEPS+(NUMBER_OF_STEPS-1)); i++){
            if(i % 2 == 0) {
                addCircle(i);
            } else {
                addLine(i);
            }
        }
    }

    private void addCircle(int i){
        RelativeLayout circle1 = (RelativeLayout) getLayoutInflater().inflate(R.layout.circle_with_text,null);
        TextView tv = (TextView)circle1.findViewById(R.id.tv_step);
        tv.setText(STEP_NAMES[i/2]);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,STEP_NAME_TEXT_SIZE);
        //View circle1 = new View(this);
        RelativeLayout.LayoutParams layoutParamsCircle1 = new RelativeLayout.LayoutParams(diameterInactive, diameterInactive);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            circle1.setId(i);
        } else {
            circle1.setId(View.generateViewId());
        }
        circle1.setBackground(ContextCompat.getDrawable(this,R.drawable.circle));

        if(lines.size() != 0) {
            layoutParamsCircle1.addRule(RelativeLayout.RIGHT_OF,(lines.get(lines.size()-1)).getId());
        }else{
            layoutParamsCircle1.leftMargin = convertDpToPx((int)(diameterInactive*0.2)/2)+ (int) unUsedSpace/2;
        }

        if(i == (NUMBER_OF_STEPS+(NUMBER_OF_STEPS-1))-1){//last step circle
            layoutParamsCircle1.rightMargin = convertDpToPx((int)(diameterInactive*0.2)/2);
        }
        layoutParamsCircle1.addRule(RelativeLayout.CENTER_VERTICAL);
        rl_stepper.addView(circle1,layoutParamsCircle1);

        //Log.e("AddCircles",circle1.getWidth()+","+circle1.getHeight());
        circles.add(circle1);
        if(Build.VERSION.SDK_INT >= 21)
            circle1.setZ(3);
    }

    private void addLine(int i) {
        View line1 = new View(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            line1.setId(i);
        } else {
            line1.setId(View.generateViewId());
        }
        RelativeLayout.LayoutParams layoutParamsLine = new RelativeLayout.LayoutParams(widthOfEachComponent, convertDpToPx(HEIGHT_OF_LINE));
        line1.setBackground(ContextCompat.getDrawable(this,R.drawable.line));
        layoutParamsLine.addRule(RelativeLayout.RIGHT_OF,(circles.get(circles.size()-1)).getId());
        layoutParamsLine.setMargins(0,diameterInactive/2,0,0);
        layoutParamsLine.addRule(RelativeLayout.CENTER_VERTICAL);
        rl_stepper.addView(line1,layoutParamsLine);
        lines.add(line1);
        if(Build.VERSION.SDK_INT >= 21)
            line1.setZ(1);
    }

    public void nextStep(){
        //Log.e("nextStep","activeStep:"+activeStep);
        final ProgressDialog progressDialog = ProgressDialog.show(this,"Please wait","processing your request...", true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scaleUp(activeStep);
                progressDialog.dismiss();
            }
        },1000); //delay to simulate long task.

    }

    private void scaleUp(final int step){
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f,1.2f,1f,1.2f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(1000);
        final View v ;
        if(step < circles.size())
            v = circles.get(step);
        else if(step == circles.size()){ //if current active step is last step scale down and finish
            scaleDown(step-1);
            return;
        }
        else {
            return;
        }

        final int orgHeight = v.getHeight();
        final int orgWidth = v.getWidth();

        //Log.e("scaleUp",v.getWidth()+","+v.getHeight());

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                //Log.e("scaleUpStart",v.getWidth()+","+v.getHeight()+","+initialColor+","+active);
                if(step > 0){
                    scaleDown(step-1);
                    //change color from orange to yellow
                }
                animateColor(v,initialColor,active);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activeStep++;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(scaleAnimation);
    }

    public void scaleDown(final int step){
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f,1f,1.2f,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(1000);
        final View v;
        if(step < circles.size() && step >= 0)
            v = circles.get(step);
        else
            return;

        final int orgHeight = v.getHeight();
        final int orgWidth = v.getWidth();

        //Log.e("scaleDown",v.getWidth()+","+v.getHeight());
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Log.e("scaleDownStart",v.getWidth()+","+v.getHeight()+","+active+","+done);
                //change color from yellow to green
                animateColor(v,active,done);
                flipView(v,0f,360f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                //lp.width = (int) (orgWidth * 0.8f);
                //lp.height = (int) (orgHeight * 0.8f);
                //v.setLayoutParams(lp);
                //rl_stepper.requestLayout();
                ((TextView)v.findViewById(R.id.tv_step)).setText("Done");
                //Log.e("scaleDownEnd",v.getWidth()+","+v.getHeight());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(scaleAnimation);
    }

    private void flipView(View v,float from, float to){
        ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(v, "rotationX", from, to);
        // Set the animation's parameters
        flipAnimator.setDuration(1500);               // duration in ms
        flipAnimator.setRepeatCount(0);                // -1 = infinite repeated
        flipAnimator.start();
        flipAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(allStepsOver) {

                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void animateColor(final View view, String fromColor, String toColor){
        try {

            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor(fromColor), Color.parseColor(toColor));
            colorAnimation.setDuration(1000); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    Drawable drawable = view.getBackground();
                    //Log.e("animateColor",drawable.getClass().getSimpleName());
                    if (drawable instanceof GradientDrawable) {
                        //Log.e("shapeDrawable", "drawable");
                        GradientDrawable shapeDrawable = (GradientDrawable) drawable;
                        shapeDrawable.setColor((int) animator.getAnimatedValue());
                        //Log.e("onColorChange", (int) animator.getAnimatedValue() + "");
                        view.setBackground(shapeDrawable);
                    }
                }

            });
            //view.bringToFront();
            colorAnimation.start();
        }catch(Exception ex){
            //Log.e("animateColor",ex.getMessage());
        }
    }

    private int convertDpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        //Log.e("convertDpToPx","dp:"+dp+",px:"+px);
        return px;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_next :
                nextStep();
                break;

            //rest view clicks here
        }
    }
}
