package com.zed.Charts;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.json.JSONException;
import org.json.JSONObject;

public class ChartView extends LinearLayout {
    private Chart g;

    private MiniChart mg;

    private CheckButton[] btns;

    public ChartView(Context context) {
        super(context);
        this.setOrientation(VERTICAL);

        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics()),0);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        g = new Chart(this.getContext());
        g.setLayoutParams(lp);
        g.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        this.addView(g);

        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()),0);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        mg = new MiniChart(this.getContext());
        mg.setLayoutParams(lp);
        mg.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));

        mg.setOnMiniGraphChange(new OnMiniGraphChanges() {
            @Override
            public void Check() {
                g.setScale(mg.getSelect());
                g.setStart(mg.getStart());
                g.istouch = false;
               if((g.getMax() > g.newgraph_h || g.getMax() < g.newgraph_h - g.stepvalue)) {
                    g.update();
                    g.checknums();
               } else {
                   g.checknums();
                   g.invalidate();
               }
            }
        });

        this.addView(mg);

        this.setBackgroundColor(g.bgcolor);

        setFocusable(true);
    }

    public void setData(JSONObject json){
        try {
            g.setData(json);
            mg.setData(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        g.setScale(mg.getSelect());
        g.setStart(mg.getStart());
        g.update();
        mg.update();

        try {
            btns = new CheckButton[json.getJSONArray("columns").length() - 1];
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout.LayoutParams lp = new LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()),0);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));


        for(int i = 0; i < g.getLinesCount(); i++){
            final int ii = i;
            btns[i] = new CheckButton(this.getContext());
            btns[i].setLayoutParams(lp);
            btns[i].setColor(g.getPaint(i).getColor());
            try {
                btns[i].setText(json.getJSONObject("names").getString("y" + i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            btns[i].setOnCheckListener(new CheckButton.OnCheckListener() {
                @Override
                public void onCheck(CheckButton v) {
                    g.setLineVisible(ii,v.ischeck);
                    mg.setLineVisible(ii,v.ischeck);
                    g.update();
                    mg.update();
                }
            });
            btns[i].textcolor = Color.BLACK;

            btns[i].ischeck = true;
            this.addView(btns[i]);
        }
    }

    protected void setTheme(int bgc, int lnc, int txtc,int btntxtc,int i) {
        g.setTheme(bgc,lnc,txtc);
        mg.setTheme(bgc,lnc,txtc);
        this.setBackgroundColor(bgc);
            for (int n = 0; n < btns.length; n++) {
                btns[n].textcolor = btntxtc;
                btns[n].invalidate();
            }
    }

    public Chart getGraph(){
        return  g;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_OUTSIDE || ev.getAction() == MotionEvent.ACTION_DOWN){
            g.istouch = false;
            mg.onTouchEvent(ev);
            g.onTouchEvent(ev);
            g.invalidate();
        }
        return false;
    }
}
