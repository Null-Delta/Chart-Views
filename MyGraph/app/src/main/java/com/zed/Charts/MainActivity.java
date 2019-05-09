package com.zed.Charts;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    RelativeLayout cl;
    ChartView[] graphs;
    Toolbar tb;
    ImageButton btn;
    int theme = 0;
    int bgc, lnc, txtc, btntxtc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cl = findViewById(R.id.cl);

        tb = findViewById(R.id.toolbar);
        tb.setElevation(8);

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (theme == 0) setThemeT(1);
                else setThemeT(0);
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        lp.setMargins(0, 0, 0, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                getResources().getDisplayMetrics()
        ));

        graphs = new ChartView[5];
        for (int i = 0; i < 5; i++) {
            try {
                graphs[i] = new ChartView(this);
                graphs[i].setLayoutParams(lp);
                graphs[i].setData(new JSONArray(readText(this, R.raw.chart_data)).getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LinearLayout ll = findViewById(R.id.ll);

            ll.addView(graphs[i]);
        }
        setThemeT(0);
    }

    private static String readText(Context context, int resId) throws IOException {
        InputStream is = context.getResources().openRawResource(resId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = br.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    public void setThemeT(int i) {
        if (i == 0) {
            ValueAnimator a = null;
            a = ValueAnimator.ofArgb(Color.parseColor("#1d2733"), Color.parseColor("#ffffff")).setDuration(250);
            a.setFrameDelay(10);
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bgc = (int) animation.getAnimatedValue();
                    for (int i = 0; i < graphs.length; i++) {
                        graphs[i].setTheme(bgc, lnc, txtc, btntxtc, i);
                    }
                }
            });
            ValueAnimator b = ValueAnimator.ofArgb(Color.parseColor("#111924"), Color.parseColor("#ededed")).setDuration(250);
            b.setFrameDelay(10);
            b.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    lnc = (int) animation.getAnimatedValue();
                    for (int i = 0; i < graphs.length; i++) {
                        graphs[i].setTheme(bgc, lnc, txtc, btntxtc, i);
                    }
                    cl.setBackgroundColor(lnc);
                }
            });
            ValueAnimator c = ValueAnimator.ofArgb(Color.parseColor("#506372"), Color.parseColor("#96a2aa")).setDuration(250);
            c.setFrameDelay(10);
            c.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    txtc = (int) animation.getAnimatedValue();
                    for (int i = 0; i < graphs.length; i++) {
                        graphs[i].setTheme(bgc, lnc, txtc, btntxtc, i);
                    }
                }
            });
            ValueAnimator d = ValueAnimator.ofArgb(Color.parseColor("#ffffff"), Color.parseColor("#000000")).setDuration(250);
            d.setFrameDelay(10);
            d.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    btntxtc = (int) animation.getAnimatedValue();
                    for (int i = 0; i < graphs.length; i++) {
                        graphs[i].setTheme(bgc, lnc, txtc, btntxtc, i);
                    }
                }
            });
            ValueAnimator e = ValueAnimator.ofArgb(Color.parseColor("#1d2733"), Color.parseColor("#517da2")).setDuration(250);
            e.setFrameDelay(10);
            e.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tb.setBackgroundColor((int) animation.getAnimatedValue());
                }
            });
            a.start();
            b.start();
            c.start();
            d.start();
            e.start();
        } else {
            ValueAnimator a = null;
            a = ValueAnimator.ofArgb(Color.parseColor("#ffffff"), Color.parseColor("#1d2733")).setDuration(250);
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    bgc = (int) animation.getAnimatedValue();
                    for (int i = 0; i < graphs.length; i++) {
                        graphs[i].setTheme(bgc, lnc, txtc, btntxtc, i);
                    }
                }
            });
            ValueAnimator b = ValueAnimator.ofArgb(Color.parseColor("#ededed"), Color.parseColor("#111924")).setDuration(250);
            b.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    lnc = (int) animation.getAnimatedValue();
                    for (int i = 0; i < graphs.length; i++) {
                        graphs[i].setTheme(bgc, lnc, txtc, btntxtc, i);
                    }
                    cl.setBackgroundColor(lnc);
                }
            });
            ValueAnimator c = ValueAnimator.ofArgb(Color.parseColor("#96a2aa"), Color.parseColor("#506372")).setDuration(250);
            c.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    txtc = (int) animation.getAnimatedValue();
                    for (int i = 0; i < graphs.length; i++) {
                        graphs[i].setTheme(bgc, lnc, txtc, btntxtc, i);
                    }
                }
            });

            ValueAnimator d = ValueAnimator.ofArgb(Color.parseColor("#000000"), Color.parseColor("#ffffff")).setDuration(250);
            d.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    btntxtc = (int) animation.getAnimatedValue();
                    for (int i = 0; i < graphs.length; i++) {
                        graphs[i].setTheme(bgc, lnc, txtc, btntxtc, i);
                    }
                }
            });
            ValueAnimator e = ValueAnimator.ofArgb(Color.parseColor("#517da2"), Color.parseColor("#1d2733")).setDuration(250);
            e.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    tb.setBackgroundColor((int) animation.getAnimatedValue());
                }
            });
            a.start();
            b.start();
            c.start();
            d.start();
            e.start();
        }
        theme = i;
    }
}
