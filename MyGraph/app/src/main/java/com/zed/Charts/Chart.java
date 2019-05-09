package com.zed.Charts;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Chart extends View {

    protected class PointInfo {
        long x;
        int y;

        PointInfo(long xx, int yy) {
            x = xx;
            y = yy;
        }
    }

    protected class LineInfo {
        Paint pnt, pnt2;
        boolean isshow;
        PointInfo[] points;
        ValueAnimator alphaanim;

        public LineInfo(PointInfo[] pnts, int cl) {
            isshow = true;
            pnt = new Paint();
            pnt.setStrokeWidth(linesize);
            pnt.setColor(cl);
            pnt.setStyle(Paint.Style.STROKE);
            pnt.setAntiAlias(true);
            points = pnts;
            pnt2 = new Paint(pnt);
            pnt2.setAntiAlias(true);
            pnt2.setStrokeCap(Paint.Cap.ROUND);
        }

        public void startanim() {
            alphaanim = new ValueAnimator();
            alphaanim.setDuration(250);
            if (isshow) alphaanim.setIntValues(pnt.getAlpha(), 255);
            else alphaanim.setIntValues(pnt.getAlpha(), 0);
            alphaanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pnt.setAlpha((int) animation.getAnimatedValue());
                    pnt2.setAlpha((int) animation.getAnimatedValue());
                    invalidate();
                }
            });
            alphaanim.start();
        }
    }

    protected LineInfo[] points;

    protected float[] alps;

    protected byte step_num;

    protected float width, height, right, left, top, bottom;

    protected float graph_h, newgraph_h;

    protected float startx;

    protected int linesize;

    private float distance, scale, inforad, linesz, lastx, lasty;

    protected int infox, stepvalue, oldsstepvalue, stepalpha, stepdate;

    protected boolean drawgrid, istouch, ismove = false, waslongtouch = false;

    protected int bgcolor, linecolor, textcolor;

    private float midwidth = 0;
    float[] dots;
    float[] linedots;
    float[] pointdots;

    DateFormat df = new SimpleDateFormat("MMM dd", Locale.US), df2 = new SimpleDateFormat("MMM dd yyyy", Locale.US);
    Paint text = new Paint();
    Paint popuppnt = new Paint();
    Date d;

    ValueAnimator infoanim, resizeanim, gridanim, numanim;
    Paint infopaint = new Paint(), p = new Paint(), bg = new Paint();

    @SuppressLint("ClickableViewAccessibility")
    public Chart(Context context) {
        super(context);
        step_num = 5;

        dots = new float[(step_num) * 4];

        linesize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());

        scale = 1;

        startx = 0;

        drawgrid = true;

        setFocusable(true);

        text.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        text.setStyle(Paint.Style.STROKE);
        text.setAntiAlias(true);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setColor(textcolor);

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
                inforad = (float) animation.getAnimatedValue() * (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
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

        gridanim = ValueAnimator.ofFloat(0, 1);
        gridanim.setDuration(250);
        gridanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                stepalpha = (int) (255 * (float) animation.getAnimatedValue());
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

        setFocusable(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                istouch = hasFocus;
                invalidate();
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    int dis = (int) (event.getX() - startx);
                    infox = (int) (dis / (distance / scale));
                    if (infox > points[0].points.length - 1) infox = points[0].points.length - 1;
                    istouch = true;
                    infoanim.start();
                    lastx = event.getX();
                    lasty = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    if((Math.abs(lastx - event.getX()) > Math.abs(lasty - event.getY())) && !ismove) {
                        ismove = true;
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }

                    if (ismove) {
                        int dis = (int) (event.getX() - startx);
                        infox = (int) (dis / (distance / scale));
                        if (infox > points[0].points.length - 1)
                            infox = points[0].points.length - 1;
                        if (infox < 0) infox = 0;
                        istouch = true;
                    } else {
                        istouch = false;
                    }
                    lasty = event.getY();
                    lastx = event.getX();
                    invalidate();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int dis = (int) (event.getX() - startx);
                    infox = (int) (dis / (distance / scale));
                    if (infox > points[0].points.length - 1) infox = points[0].points.length - 1;
                    istouch = true;
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    ismove = false;
                }
                return true;
            }
        });
    }

    protected void setTheme(int bgc, int lnc, int txtc) {
        bgcolor = bgc;
        linecolor = lnc;
        textcolor = txtc;
        this.setBackgroundColor(bgcolor);
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

        while ((stepdate * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length < midwidth * 2)
            stepdate *= 2;
        checknums();
        update();
    }

    public void update() {
        float mx = getMax();
        if (mx % (stepvalue) > 0) mx += (stepvalue - mx % (stepvalue));
        newgraph_h = (int) (mx / ((int) (mx / stepvalue)));
        newgraph_h *= (int) (mx / (stepvalue));
        if (newgraph_h == 0) newgraph_h = step_num;

        if (newgraph_h != graph_h) {
            oldsstepvalue = stepvalue;
            stepvalue = (int) (newgraph_h / step_num);
            stepalpha = 0;

            resizeanim.setFloatValues(graph_h, newgraph_h);
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
        p.setStrokeWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1f,
                getResources().getDisplayMetrics()
        ));
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);
        p.setColor(linecolor);
        text.setColor(textcolor);

        bg.setStyle(Paint.Style.FILL);
        bg.setColor(bgcolor);

        bg.setAlpha(200 - (int)(stepalpha * (200f / 255f)));
        boolean isshow = false;

        for (int i = 0; i < step_num; i++) {
            for (int n = 0; n < step_num; n++) {
                if (oldsstepvalue * i == stepvalue * n) {
                    isshow = true;
                    break;
                }
            }
            if (isshow) {
                p.setAlpha(255);
                text.setAlpha(255);
                isshow = false;
            } else {
                p.setAlpha(255 - stepalpha);
                text.setAlpha(255 - stepalpha);
            }
            Rect r = new Rect(0,0,0,0);
            text.getTextBounds(convertnum(stepvalue * i),0,convertnum(stepvalue * i).length(),r);
            canvas.drawRoundRect(new RectF(left - (left / 2),top + (height / graph_h) * reverse(oldsstepvalue * i) - 10 + (left / 2), left + r.width() + (left / 2),top + (height / graph_h) * reverse(oldsstepvalue * i) - 10 - r.height() - (left / 2)),10,10,bg);
            canvas.drawText(convertnum(oldsstepvalue * i), left, top + (height / graph_h) * reverse(oldsstepvalue * i) - 10, text);
        }

        p.setAlpha(stepalpha);
        text.setAlpha(stepalpha);
        bg.setAlpha((int)(stepalpha * (200f / 255f)));
        for (int i = 0; i < step_num; i++) {
            Rect r = new Rect(0,0,0,0);
            text.getTextBounds(convertnum(stepvalue * i),0,convertnum(stepvalue * i).length(),r);
            canvas.drawRoundRect(new RectF(left - (left / 2),top + (height / graph_h) * reverse(stepvalue * i) - 10 + (left / 2), left + r.width() + (left / 2),top + (height / graph_h) * reverse(stepvalue * i) - 10 - r.height() - (left / 2)),10,10,bg);
            canvas.drawText(convertnum(stepvalue * i), left, top + (height / graph_h) * reverse(stepvalue * i) - 10, text);
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
        p.setColor(linecolor);
        text.setColor(textcolor);

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
                    p.setAlpha(255);
                    text.setAlpha(255);
                    isshow = false;
                } else {
                    p.setAlpha(255 - stepalpha);
                    text.setAlpha(255 - stepalpha);
                }

                dots[i * 4] = left;
                dots[i * 4 + 1] = top + (height / graph_h) * reverse(oldsstepvalue * i);
                dots[i * 4 + 2] = left + width;
                dots[i * 4 + 3] = top + (height / graph_h) * reverse(oldsstepvalue * i);
            }
            canvas.drawLines(dots, p);
        }

        p.setAlpha(stepalpha);
        text.setAlpha(stepalpha);
        for (int i = 0; i < step_num; i++) {
            dots[i * 4] = left;
            dots[i * 4 + 1] = top + (height / graph_h) * reverse(stepvalue * i);
            dots[i * 4 + 2] = left + width;
            dots[i * 4 + 3] = top + (height / graph_h) * reverse(stepvalue * i);
            canvas.drawText(convertnum(stepvalue * i), left, top + (height / graph_h) * reverse(stepvalue * i) - 10, text);
        }
        canvas.drawLines(dots, p);

        for (int i = 0; i < points[0].points.length; i++) {
            d = new Date(points[0].points[i].x);
            text.setAlpha((int) (alps[i] * 255f));
            if (text.getAlpha() != 0) {
                canvas.drawText(df.format(d), left + startx +
                                ((i * ((distance / scale) * (points[0].points.length) - midwidth)) / points[0].points.length)
                        , bottom + top + top, text);
            }

        }
    }

    private float reverse(float x) {
        return graph_h - x;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bgcolor);
        if (drawgrid) drawgrid(canvas);

        drawinfoline(canvas);
        for (int n = 0; n < points.length; n++) {
            if (points[n].pnt.getAlpha() != 0) {
                for (int i = 0; i < points[n].points.length - 1; i++) {
                    pointdots[i * 2] = left + startx + ((distance / scale) * i);
                    pointdots[i * 2 + 1] = top + (height / graph_h) * reverse(points[n].points[i].y);
                    linedots[i * 4] = left + startx + ((distance / scale) * i);
                    linedots[i * 4 + 1] = top + (height / graph_h) * reverse(points[n].points[i].y);
                    linedots[i * 4 + 2] = left + startx + ((distance / scale) * (i + 1));
                    linedots[i * 4 + 3] = top + (height / graph_h) * reverse(points[n].points[i + 1].y);
                }
                canvas.drawLines(linedots, getStartPoint() * 4, (getEndPoint() - getStartPoint() - 1) * 4, points[n].pnt);
                canvas.drawPoints(pointdots, getStartPoint() * 2, (getEndPoint() - getStartPoint() - 1) * 2, points[n].pnt2);
            }
        }

        if(drawgrid) drawtextgrid(canvas);
        drawinfo(canvas);
    }

    private void drawinfoline(Canvas canvas) {
        if (istouch) {
            infopaint.setStrokeWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics()));
            infopaint.setStyle(Paint.Style.STROKE);
            infopaint.setColor(linecolor);
            infopaint.setAntiAlias(true);
            canvas.drawLine(left + startx + ((distance / scale) * infox), bottom, left + startx + ((distance / scale) * infox), bottom - ((height + top) * linesz), infopaint);
        }
    }

    private void drawinfo(Canvas canvas) {
        if (istouch) {
            infopaint.setStrokeWidth(linesize);

            if (infox < 0) infox = 0;
            if (infox > points[0].points.length - 1) infox = points[0].points.length - 1;

            for (int i = 0; i < points.length; i++) {
                infopaint.setColor(bgcolor);
                infopaint.setAlpha(points[i].pnt.getAlpha());
                infopaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(left + startx + ((distance / scale) * infox), top + (height / graph_h) * reverse(points[i].points[infox].y), inforad, infopaint);
                infopaint.setColor(points[i].pnt.getColor());
                infopaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(left + startx + ((distance / scale) * infox), top + (height / graph_h) * reverse(points[i].points[infox].y), inforad, infopaint);
            }
            drawpopupmenu(left + startx + ((distance / scale) * infox), top, infox, canvas);
        }
    }

    private void drawpopupmenu(float x, float y, int step, Canvas canvas) {
        d = new Date(points[0].points[step].x);

        popuppnt.setColor(linecolor);
        popuppnt.setAlpha((int) (linesz * 255));
        popuppnt.setStyle(Paint.Style.FILL_AND_STROKE);
        Rect b = new Rect(0, 0, 0, 0);
        int max = 0, a = 0;
        for (int i = 0; i < points.length; i++) {
            if (points[i].points[step].y > max) max = points[i].points[step].y;
            if (points[i].isshow) a++;
        }

        if (coolnum(max).length() > 11)
            popuppnt.getTextBounds(coolnum(max), 0, coolnum(max).length(), b);
        else
            popuppnt.getTextBounds("WWW 00 0000", 0, 11, b);

        RectF r = new RectF(x, y, x + b.width() + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()), y + b.height() * 2 * a + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, getResources().getDisplayMetrics()));
        float inc;

        if (x + r.width() > width + left) {
            inc = r.width() + left;
        } else inc = -left;

        r.left -= inc;
        r.right -= inc;
        canvas.drawRoundRect(r, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, getResources().getDisplayMetrics()), popuppnt);

        popuppnt.setColor(textcolor);
        popuppnt.setAlpha((int) (linesz * 255));

        canvas.drawText(df2.format(d), x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc, y + b.height() + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()), popuppnt);
        int ii = 0;
        for (int i = 0; i < points.length; i++) {
            if (points[i].isshow) {
                popuppnt.setColor(points[i].pnt.getColor());
                popuppnt.setAlpha((int) (linesz * 255));
                canvas.drawText(coolnum((points[i].points[step].y)), x + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics()) - inc, y + (b.height() * (ii + 1) * 2) + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, getResources().getDisplayMetrics()), popuppnt);
                ii++;
            }
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

    private int getStartPoint() {
        if ((-((startx + left) / (distance / scale))) < 0) return 0;
        else
            return (int) ((-((startx + left) / (distance / scale))));
    }

    private int getEndPoint() {
        if ((-((startx - width - left - left - left) / (distance / scale))) + 1 > points[0].points.length)
            return points[0].points.length;
        else return (int) (-((startx - width - left - left - left) / (distance / scale))) + 1;
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
                if (points[n].isshow && points[n].points[i].y > max) max = points[n].points[i].y;
            }
        }
        return max;
    }

    public LineInfo getLine(int index) {
        return points[index];
    }

    public void setScale(float m) {
        scale = m;
    }

    public void setStart(float s) {
        startx = -s * ((distance / scale) * (getLine(0).points.length - 1));
    }

    public void setData(JSONObject json) throws JSONException {
        PointInfo[] pi;

        points = new LineInfo[json.getJSONArray("columns").length() - 1];

        for (int n = 1; n < json.getJSONArray("columns").length(); n++) {
            pi = new PointInfo[json.getJSONArray("columns").getJSONArray(n).length() - 1];
            for (int i = 0; i < json.getJSONArray("columns").getJSONArray(n).length() - 1; i++) {
                pi[i] = new PointInfo(json.getJSONArray("columns").getJSONArray(0).getLong(i + 1), json.getJSONArray("columns").getJSONArray(n).getInt(i + 1));
            }
            points[n - 1] = new LineInfo(pi, Color.parseColor(json.getJSONObject("colors").getString(json.getJSONArray("columns").getJSONArray(n).getString(0))));
        }

        alps = new float[points[0].points.length];
        for (int i = 0; i < alps.length; i++) {
            alps[i] = 0;
        }
        linedots = new float[(points[0].points.length - 1) * 4];
        pointdots = new float[(points[0].points.length - 1) * 2];

        Rect r = new Rect(0, 0, 0, 0);

        for (int i = 0; i < points[0].points.length; i++) {
            text.getTextBounds(df.format(new Date(points[0].points[i].x)), 0, df.format(new Date(points[0].points[i].x)).length(), r);
            midwidth += r.width();
        }
        midwidth = midwidth / points[0].points.length + 20;

        update();
    }

    public void setLineVisible(int index, boolean visible) {
        points[index].isshow = visible;
        points[index].startanim();
    }

    public void setStep(byte step) {
        step_num = step;
    }

    public void setStart(int start) {
        startx = start;
    }
}
