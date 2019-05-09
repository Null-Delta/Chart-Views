package com.zed.PersentChart;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.zed.Charts.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PieChart {

    protected class PointInfo {
        long x;
        int y;

        PointInfo(long xx, int yy) {
            x = xx;
            y = yy;
        }
    }

    protected class LineInfo {
        Paint pnt;
        float mult;
        boolean isshow;
        String name;
        PointInfo[] points;
        ValueAnimator alphaanim;

        public LineInfo(PointInfo[] pnts, int cl) {
            isshow = true;
            pnt = new Paint();
            pnt.setStrokeWidth(linesize);
            pnt.setColor(cl);
            pnt.setStyle(Paint.Style.STROKE);
            points = pnts;
            mult = 1;
        }

        public void startanim() {
            alphaanim = new ValueAnimator();
            alphaanim.setDuration(250);
            if (isshow) alphaanim.setFloatValues(0, 1);
            else alphaanim.setFloatValues(1, 0);
            alphaanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mult = (float) animation.getAnimatedValue();
                    parent.invalidate();
                }
            });
            alphaanim.start();
        }
    }

    //protected LineInfo[] points;

    protected float[] dis;

    public int selectpart = -1;
    public boolean[] select;

    public float newnums[], oldnums[];

    protected byte step_num;

    public float touchx,touchy;

    protected float startx;

    protected int linesize;

    private float scale, linesz;

    protected int infox, stepalpha, stepdate;

    protected boolean drawgrid, istouch, ismove = false, waslongtouch = false,isstart;

    public PersentChart parent;

    Path path;

    DateFormat df = new SimpleDateFormat("MMM dd", Locale.US), df2 = new SimpleDateFormat("MMM dd yyyy", Locale.US);
    Paint text = new Paint();
    Paint popuppnt = new Paint();
    Date d;

    public ValueAnimator moveanim,startanim;

    Paint infopaint = new Paint(), p = new Paint(), bg = new Paint();

    @SuppressLint("ClickableViewAccessibility")
    public PieChart(PersentChart context) {

        parent = context;
        path = new Path();

        scale = 1;

        startx = 0;

        drawgrid = true;

        text.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics()));
        text.setStyle(Paint.Style.STROKE);
        text.setAntiAlias(true);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setColor(MainActivity.textcolor);

        popuppnt.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, parent.getResources().getDisplayMetrics()));
        popuppnt.setAntiAlias(true);
        popuppnt.setTypeface(Typeface.DEFAULT_BOLD);

        stepdate = 1;

        moveanim = ValueAnimator.ofFloat(0,1);
        moveanim.setDuration(250);
        moveanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for(int i = 0; i < dis.length; i++){
                    if(select[i]){
                        if(dis[i] < (float)animation.getAnimatedValue() * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, parent.parent.parent.getResources().getDisplayMetrics()))
                        dis[i] = (float)animation.getAnimatedValue() * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, parent.parent.parent.getResources().getDisplayMetrics());
                    }
                    else if (dis[i] > (1f - (float)animation.getAnimatedValue()) *  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, parent.parent.parent.getResources().getDisplayMetrics())){
                        dis[i] = (1f - (float)animation.getAnimatedValue()) *  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, parent.parent.parent.getResources().getDisplayMetrics());
                    }
                }
                parent.invalidate();
            }
        });

        startanim = ValueAnimator.ofFloat(0,1);
        startanim.setDuration(500);
        startanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for(int i = 0; i < parent.points.length; i++){
                    if(parent.isshow[i])
                    parent.points[i].mult = (float)animation.getAnimatedValue();
                }
                parent.invalidate();
                if((float)animation.getAnimatedValue() == 1) isstart = false;
            }
        });
    }


    public float getPersent(float a){
        float sum = 0;
        for(int n = 0; n < parent.points.length; n++){
            for(int i = parent.getStartPoint(); i < parent.getEndPoint() + 1; i++) {
                if(isstart){
                    if(parent.isshow[n])
                    sum += parent.points[n].points[i].y;
                } else {
                    sum += parent.points[n].points[i].y * parent.points[n].mult;
                }
            }
        }
        return a / sum * 100;
    }
    public float getH(int k){
        float h = 0;
        for(int n = 0; n < k; n++){
            for(int i = parent.getStartPoint(); i < parent.getEndPoint() + 1; i++) {
                h += parent.points[n].points[i].y * parent.points[n].mult;
            }
        }
        return h;
    }
    public float getN(int k){
        float h = 0;
            for(int i = parent.getStartPoint(); i < parent.getEndPoint() + 1; i++) {
                h += parent.points[k].points[i].y * parent.points[k].mult;
            }
        return h;
    }

    public float getHT(int i){
        float h = 0;
        for(int n = 0; n <= i; n++){
            if(parent.isshow[n]) h += parent.points[n].points[0].y;
        }
        return h;
    }

    public void ontouch(float x, float y){

        touchx = x;
        touchy = y;
        for(int i = 0; i < select.length; i++){
            select[i] = false;
        }

        istouch = false;
        selectpart = -1;

    double angle = ((180 - Math.toDegrees(Math.atan2((y - parent.height / 2 - parent.top),(parent.width / 2 - x)))));

        for(int i = parent.points.length - 1; i >= 0; i--) {
            if((360f) *getPersent(getH(i + 1)) / 100f > angle && angle > (360f) * getPersent(getH(i + 1)) / 100f - (360f) * getPersent(getN(i)) / 100f){
                if(Math.pow(Math.pow(x - parent.width/2,2) + Math.pow(y - parent.height/2 - parent.top,2),0.5) < parent.height / 2) {
                    select[i] = true;
                    istouch = true;
                    selectpart = i;
                }
            }

        }
        moveanim.start();
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(MainActivity.bgc);

        for(int i = parent.points.length; i >= 1; i--) {
            if (parent.points[i - 1].mult != 0) {
                float dx,dy;
                dx = (float)Math.cos(((Math.toRadians(360f) * getPersent(getH(i)) / 100f) - ((Math.toRadians(360f) * getPersent(getN(i - 1)) / 100f)/2f))) * dis[i - 1];
                dy = (float)Math.sin(((Math.toRadians(360f) * getPersent(getH(i)) / 100f) - ((Math.toRadians(360f) * getPersent(getN(i - 1)) / 100f)/2f))) * dis[i - 1];


                parent.points[i-1].pnt.setStyle(Paint.Style.FILL_AND_STROKE);
                parent.points[i-1].pnt.setStrokeWidth(1);
                canvas.drawArc(parent.width / 2 - parent.height / 2 + dx, parent.top + dy, parent.width / 2 + parent.height / 2 + dx, parent.bottom + dy, 360f * getPersent(getH(i - 1)) / 100f, 360f * getPersent(getH(i)) / 100f - 360f * getPersent(getH(i - 1)) / 100f, true, parent.points[i - 1].pnt);
            }
        }
        for(int i = parent.points.length - 1; i >= 0; i--) {
            if (parent.points[i].mult != 0) {

                float dx,dy;
                dx = (float)Math.cos(((Math.toRadians(360f) * getPersent(getH(i + 1)) / 100f) - ((Math.toRadians(360f) * getPersent(getN(i)) / 100f)/2f))) * dis[i];
                dy = (float)Math.sin(((Math.toRadians(360f) * getPersent(getH(i + 1)) / 100f) - ((Math.toRadians(360f) * getPersent(getN(i)) / 100f)/2f))) * dis[i];

                Rect r = new Rect(0,0,0,0);
                parent.text.setColor(Color.WHITE);
                parent.text.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, parent.parent.getResources().getDisplayMetrics()) * (getPersent(getN(i)) / 150f + 0.25f));
                text.getTextBounds(String.valueOf(Math.round(getPersent(getN(i)))) + "%",0,String.valueOf(Math.round(getPersent(getN(i)))).length() + 1,r);
                //canvas.drawLine(parent.width/2,parent.height/2 + parent.top,(float) (Math.cos(((Math.toRadians(360f) * getPersent(getH(i + 1)) / 100f) - ((Math.toRadians(360f) * getPersent(getN(i)) / 100f)/2f))) * parent.height / 4) + parent.width / 2, (float) ((Math.sin(((Math.toRadians(360f) * getPersent(getH(i + 1)) / 100f - ((Math.toRadians(360f) * getPersent(getN(i)) / 100f)/2f)))) * parent.height / 4)) + parent.height / 2 + parent.top, parent.text);
                canvas.drawText(String.valueOf(Math.round(getPersent(getN(i)))) + "%", (float) ((Math.cos(((Math.toRadians(360f) * getPersent(getH(i + 1)) / 100f) - ((Math.toRadians(360f) * getPersent(getN(i)) / 100f)/2f))) * parent.height / 8 * 3) + parent.width / 2 - r.width()/2f + dx), (float) (((Math.sin(((Math.toRadians(360f) * getPersent(getH(i + 1)) / 100f - ((Math.toRadians(360f) * getPersent(getN(i)) / 100f)/2f)))) * parent.height / 8 * 3)) + parent.height / 2 + r.height() / 2f + parent.top + dy), parent.text);
            }
        }

        drawinfo(canvas);
    }


    private void drawinfo(Canvas canvas) {
        if (istouch) {
            drawpopupmenu(touchx,touchy, selectpart, canvas);
        }
    }

    public void resetdate(){

    }

    private void drawpopupmenu(float x, float y, int step, Canvas canvas) {
        d = new Date(parent.points[0].points[step].x);

        popuppnt.setColor(MainActivity.bgc2);
        popuppnt.setAlpha((int) (255));
        popuppnt.setStyle(Paint.Style.FILL_AND_STROKE);
        Rect b = new Rect(0, 0, 0, 0);
        Rect h = new Rect(0, 0, 0, 0);
        popuppnt.getTextBounds("Gg",0,2,h);

        popuppnt.getTextBounds(parent.points[step].name + "AAA" + coolnum((int)(getN(step))),0,parent.points[step].name.length() + coolnum((int)(getN(step))).length() + 3,b);

        RectF r = new RectF(x, y, x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120f, parent.getResources().getDisplayMetrics()), y + h.height() + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, parent.getResources().getDisplayMetrics()));
        float inc;

        if (x + r.width() >parent.width + parent.left) {
            inc = r.width() + parent.left;
        } else inc = -parent.left;

        r.left -= inc;
        r.right -= inc;
        canvas.drawRoundRect(r, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, parent.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, parent.getResources().getDisplayMetrics()), popuppnt);

        popuppnt.setColor(MainActivity.textcolor);
        popuppnt.setAlpha((int)  (255));
         popuppnt.setColor(parent.points[step].pnt.getColor());
                popuppnt.setAlpha((int) (255));
                popuppnt.getTextBounds(coolnum(((int)(getN(step)))), 0, coolnum(((int)(getN(step)))).length(), b);
                canvas.drawText(coolnum(((int)(getN(step)))), x - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, parent.getResources().getDisplayMetrics()) - inc + r.width() - b.width() , y + h.height() + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, parent.getResources().getDisplayMetrics()), popuppnt);
                popuppnt.setColor(MainActivity.textcolor);
                popuppnt.setAlpha((int) (255));
                canvas.drawText(parent.points[step].name, x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, parent.getResources().getDisplayMetrics()) - inc , y + h.height() + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, parent.getResources().getDisplayMetrics()), popuppnt);
    }

    public String convertnum(int i) {
        String s = "";
        if (i / 1000000 > 0) {
            s = String.format("%.2f",i / 1000000f) + "M";
        } else if (i / 1000 > 0) {
            s = String.format("%.2f",i / 1000f) + "K";
        } else s = String.valueOf(i);
        return s;
    }

    public String coolnum(int i) {
        String s = String.valueOf(i);
        String ns = "";

        ns += s.charAt(s.length() - 1);
        for (int n = s.length() - 2; n >= 0; n--) {
            if ((s.length() - n - 1) % 3 == 0) ns += ".";
            ns += s.charAt(n);
        }

        s = "";
        for (int n = ns.length() - 1; n >= 0; n--) {
            s += ns.charAt(n);
        }

        return s;
    }

    public void init(){
        select = new boolean[parent.points.length];
        dis = new float[parent.points.length];
        newnums = new float[parent.points.length];
        oldnums = new float[parent.points.length];

        for(int i = 0; i < select.length; i++){
            select[i] = false;
            dis[i] = 0;
        }

    }
    public void setStart(int start) {
        startx = start;
    }
}
