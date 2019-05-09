package com.zed.Charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class MiniChart extends Chart {

    private float start, select, lastx,lasty, duration,duration2;
    private boolean ismove, resizeleft, resizeright;
    private OnMiniGraphChanges listener;
    private Paint edge = new Paint();

    public MiniChart(Context context) {
        super(context);

        drawgrid = false;

        start = 0;

        select = 1f;

        setScale(1);

        ismove = false;

        linesize = 3;

        listener = null;

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //v.getParent().requestDisallowInterceptTouchEvent(true);
                        lastx = event.getX();
                        lasty = event.getY();
                        duration = lastx - start * width;
                        duration2 = lastx - (start + select) * width;
                        if (event.getX() > left + start * width && event.getX() < (start + select) * width) {
                            ismove = true;
                        } else if (event.getX() < left + start * width) {
                            resizeleft = true;
                        } else if (event.getX() > (start + select) * width) {
                            resizeright = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(Math.abs(lastx - event.getX()) > Math.abs(lasty - event.getY())){
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                            if (ismove) {
                                start = (event.getX() / width) - (duration / width);
                                if (start < 0) start = 0;
                                if (start + select > 1) start = 1 - select;
                            }
                            if (resizeleft) {
                                float latst = start;
                                start = (event.getX() / width) - (duration / width);
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
                                select = (event.getX() / width) - (duration2 / width) - start;

                                if (select < 0.1f) {
                                    select = 0.1f;
                                }
                                if (start + select > 1) select = 1 - start;
                            }
                            listener.Check();
                        lasty = event.getY();
                        lastx = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        ismove = false;
                        resizeleft = false;
                        resizeright = false;
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

        width = w - this.getPaddingLeft() - this.getPaddingRight();
        height = h - this.getPaddingTop() - this.getPaddingBottom();
        left = this.getPaddingLeft();
        top = this.getPaddingTop();
        right = width + left;
        bottom = height + top;
    }

    private void drawedge(Canvas canvas) {
        edge.setStyle(Paint.Style.FILL_AND_STROKE);

        edge.setColor(linecolor);
        edge.setAlpha(200);
        canvas.drawRect(0, 0, (int) (width * start), top + width, edge);
        canvas.drawRect(left + (int) ((start + select) * width) + left, 0, getWidth(), top + width, edge);
        edge.setColor(textcolor);
        edge.setAlpha(150);
        canvas.drawRect(left + (int) (width * start), 0, left + (int) ((start + select) * width), top, edge);
        canvas.drawRect(left + (int) (width * start), bottom, left + (int) ((start + select) * width), top + width, edge);

        canvas.drawRect((int) (width * start), 0, left + (int) (width * start), top + width, edge);
        canvas.drawRect(left + (int) ((start + select) * width), 0, left + (int) ((start + select) * width) + left, top + width, edge);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawedge(canvas);
    }
}