package com.zed.DualChart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zed.Charts.MainActivity;
import com.zed.Charts.OnMiniGraphChanges;
import com.zed.Charts.R;
import com.zed.Others.ButtonsView;
import com.zed.Others.CheckButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DualChartView extends LinearLayout {
    private DualChart g;

    public DualMiniChart mg;

    public Activity parent;

    private LinearLayout ll,text;
    public TextView title,edge;

    ButtonsView v;

    public ImageView im;

    private CheckButton[] btns;
    DateFormat df = new SimpleDateFormat("MMMM dd yyyy", Locale.US);

    public DualChartView(Context context,Activity par) {
        super(context);
        this.setOrientation(VERTICAL);

        parent = par;

        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));


        text = new LinearLayout(context);
        text.setLayoutParams(lp);

        lp = new LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),0);
        lp.gravity = Gravity.CENTER;

        im = new ImageView(context);
        im.setImageDrawable(getResources().getDrawable(R.drawable.zoomout));
        im.setVisibility(GONE);

        im.setLayoutParams(lp);
        im.setScaleType(ImageView.ScaleType.FIT_CENTER);
        text.addView(im);
        lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        lp.gravity = Gravity.CENTER;

        title = new TextView(context);
        title.setGravity(Gravity.LEFT);
        title.setText("Null");
        title.setTextColor(Color.BLACK);
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLayoutParams(lp);
        text.addView(title);


        edge = new TextView(context);
        edge.setGravity(Gravity.RIGHT);
        edge.setText("Null");
        edge.setTextColor(Color.BLACK);
        edge.setTextSize(10);
        edge.setTypeface(Typeface.DEFAULT_BOLD);
        edge.setLayoutParams(lp);
        text.addView(edge);

        this.addView(text);

         lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics()),0);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        g = new DualChart(this.getContext(),this);
        g.setLayoutParams(lp);
        g.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        this.addView(g);

        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()),0);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        mg = new DualMiniChart(this.getContext(),this);
        mg.setLayoutParams(lp);
        mg.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));

        mg.setOnMiniGraphChange(new OnMiniGraphChanges() {
            @Override
            public void Check(int i) {
                if (i == 0) {
                    g.setScale(mg.getSelect());
                    g.setStart(mg.getStart());
                    edge.setText(String.valueOf(df.format(g.points[0].points[g.getStartPoint()].x) + " - " + df.format(g.points[0].points[g.getEndPoint() - 1].x)));
                    g.istouch = false;
                    g.checknums();
                    g.invalidate();

                } else if (i == 1) {
                    if ((g.getMax(0) > g.newgraph_h || g.getMax(0) < g.newgraph_h - g.stepvalue) || (g.getMin(0) < g.newgraph_b || g.getMin(0) > g.newgraph_b + g.stepvalue)) {
                        g.update(0);
                        g.checknums();
                        g.invalidate();
                    }
                    if ((g.getMax(1) > g.newgraph_h2 || g.getMax(1) < g.newgraph_h2 - g.stepvalue2) || (g.getMin(1) < g.newgraph_b2 || g.getMin(1) > g.newgraph_b2 + g.stepvalue2)) {
                        g.update(1);
                        g.checknums();
                        g.invalidate();
                    } else {
                        g.checknums();
                        g.invalidate();
                    }
                }
            }
        });


        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));



        ll = new LinearLayout(context);
        ll.setLayoutParams(lp);





        this.addView(mg);

        this.addView(ll);

        this.setBackgroundColor(MainActivity.bgc);
    }

    public void setData(JSONObject json){
        try {
            g.setData(json);

            g.isshow = new boolean[g.points.length];
            for (int i = 0; i < g.isshow.length; i++) {
                g.isshow[i] = true;
            }
            g.update(-1);

            mg.setData(json);
            mg.isshow = new boolean[mg.points.length];
            for (int i = 0; i < mg.isshow.length; i++) {
                mg.isshow[i] = true;
            }
            mg.update(-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        g.setScale(mg.getSelect());
        g.setStart(mg.getStart());
        g.update(0);
        mg.update(0);

        try {
            btns = new CheckButton[json.getJSONArray("columns").length() - 1];
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));
        lp.gravity = Gravity.CENTER;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));


        for(int i = 0; i < g.getLinesCount(); i++){
            final int ii = i;
            try {
                lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));
                lp.gravity = Gravity.CENTER;
                lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));


                btns[i] = new CheckButton(this.getContext(),json.getJSONObject("names").getString("y" + i));
                btns[i].setLayoutParams(lp);
                btns[i].setColor(g.getPaint(i).getColor());
                btns[i].setOnCheckListener(new CheckButton.OnCheckListener() {
                    @Override
                    public void onCheck(CheckButton v) {
                        g.setLineVisible(ii,v.ischeck);
                        mg.setLineVisible(ii,v.ischeck);
                            g.update(-1);
                            mg.update(-1);
                    }
                });
                btns[i].textcolor = Color.WHITE;

                btns[i].ischeck = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));

        v = new ButtonsView(this.getContext(),btns);
        v.setLayoutParams(lp);
        this.addView(v);

        edge.setText(String.valueOf(df.format(g.points[0].points[0].x) + " - " + df.format(g.points[0].points[g.points[0].points.length - 1].x)));
    }

    public void setTheme() {
        g.setTheme();
        mg.setTheme();
        this.setBackgroundColor(MainActivity.bgc);

        if(g.chartmode == 0)
        this.title.setTextColor(MainActivity.textcolor);
        else
            this.title.setTextColor(MainActivity.zoomtext);

        this.edge.setTextColor(MainActivity.textcolor);
    }

    public DualChart getGraph(){
        return  g;
    }
}
