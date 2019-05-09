package com.zed.Others;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.zed.Charts.R;

public class CheckButton extends View {

    public interface OnCheckListener{
        void onCheck(CheckButton v);
    }

    Drawable checkic;
    public boolean ischeck = true,istouch;
    int clr,num;
    public int textcolor;
    public ButtonsView parent;
    int bgcolor;
    Rect r;
    float rad;
    Paint pnt,crcl;
    String text;
    ValueAnimator anim,textanim,textmoveanim;
    public float width,height,left,top,bottom,right,textx;
    OnCheckListener onclick;
    public ValueAnimator v;

    public CheckButton(Context context, String str) {
        super(context);

        v = ValueAnimator.ofFloat(0,1);
        v.setDuration(500);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if((float)animation.getAnimatedValue() == 1 && istouch){
                    for(int i = 0; i < parent.btns.length; i++) {
                        if (i == num) {
                            if(!parent.btns[i].ischeck) {
                                parent.btns[i].ischeck = true;

                                if (!parent.btns[i].ischeck)
                                    parent.btns[i].textanim = ValueAnimator.ofArgb(Color.WHITE, parent.btns[i].clr);
                                if (parent.btns[i].ischeck)
                                    parent.btns[i].textanim = ValueAnimator.ofArgb(parent.btns[i].clr, Color.WHITE);

                                if (!parent.btns[i].ischeck)
                                    parent.btns[i].textmoveanim = ValueAnimator.ofFloat(parent.btns[i].height / 6 * 5, parent.btns[i].left + (parent.btns[i].width - parent.btns[i].r.width()) / 2);
                                if (parent.btns[i].ischeck)
                                    parent.btns[i].textmoveanim = ValueAnimator.ofFloat(parent.btns[i].left + (parent.btns[i].width - parent.btns[i].r.width()) / 2, parent.btns[i].height / 6 * 5);

                                final int finalI = i;
                                parent.btns[i].textmoveanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        parent.btns[finalI].textx = (float) animation.getAnimatedValue();
                                    }
                                });
                                parent.btns[i].textmoveanim.setDuration(250);

                                parent.btns[i].textanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        parent.btns[finalI].textcolor = (int) animation.getAnimatedValue();
                                    }
                                });
                                parent.btns[i].textanim.setDuration(250);
                                parent.btns[i].textanim.start();
                                parent.btns[i].anim.start();
                                parent.btns[i].textmoveanim.start();
                                parent.btns[i].onclick.onCheck(parent.btns[i]);
                                istouch = false;
                            }
                            istouch = false;
                        } else if (parent.btns[i].ischeck) {
                            {
                                parent.btns[i].ischeck = false;

                                if (!parent.btns[i].ischeck)
                                    parent.btns[i].textanim = ValueAnimator.ofArgb(Color.WHITE, parent.btns[i].clr);
                                if (parent.btns[i].ischeck)
                                    parent.btns[i].textanim = ValueAnimator.ofArgb(parent.btns[i].clr, Color.WHITE);

                                if (!parent.btns[i].ischeck)
                                    parent.btns[i].textmoveanim = ValueAnimator.ofFloat(parent.btns[i].height / 6 * 5, parent.btns[i].left + (parent.btns[i].width - parent.btns[i].r.width()) / 2);
                                if (parent.btns[i].ischeck)
                                    parent.btns[i].textmoveanim = ValueAnimator.ofFloat(parent.btns[i].left + (parent.btns[i].width - parent.btns[i].r.width()) / 2, parent.btns[i].height / 6 * 5);

                                final int finalI = i;
                                parent.btns[i].textmoveanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        parent.btns[finalI].textx = (float) animation.getAnimatedValue();
                                    }
                                });
                                parent.btns[i].textmoveanim.setDuration(250);

                                parent.btns[i].textanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        parent.btns[finalI].textcolor = (int) animation.getAnimatedValue();
                                    }
                                });
                                parent.btns[i].textanim.setDuration(250);
                                parent.btns[i].textanim.start();
                                parent.btns[i].anim.start();
                                parent.btns[i].textmoveanim.start();
                                parent.btns[i].onclick.onCheck(parent.btns[i]);
                                istouch = false;
                            }
                        }
                    }
                }
            }
        });
        crcl = new Paint();
        crcl.setStrokeWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        crcl.setAntiAlias(true);
        rad = 1;

        pnt = new Paint();
        pnt.setAntiAlias(true);
        pnt.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));

        r = new Rect(0,0,0,0);
        text = str;
        pnt.getTextBounds(text,0,text.length(),r);

        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
        left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        width = (int)(r.right - r.left + height * 1.5);

        anim = ValueAnimator.ofFloat(0,1);
        anim.setDuration(250);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(ischeck) {
                    rad = (float)animation.getAnimatedValue();
                }
                else{
                    rad = 1 - (float)animation.getAnimatedValue();
                }
                invalidate();
            }
        });

        textanim = ValueAnimator.ofArgb(Color.WHITE,clr);
        textanim.setDuration(250);
        textanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textcolor = (int)animation.getAnimatedValue();
            }
        });

        checkic = getResources().getDrawable(R.drawable.check);


        textcolor = Color.WHITE;
    }

    public void setPatent(ButtonsView b, int i){
        num = i;
        parent = b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        pnt.getTextBounds(text,0,text.length(),r);
        Log.i("Test",String.valueOf(r.width() + height * 1.5) + " - " + String.valueOf((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics())));
        int wdt = (int)(r.right - r.left + height * 1.5);
        setMeasuredDimension((int)(wdt), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
    }

    public void setOnCheckListener(OnCheckListener cl){
        onclick = cl;
    }

    public void init(int color, boolean isch, String txt,int bgcl){
        clr = color;
        ischeck = isch;
        text = txt;
        textcolor = clr;
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

        crcl.setColor(clr);
        crcl.setAlpha(255);
        crcl.setStyle(Paint.Style.STROKE);
        canvas.drawArc(left + crcl.getStrokeWidth(),top +  crcl.getStrokeWidth(),height -  crcl.getStrokeWidth(),height -  crcl.getStrokeWidth(),90,180,false,crcl);
        canvas.drawArc(left + right - height,top +  crcl.getStrokeWidth(),left + right -  crcl.getStrokeWidth(),height -  crcl.getStrokeWidth(),-90,180,false,crcl);
        canvas.drawLine(left + height / 2,top + crcl.getStrokeWidth(),left + right - height / 2,top + crcl.getStrokeWidth(),crcl);
        canvas.drawLine(left + height / 2,top + height - crcl.getStrokeWidth(),left + right - height / 2,top + height - crcl.getStrokeWidth(),crcl);

        crcl.setColor(clr);
        crcl.setAlpha((int)(255f * rad));
        crcl.setStyle(Paint.Style.FILL);
        canvas.drawArc(left + crcl.getStrokeWidth(),top +  crcl.getStrokeWidth(),height -  crcl.getStrokeWidth(),height -  crcl.getStrokeWidth(),90,180,false,crcl);
        canvas.drawArc(left + right - height + crcl.getStrokeWidth(),top + crcl.getStrokeWidth(),left + right - crcl.getStrokeWidth(),height -  crcl.getStrokeWidth(),-90,180,false,crcl);
        canvas.drawRect(new RectF(left + height / 2,top + crcl.getStrokeWidth(),left + right - height / 2,top + height - crcl.getStrokeWidth()),crcl);

        pnt.setColor(textcolor);
        pnt.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(text,textx,height / 2 + r.height() / 2f,pnt);

        checkic.mutate().setAlpha((int)(255f * rad));
        checkic.draw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        height = h - this.getPaddingTop() - this.getPaddingBottom() - this.getPaddingBottom() - this.getPaddingBottom() - this.getPaddingBottom();
        width = (int)(r.right - r.left + height * 1.5);

        left = this.getPaddingLeft();
        top = this.getPaddingTop();
        right = width + left;
        bottom = height + top;
        checkic.setBounds((int)(left + height / 4),(int)(top + height / 4),(int)(left + height / 4 + height / 2),(int)(top + height / 4 + height / 2));
        textx = height / 6 * 5;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            istouch = true;
            v.start();
        }
        if(event.getAction() == MotionEvent.ACTION_UP && istouch){
            ischeck = !ischeck;
            istouch = false;
            onclick.onCheck(this);

            if(!ischeck) textanim = ValueAnimator.ofArgb(Color.WHITE,clr);
            if(ischeck) textanim = ValueAnimator.ofArgb(clr,Color.WHITE);

            if(!ischeck) textmoveanim = ValueAnimator.ofFloat(height / 6 * 5, left + (width - r.width()) / 2);
            if(ischeck) textmoveanim = ValueAnimator.ofFloat( left + (width - r.width()) / 2,height / 6 * 5);

            textmoveanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    textx = (float)animation.getAnimatedValue();
                }
            });
            textmoveanim.setDuration(250);

            textanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    textcolor = (int)animation.getAnimatedValue();
                }
            });
            textanim.setDuration(250);
            textanim.start();
            anim.start();
            textmoveanim.start();
        }
        return true;
    }
}
