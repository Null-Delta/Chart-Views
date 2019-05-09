package com.zed.PersentChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zed.BarChart.BarChartView;
import com.zed.Charts.MainActivity;
import com.zed.Charts.OnMiniGraphChanges;
import com.zed.MultiBarChart.MultiBarChartView;

import java.util.Date;

public class PersentMiniChart extends PersentChart {

    public float start, select, lastx,lasty, duration,duration2,left2,width2,right2,top2,bottom2,height2;
    private boolean ismove, resizeleft, resizeright;
    private OnMiniGraphChanges listener;
    private Paint edge = new Paint();
    private Path round,round2;

    public PersentMiniChart(Context context, PersentChartView par) {
        super(context,par);

        drawgrid = false;

        start = 0;

        round = new Path();
        round2 = new Path();

        select = 1f;

        setScale(1);

        ismove = false;

        linesize = 3;

        listener = null;

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: lastx = event.getX();
                        lasty = event.getY();
                        duration = lastx - start * width2;
                        duration2 = lastx - (start + select) * width2;
                        if (event.getX() > left2 + start * width2 && event.getX() < (start + select) * width2) {
                            ismove = true;
                        } else if (event.getX() < left2 + start * width2) {
                            resizeleft = true;
                        } else if (event.getX() > (start + select) * width2) {
                            resizeright = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(Math.abs(lastx - event.getX()) > Math.abs(lasty - event.getY())){
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        if (ismove) {
                            start = (event.getX() / width2) - (duration / width2);
                            if (start < 0) start = 0;
                            if (start + select > 1) start = 1 - select;
                        }
                        if (resizeleft) {
                            float latst = start;
                            start = (event.getX() / width2) - (duration / width2);
                            if (start >= 0) {
                                select -= start - latst;
                            } else {
                                start = 0;
                                select -= start - latst;
                            }

                            if (select < 0.1f) {
                                start -= 0.1f - select;
                                select = 0.1f;
                            }
                        }
                        if (resizeright) {
                            select = (event.getX() / width2) - (duration2 / width2) - start;

                            if (select < 0.1f) {
                                select = 0.1f;
                            }
                            if (start + select > 1) select = 1 - start;
                        }
                        listener.Check(0);
                        lasty = event.getY();
                        lastx = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        ismove = false;
                        resizeleft = false;
                        resizeright = false;
                        listener.Check(1);
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                invalidate();
                return true;
            }
        });
    }

    public float getStart() {
        return start;
    }

    public float getSelect() {
        return select;
    }

    public void setOnMiniGraphChange(OnMiniGraphChanges l) {
        listener = l;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w; //- this.getPaddingLeft() - this.getPaddingRight();

        width2 = w- this.getPaddingLeft() - this.getPaddingRight();
        height = h;// - this.getPaddingTop() - this.getPaddingBottom();

        height2 = h - this.getPaddingTop() - this.getPaddingBottom();

        left2 = this.getPaddingLeft();
        top =0;// this.getPaddingTop();

        top2 =this.getPaddingTop();

        left = 0;
        right = width + left;

        right2 = width2 + left2;
        bottom = height + top;
        bottom2 = height2 + top2;

        distance = width / (float) (points[0].points.length - 1);

        round.moveTo(0,0);
        round.lineTo(left2,0);
        round.lineTo(0,left2);
        round.close();

        round.moveTo(0,bottom - left2);
        round.lineTo(left2,bottom);
        round.lineTo(0,bottom);
        round.close();

        round.moveTo(right - left2,0);
        round.lineTo(right,0);
        round.lineTo(right,left2);
        round.close();

        round.moveTo(right,bottom - left2);
        round.lineTo(right,bottom);
        round.lineTo(right - left2,bottom);
        round.close();

        round2.addArc(0,0,left2 * 2,left2 * 2, 180,90);
        round2.addArc(0,bottom - left2 * 2,left2 * 2,bottom,90,90);
        round2.addArc(width - left2 * 2,0,width,left2 * 2,-90,90);
        round2.addArc(width - left2 * 2,bottom - left2 * 2,width,bottom, 0,90);
        round.op(round2, Path.Op.XOR);
        //round.addRect(0,0,left2,left2, Path.Direction.CW);
    }

    private void drawedge(Canvas canvas) {
        edge.setStyle(Paint.Style.FILL_AND_STROKE);

        edge.setColor(MainActivity.barback);
        edge.setAlpha((int)(255 * 0.6));

        canvas.drawRect(new RectF(left2, 0, (int) (width2 * start) + left2, top2 + bottom2), edge);
        canvas.drawRect( new RectF((int) ((start + select) * width2) + left2, 0, right2, top2 + bottom2), edge);

        canvas.drawArc(new RectF(0, 0, left2 * 2, left2 * 2), -90,-90, true, edge);
        canvas.drawArc(new RectF( 0, bottom2 - left2 * 2 + top2, left2 * 2, bottom2 + top2), 90,90, true, edge);

        canvas.drawArc(new RectF( right2 - left2, 0, right2 + left2, left2 * 2), -90,90, true, edge);
        canvas.drawArc(new RectF(right2 - left2, bottom2 - left2 * 2 + top2, right2 + left2, bottom2 + top2), 90,-90, true, edge);

        canvas.drawRect(0, left2 , left2, bottom2 - left2 + top2 , edge);
        canvas.drawRect(right2, left2, right2 + left2,bottom2 - left2 + top2, edge);
        canvas.drawRect(left2 + (int) (width2 * start), 0, left2 + (int) ((start + select) * width2), top2, edge);
        canvas.drawRect(left2 + (int) (width2 * start), bottom2, left2 + (int) ((start + select) * width2), top2 + bottom2, edge);


        edge.setColor(MainActivity.barselect);
        edge.setAlpha(255/2);
        canvas.drawRect(left2 + (int) (width2 * start), 0, left2 + (int) ((start + select) * width2), top2, edge);
        canvas.drawRect(left2 + (int) (width2 * start), bottom2, left2 + (int) ((start + select) * width2), top2 + bottom2, edge);

        canvas.drawArc(new RectF((int) (width2 * start), 0, (int) (width2 * start) + left2 * 2, left2 * 2), -90,-90, true, edge);
        canvas.drawArc(new RectF((int) (width2 * start), bottom2 - left2 * 2 + top2, (int) (width2 * start) + left2 * 2, bottom2 + top2), 90,90, true, edge);

        canvas.drawArc(new RectF((int) ((start + select) * width2), 0, left2 + (int) ((start + select) * width2) + left2, left2 * 2), -90,90, true, edge);
        canvas.drawArc(new RectF((int) ((start + select) * width2), bottom2 - left2 * 2 + top2, left2 + (int) ((start + select) * width2) + left2, bottom2 + top2), 90,-90, true, edge);
        canvas.drawRect((int) (width2 * start), left2 , left2 + (int) (width2 * start), bottom2 + top2  - left2 , edge);
        canvas.drawRect(left2 + (int) ((start + select) * width2), left2, left2 + (int) ((start + select) * width2) + left2, top2 + bottom2 - left2, edge);

        edge.setColor(Color.WHITE);
        edge.setAlpha(255);

        canvas.drawRoundRect(new RectF((int) ((width2 * start) + left2/2 - left2 / 8),top2 * 10,(int) ((width2 * start) + left2/2 + left2 / 8),  bottom2 - top2 * 9),left2/8,left2/8,edge);
        canvas.drawRoundRect(new RectF( left2 + (int) (((start + select) * width2) + left2/2 - left2 / 8),top2 * 10,left2 + ((int) ((start + select) * width2) + left2/2 + left2 / 8),  bottom2 - top2 * 9),left2/8,left2/8,edge);

        edge.setColor(MainActivity.bgc);

        canvas.drawPath(round,edge);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawedge(canvas);
    }


    @Override
    protected void setData(LineInfo[] ln) {
        for (int i = 0; i < ln.length; i++) {
            points[i] = ln[i];
        }


        alps = new float[points[0].points.length];
        for (int i = 0; i < alps.length; i++) {
            alps[i] = 0;
        }
        linedots = new float[(points[0].points.length - 1) * 4];
        pointdots = new float[(points[0].points.length - 1) * 2];

        Rect r = new Rect(0, 0, 0, 0);

//        for (int i = 0; i < points[0].points.length - 1; i++) {
//            text.getTextBounds(df.format(new Date(points[0].points[i].x)), 0, df.format(new Date(points[0].points[i].x)).length(), r);
//            midwidth += r.width();
//        }
//        midwidth = midwidth / points[0].points.length + 20;

        distance = width / (float) (points[0].points.length - 1);

        if (isshow != null) {
            for (int n = 0; n < points.length; n++) {
                if (isshow[n]) points[n].mult = 1;
                else points[n].mult = 0;
            }
        }stepvalue = 20;
        graph_h = 100;
        graph_b = 0;

        //if (PersentChart.class == this.getClass()) pie.init();
        invalidate();
    }
}