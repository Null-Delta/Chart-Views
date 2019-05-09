package com.zed.MultiBarChart;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.zed.Charts.MainActivity;
import com.zed.Charts.R;
import com.zed.Others.ButtonsView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MultiBarChart extends View {

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
        int num;
        String name;
        PointInfo[] points;
        ValueAnimator alphaanim;

        public LineInfo(PointInfo[] pnts, int cl, int i) {
            pnt = new Paint();
            pnt.setStrokeWidth(linesize);
            pnt.setColor(cl);
            pnt.setStyle(Paint.Style.STROKE);
            points = pnts;
            mult = 1;
            num = i;
        }

        public void startanim() {
            alphaanim = new ValueAnimator();
            alphaanim.setDuration(250);
            if (isshow[num]) alphaanim.setFloatValues(0, 1);
            else alphaanim.setFloatValues(1, 0);
            alphaanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mult = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            alphaanim.start();
        }
    }

    protected LineInfo[] points;

    protected float[] alps;

    public MultiBarChartView parent;

    protected byte step_num;

    protected float width, height, right, left, top, bottom;

    protected float graph_h, newgraph_h,graph_b,newgraph_b;

    protected double startx,scale;

    protected int linesize,chartmode;

    public float distance,daydistance, linesz, lastx, lasty,mode,menux,menuy,menuw,menuh;

    public ButtonsView btns;

    protected int infox, stepvalue, oldsstepvalue, stepalpha, stepdate;

    protected boolean drawgrid, istouch, ismove = false, waslongtouch = false, ischacge = false;

    private float midwidth = 0;
    float[] dots;
    float[] linedots;
    float[] pointdots;

    boolean[] isshow = null;

    DateFormat df = new SimpleDateFormat("MMM dd", Locale.US), df2 = new SimpleDateFormat("MMMM dd yyyy", Locale.US),df3 = new SimpleDateFormat("HH:mm",Locale.US), df4 = new SimpleDateFormat("MMM dd HH:mm", Locale.US);
    Paint text = new Paint();
    Paint popuppnt = new Paint();
    Date d;

    ValueAnimator infoanim, resizeanim, gridanim, numanim,resizeanim2,changemodeanim;
    Paint infopaint = new Paint(), p = new Paint(), bg = new Paint();

    @SuppressLint("ClickableViewAccessibility")
    public MultiBarChart(Context context, final MultiBarChartView par) {
        super(context);
        step_num = 5;

        df2.setTimeZone(TimeZone.getTimeZone("UTC"));

        df3.setTimeZone(TimeZone.getTimeZone("UTC"));

        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        df4.setTimeZone(TimeZone.getTimeZone("UTC"));

        dots = new float[(step_num) * 4];

        linesize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());

        scale = 1;

        parent = par;

        startx = 0;

        drawgrid = true;

        setFocusable(true);

        text.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        text.setStyle(Paint.Style.STROKE);
        text.setAntiAlias(true);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setColor(MainActivity.textcolor);

        popuppnt.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        popuppnt.setAntiAlias(true);
        popuppnt.setTypeface(Typeface.DEFAULT_BOLD);

        stepdate = 1;

        infoanim = new ValueAnimator();
        infoanim.setDuration(250);
        infoanim.setFloatValues(0, 1f);
        infoanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linesz = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        resizeanim = new ValueAnimator();
        resizeanim.setDuration(250);
        resizeanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                graph_h = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        resizeanim2 = new ValueAnimator();
        resizeanim2.setDuration(250);
        resizeanim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                graph_b = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        gridanim = ValueAnimator.ofFloat(0, 1);
        gridanim.setDuration(250);
        gridanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                stepalpha = (int) (255 * (float) animation.getAnimatedValue());
            }
        });

        mode = 1;
        changemodeanim = ValueAnimator.ofFloat(1, 0);
        changemodeanim.setDuration(250);
        changemodeanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(chartmode == 1)
                mode = (float) animation.getAnimatedValue();
                else
                    mode = 1f - (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        numanim = ValueAnimator.ofFloat(0, 1);
        numanim.setDuration(150);
        numanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (int i = 0; i < points[0].points.length; i++) {
                    if ((points[0].points.length - 1 - i) % stepdate == 0 && alps[i] < 1) {
                        if (alps[i] < (float) animation.getAnimatedValue())
                            alps[i] = (float) animation.getAnimatedValue();
                    } else if ((points[0].points.length - 1 - i) % stepdate != 0 && alps[i] > 0) {
                        if (alps[i] > 1f - (float) animation.getAnimatedValue())
                            alps[i] = 1f - (float) animation.getAnimatedValue();
                    }
                    invalidate();
                }
            }
        });

        graph_b = 0;

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(event.getX() > menux && event.getX() < menuw && event.getY() > menuy && event.getY() < menuh && chartmode == 0 && istouch){
                            ischacge = false;
                        } else{
                                int dis = (int) (event.getX() - startx);
                                infox = (int) (dis / (distance / scale));
                                if (infox > points[0].points.length - 2)
                                    infox = points[0].points.length - 2;
                                if (infox < 0) infox = 0;
                                istouch = true;
                                infoanim.start();
                                lastx = event.getX();
                                lasty = event.getY();
                                ismove = false;
                                ischacge = true;
                                invalidate();
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:

                            if(ischacge) {
                                if ((Math.abs(lastx - event.getX()) > Math.abs(lasty - event.getY())) && !ismove) {
                                    ismove = true;
                                    v.getParent().requestDisallowInterceptTouchEvent(true);
                                }

                                if (ismove) {
                                        int dis = (int) (event.getX() - startx);
                                        infox = (int) (dis / (distance / scale));
                                        if (infox > points[0].points.length - 2)
                                            infox = points[0].points.length - 2;
                                        if (infox < 0) infox = 0;
                                        istouch = true;
                                } else {
                                    istouch = false;
                                }
                                lasty = event.getY();
                                lastx = event.getX();
                                invalidate();
                            }
                            return true;
                    case MotionEvent.ACTION_UP :
                                if(ischacge) {
                                        int dis = (int) (event.getX() - startx);
                                        infox = (int) (dis / (distance / scale));
                                        if (infox > points[0].points.length - 2)
                                            infox = points[0].points.length - 2;
                                        if (infox < 0) infox = 0;
                                        v.getParent().requestDisallowInterceptTouchEvent(false);
                                        ismove = false;
                                        istouch = true;
                                } else {
                                    if(event.getX() > menux && event.getX() < menuw && event.getY() > menuy && event.getY() < menuh) {
                                        chartmode = 1;
                                        //changemodeanim.start();
                                        parent.edge.setText(String.valueOf(df2.format(points[0].points[infox].x)));
                                        parent.title.setText("Zoom Out");
                                        parent.title.setTextSize(12);
                                        parent.title.setTextColor(MainActivity.zoomtext);
                                        istouch = false;
                                        parent.im.setVisibility(VISIBLE);
                                        parent.title.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                chartmode = 0;
                                                changemodeanim.start();
                                                parent.title.setText("Messages");
                                                parent.title.setTextSize(16);
                                                parent.title.setTextColor(MainActivity.textcolor);
                                                parent.im.setVisibility(GONE);
                                                try {
                                                    setData(new JSONObject(readText("chart_3/overview.json")));
                                                    parent.mg.setData(new JSONObject(readText("chart_3/overview.json")));
                                                    stepvalue = 1;
                                                    while ((stepdate * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length < midwidth * 2)
                                                        stepdate *= 2;
                                                    parent.edge.setText(String.valueOf(parent.df.format(points[0].points[getStartPoint()].x) + " - " + parent.df.format(points[0].points[getEndPoint() - 1].x)));
                                                    parent.mg.select = 1;
                                                    parent.mg.start = 0;
                                                    setStart(parent.mg.start);
                                                    setScale(parent.mg.select);
                                                    while ((stepdate * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length < midwidth * 2)
                                                        stepdate *= 2;
                                                    checknums();
                                                    update();
                                                    parent.mg.update();
                                                    df = new SimpleDateFormat("MMM dd", Locale.US);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                parent.title.setOnClickListener(null);
                                            }
                                        });

                                        try {
                                            String path = "chart_3/";
                                            df = new SimpleDateFormat("yyyy-MM");
                                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            path += df.format(points[0].points[infox].x) + "/";
                                            df = new SimpleDateFormat("d");
                                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            if(df.format(points[0].points[infox].x).length() == 1)  path += "0" + df.format(points[0].points[infox].x) + ".json";
                                            else path += df.format(points[0].points[infox].x) + ".json";
                                            df = new SimpleDateFormat("MMM dd", Locale.US);
                                            df.setTimeZone(TimeZone.getTimeZone("UTC"));

                                            setData(new JSONObject(readText(path)));
                                            parent.mg.setData(new JSONObject(readText(path)));
                                            parent.edge.setText(String.valueOf(parent.df.format(points[0].points[getStartPoint()].x) + " - " + parent.df.format(points[0].points[getEndPoint() - 1].x)));
                                            parent.mg.select = 1;
                                            parent.mg.start = 0;
                                            setStart(0f);
                                            setScale(1f);
                                            while ((stepdate * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length < midwidth * 2)
                                                stepdate *= 2;
                                            checknums();
                                            update();
                                            parent.mg.update();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                                return true;
                }
                return false;
            }
        });
    }
    private String readText(String name) throws IOException {
        InputStream is = parent.parent.getAssets().open(name);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = br.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    public float getH(int i, int k){
            float h = 0;
            for (int n = 0; n <= i; n++) {
                h += points[n].points[k].y * points[n].mult;
            }
            return h;
    }

    public float getHT(int i, int k){
        float h = 0;
        for (int n = 0; n <= i; n++) {
            if (isshow[n]) h += points[n].points[k].y;
        }
        return h;
    }

    protected void setTheme() {
        this.setBackgroundColor(MainActivity.bgc);
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

        distance = width / (float) (points[0].points.length - 1);
        daydistance = width / 24;

        while ((stepdate * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length < midwidth * 2)
            stepdate *= 2;

        checknums();
        update();
    }

    public void update() {
            if (getMax() > newgraph_h || getMax() < newgraph_h - stepvalue) {
                float mx = getMax();
                newgraph_h = mx;
                if (newgraph_h == 0) newgraph_h = step_num;
            }

            if ((newgraph_h) != graph_h) {
                oldsstepvalue = stepvalue;
                stepvalue = (int) ((newgraph_h) / step_num);
                ///stepalpha = 0;

                resizeanim.setFloatValues(graph_h, newgraph_h - newgraph_b);
                resizeanim.start();


                gridanim.start();
            }
    }

    public void checknums() {
        if (midwidth > 0) {
            if ((stepdate * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length < midwidth) {
                stepdate *= 2;
                numanim.start();
            } else if ((stepdate * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length > midwidth * 2) {
                stepdate /= 2;
                numanim.start();
            }
        }
    }

    private void drawtextgrid(Canvas canvas){
        text.setColor(MainActivity.gridcolor);
        text.setAlpha((int)(255/2));
        text.setStyle(Paint.Style.FILL_AND_STROKE);
        text.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        boolean isshow = false;

        for (int i = 0; i < step_num; i++) {
            for (int n = 0; n < step_num; n++) {
                if (oldsstepvalue * i == stepvalue * n) {
                    isshow = true;
                    break;
                }
            }
            if (isshow) {
                text.setAlpha(0);
                isshow = false;
            } else {
                text.setAlpha(((255/2- stepalpha/2)));
            }
            Rect r = new Rect(0,0,0,0);
            text.getTextBounds(convertnum(stepvalue * i + (int)(newgraph_b)),0,convertnum(stepvalue * i + (int)(newgraph_b)).length(),r);
            canvas.drawText(convertnum(oldsstepvalue * i + (int)(newgraph_b)), left, top + (height / graph_h) * reverse(oldsstepvalue * i + newgraph_b) - 10, text);
        }

        text.setAlpha((stepalpha/2));
        for (int i = 0; i < step_num; i++) {
            Rect r = new Rect(0,0,0,0);
            text.getTextBounds(convertnum(stepvalue * i + (int)(newgraph_b)),0,convertnum(stepvalue * i + (int)(newgraph_b)).length(),r);
            canvas.drawText(convertnum(stepvalue * i + (int)(newgraph_b)), left, top + (height / (graph_h)) * reverse(stepvalue * i + newgraph_b) - 10, text);
        }
    }

    private void drawgrid(Canvas canvas) {
            p.setStrokeWidth((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    1f,
                    getResources().getDisplayMetrics()
            ));
            p.setStyle(Paint.Style.STROKE);
            p.setAntiAlias(true);
            p.setColor(MainActivity.gridcolor);
            text.setColor(MainActivity.gridtextcolor);

            boolean isshow = false;
            if ((255 - stepalpha) > 0) {
                for (int i = 0; i < step_num; i++) {
                    for (int n = 0; n < step_num; n++) {
                        if (oldsstepvalue * i == stepvalue * n) {
                            isshow = true;
                            break;
                        }
                    }
                    if (isshow) {
                        p.setAlpha((int) (255/10));
                        isshow = false;
                    } else {
                        p.setAlpha((int) (255/10 - stepalpha/10));
                    }

                    dots[i * 4] = left;
                    dots[i * 4 + 1] = top + (height / (graph_h)) * reverse(oldsstepvalue * i + graph_b);
                    dots[i * 4 + 2] = left + width;
                    dots[i * 4 + 3] = top + (height / (graph_h)) * reverse(oldsstepvalue * i + graph_b);
                }
                canvas.drawLines(dots, p);
            }

            p.setAlpha((int) (stepalpha/10));
            for (int i = 0; i < step_num; i++) {
                dots[i * 4] = left;
                dots[i * 4 + 1] = top + (height / (graph_h)) * (reverse(stepvalue * i + graph_b));
                dots[i * 4 + 2] = left + width;
                dots[i * 4 + 3] = top + (height / (graph_h)) * (reverse(stepvalue * i + graph_b));
            }
            canvas.drawLines(dots, p);

            if(chartmode == 0) {
                for (int i = 0; i < points[0].points.length; i++) {
                    d = new Date(points[0].points[i].x);
                    text.setAlpha((int) (alps[i] * 255f/2));
                    if (text.getAlpha() != 0) {
                        canvas.drawText(df.format(d), (float)(left + startx +
                                        ((i * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length))
                                , bottom + top + top, text);
                    }

                }
            } else {
                for (int i = 0; i < points[0].points.length; i++) {
                    d = new Date(points[0].points[i].x);
                    text.setAlpha((int) (alps[i] * 255f/2));
                    if (text.getAlpha() != 0) {
                        canvas.drawText(df3.format(d), (float)(left + startx +
                                        ((i * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length))
                                , bottom + top + top, text);
                    }

                }
            }
    }

    private float reverse(float x) {
        return (graph_h - (x - graph_b));
    }

    public void drawallinfo(Canvas canvas){
                for (int n = 0; n < points.length; n++) {
                    for (int i = 0; i < points[n].points.length - 1; i++) {
                        linedots[i * 4] = (float)(left + startx + ((distance / scale) * i) + (distance / scale / 2));
                        linedots[i * 4 + 1] = top + (height / (graph_h)) * reverse(getH(n, i));
                        linedots[i * 4 + 2] = (float)(left + startx + ((distance / scale) * (i)) + (distance / scale / 2));
                        linedots[i * 4 + 3] = top + (height / (graph_h)) * reverse(getH(n - 1, i));
                    }
                    points[n].pnt.setStrokeWidth((float)(distance / scale));
                    if (istouch)
                        points[n].pnt.setAlpha((int) (255/2 * mode));
                    else
                        points[n].pnt.setAlpha((int) (255 * mode));

                    canvas.drawLines(linedots, points[n].pnt);
                    if (istouch) {
                        points[n].pnt.setAlpha((int) (255 * mode));
                        canvas.drawLine((float)(left + startx + ((distance / scale) * infox) + (distance / scale / 2)), top + (height / (graph_h)) * reverse(getH(n, infox)), (float)(left + startx + ((distance / scale) * (infox)) + (distance / scale / 2)), top + (height / (graph_h)) * reverse(getH(n - 1, infox)), points[n].pnt);
                    }
                }

                if (drawgrid) drawgrid(canvas);
        if (drawgrid) drawtextgrid(canvas);
            drawinfo(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(MainActivity.bgc);
        drawallinfo(canvas);
    }

    private void drawinfo(Canvas canvas) {
        if (istouch) {

            if (infox < 0) infox = 0;
            if (infox > points[0].points.length - 1) infox = points[0].points.length - 1;

            drawpopupmenu((float)(left + startx + ((distance / scale) * infox)), top, infox, canvas);
        }
    }

    private void drawpopupmenu(float x, float y, int step, Canvas canvas) {
        d = new Date(points[0].points[step].x);

        popuppnt.setColor(MainActivity.bgc2);
        popuppnt.setAlpha((int) (linesz * 255 * mode));
        popuppnt.setStyle(Paint.Style.FILL_AND_STROKE);
        Rect b = new Rect(0, 0, 0, 0);
        Rect h = new Rect(0, 0, 0, 0);
        popuppnt.getTextBounds("Gg",0,2,h);
        int max = 0, a = 0;
        for (int i = 0; i < points.length; i++) {
            if (coolnum( points[i].points[step].y).length() + points[i].name.length() > coolnum( points[max].points[step].y).length() + points[max].name.length() ) max = i;
            if (isshow[i]) a++;
        }

        if (coolnum( points[max].points[step].y).length() + points[max].name.length() + 3 >= 11) {
            popuppnt.getTextBounds(coolnum(points[max].points[step].y) + points[max].name + "BBB", 0, coolnum(points[max].points[step].y).length() + points[max].name.length() + 3, b);
        } else {
            popuppnt.getTextBounds("WWW 00 0000", 0, 11, b);
        }

        RectF r = new RectF(x, y, x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120f, getResources().getDisplayMetrics()), y + h.height() * (a + 1) * 2 + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, getResources().getDisplayMetrics()));

        float inc;

        if (x + r.width() > (float)(width - (distance / scale))) {
            inc = (float)(r.width() - (distance / scale));
        } else inc = (float)(-(distance / scale) * 2);

        r.left -= inc;
        r.right -= inc;

        menux = r.left;
        menuy = r.top;
        menuh = r.bottom;
        menuw = r.right;
        canvas.drawRoundRect(r, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, getResources().getDisplayMetrics()), popuppnt);

        popuppnt.setColor(MainActivity.textcolor);
        popuppnt.setAlpha((int) (linesz * 255 * mode));

        if(chartmode == 0)
        canvas.drawText(df.format(d), x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc, y + h.height() + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()), popuppnt);
        else         canvas.drawText(df4.format(d), x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc, y + h.height() + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()), popuppnt);

        int ii = 0;
        for (int i = 0; i < points.length; i++) {
            if (isshow[i]) {
                popuppnt.setColor(points[i].pnt.getColor());
                popuppnt.setAlpha((int) (linesz * 255 * mode));
                popuppnt.getTextBounds(coolnum((points[i].points[step].y)), 0, coolnum((points[i].points[step].y)).length(), b);
                canvas.drawText(coolnum((points[i].points[step].y)), x - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc + r.width() - b.width() , y + h.height() + (h.height() * (ii + 1) * 2 ) + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()), popuppnt);
                popuppnt.setColor(MainActivity.textcolor);
                popuppnt.setAlpha((int) (linesz * 255 * mode));
                canvas.drawText(points[i].name, x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc , y + h.height() + (h.height() * (ii + 1) * 2) + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()), popuppnt);
                ii++;
            }
        }

        canvas.drawText("All", x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc , y + h.height() + (h.height() * (ii + 1) * 2) + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()), popuppnt);

        popuppnt.getTextBounds(coolnum((int)getH(points.length - 1,infox)), 0,coolnum((int)getH(points.length - 1,infox)).length(), b);
        canvas.drawText(coolnum((int)getH(points.length - 1,infox)), x - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc + r.width() - b.width() , y + h.height() + (h.height() * (ii + 1) * 2 ) + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()), popuppnt);

        if(chartmode == 0) {
            Drawable d = getResources().getDrawable(R.drawable.infoicon);
            d.setBounds((int) (x - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc + r.width()) - h.height() * 2, (int)(y + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics())) - h.height(), (int)(x - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc + r.width() + h.height() * 2 - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics())),(int)(y + h.height() * 2) + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()));
            d.draw(canvas);
        }

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

    public int getStartPoint() {
        if ((-((startx + left) / (distance / scale))) < 0) return 0;
        else
            return (int) ((-((startx + left) / (distance / scale))));
    }

    public int getdayStartPoint() {
        if ((-((startx + left) / (daydistance / scale))) < 0) return 0;
        else
            return (int) ((-((startx + left) / (daydistance / scale))));
    }

    public int getEndPoint() {
        if ((-((startx - width - left) / (distance / scale))) + 1 > points[0].points.length - 1)
            return points[0].points.length - 1;
        else return (int) (-((startx - width - left) / (distance / scale))) + 1;
    }

    public int getLinesCount() {
        return points.length;
    }

    public Paint getPaint(int index) {
        return points[index].pnt;
    }

    protected float getMax() {
            float max = 0;
            for (int n = 0; n < points.length; n++) {
                for (int i = getStartPoint(); i < getEndPoint(); i++) {
                    if (isshow[n] && getHT(n, i) > max) max = getHT(n, i);
                }
            }
            return max;
    }

    public LineInfo getLine(int index) {
        return points[index];
    }

    public void setScale(double m) {
        scale = m;
    }

    public void setStart(double s) {
        startx = -s * ((distance / scale) * (points[0].points.length - 1));
    }

    public void setData(JSONObject json) throws JSONException {
        PointInfo[] pi;

        points = new LineInfo[json.getJSONArray("columns").length() - 1];

        for (int n = 1; n < json.getJSONArray("columns").length(); n++) {
            pi = new PointInfo[json.getJSONArray("columns").getJSONArray(n).length()];
            for (int i = 0; i < json.getJSONArray("columns").getJSONArray(n).length() - 1; i++) {
                pi[i] = new PointInfo(json.getJSONArray("columns").getJSONArray(0).getLong(i + 1), json.getJSONArray("columns").getJSONArray(n).getInt(i + 1));
            }
            points[n - 1] = new LineInfo(pi, Color.parseColor(json.getJSONObject("colors").getString(json.getJSONArray("columns").getJSONArray(n).getString(0))),n - 1);
            points[n - 1].name = json.getJSONObject("names").getString("y" + (n - 1) );
        }
        points[0].points[points[0].points.length - 1] = points[0].points[0];

        alps = new float[points[0].points.length];
        for (int i = 0; i < alps.length; i++) {
            alps[i] = 0;
        }
        linedots = new float[(points[0].points.length - 1) * 4];
        pointdots = new float[(points[0].points.length - 1) * 2];

        Rect r = new Rect(0, 0, 0, 0);

        if(chartmode == 0) {
            for (int i = 0; i < points[0].points.length; i++) {
                text.getTextBounds(df.format(new Date(points[0].points[i].x)), 0, df.format(new Date(points[0].points[i].x)).length(), r);
                midwidth += r.width();
            }
            midwidth = midwidth / points[0].points.length + 20;
        } else if(chartmode == 1) {
            for (int i = 0; i < points[0].points.length; i++) {
                text.getTextBounds(df3.format(new Date(points[0].points[i].x)), 0, df3.format(new Date(points[0].points[i].x)).length(), r);
                midwidth += r.width();
            }
            midwidth = midwidth / points[0].points.length + 20;
        }
        //update();
        distance = width / (float) (points[0].points.length - 1);
        if (isshow != null) {
            for(int n = 0; n < points.length; n++){
                if(isshow[n])points[n].mult = 1;
                else points[n].mult = 0;
            }
        }
    }

    public void setLineVisible(int index, boolean visible) {
        isshow[index] = visible;
        points[index].startanim();
    }

    public void setStep(byte step) {
        step_num = step;
    }

    public void setStart(int start) {
        startx = start;
    }
}
