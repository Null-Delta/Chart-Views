package com.zed.Charts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CheckButton extends View {

    interface OnCheckListener{
        void onCheck(CheckButton v);
    }

    boolean ischeck = true;
    int clr,textcolor,bgcolor;
    Rect r = new Rect(0,0,0,0);
    float rad;
    Paint pnt,crcl;
    String text;
    ValueAnimator anim;
    float width,height,left,top,bottom,right;
    OnCheckListener onclick;

    public CheckButton(Context context) {
        super(context);
        crcl = new Paint();
        crcl.setStrokeWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        crcl.setAntiAlias(true);
        rad = 1;

        pnt = new Paint();
        pnt.setStyle(Paint.Style.STROKE);
        pnt.setAntiAlias(true);
        pnt.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));

        anim = ValueAnimator.ofFloat(0,1);
        anim.setDuration(250);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(ischeck) rad = (float)animation.getAnimatedValue();
                else rad = 1 - (float)animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    public void setOnCheckListener(OnCheckListener cl){
        onclick = cl;
    }

    public void init(int color, boolean isch, String txt,int txtclr,int bgcl){
        clr = color;
        ischeck = isch;
        text = txt;
        textcolor = txtclr;
        bgcolor = bgcl;
        pnt.setColor(clr);
    }

    public boolean ischecked(){
        return ischeck;
    }
    public void setText(String str){
        text = str;
        pnt.getTextBounds(text,0,text.length(),r);
    }
    public void setColor(int cl){
        clr = cl;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        pnt.setColor(textcolor);
        canvas.drawText(text,height + height / 2,height / 2 + r.height() / 2f,pnt);

        crcl.setColor(Color.GRAY);
        crcl.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(height / 2,height / 2,height / 2.5f,crcl);

        crcl.setColor(clr);
        crcl.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(height / 2,height / 2,rad * height / 2.5f,crcl);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w - this.getPaddingLeft() - this.getPaddingRight();
        height = h - this.getPaddingTop() - this.getPaddingBottom() - this.getPaddingBottom() - this.getPaddingBottom() - this.getPaddingBottom();
        left = this.getPaddingLeft();
        top = this.getPaddingTop();
        right = width + left;
        bottom = height + top;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            ischeck = !ischeck;
            onclick.onCheck(this);
            anim.start();
        }
        return true;
    }
}
