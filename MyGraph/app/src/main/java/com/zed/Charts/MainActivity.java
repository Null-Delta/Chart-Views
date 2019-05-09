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
import android.widget.TextView;
import android.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zed.BarChart.BarChartView;
import com.zed.DualChart.DualChart;
import com.zed.DualChart.DualChartView;
import com.zed.MultiBarChart.MultiBarChart;
import com.zed.MultiBarChart.MultiBarChartView;
import com.zed.PersentChart.PersentChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class MainActivity extends Activity {

    RelativeLayout cl;
    ChartView chart1;
    DualChartView chart2;
    MultiBarChartView chart3;
    BarChartView chart4;
    PersentChartView chart5;

    Toolbar tb;
    TextView title;
    ImageButton btn;
    int theme = 0;
    public static int bgc, textcolor, gridcolor,bgc2,gridtextcolor,barback,barselect,zoomtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cl = findViewById(R.id.cl);

        tb = findViewById(R.id.toolbar);
        tb.setElevation(8);

        title = findViewById(R.id.title);
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

            try {
                chart1 = new ChartView(this,this);
                chart1.setLayoutParams(lp);
                chart1.setData(new JSONObject(readText("chart_1/overview.json")));
                chart1.title.setText("Followers");

                chart2 = new DualChartView(this,this);
                chart2.setLayoutParams(lp);
                chart2.setData(new JSONObject(readText("chart_2/overview.json")));
                chart2.title.setText("Interactions");

                chart3 = new MultiBarChartView(this,this);
                chart3.setLayoutParams(lp);
                chart3.setData(new JSONObject(readText("chart_3/overview.json")));
                chart3.title.setText("Messages");

                chart4 = new BarChartView(this,this);
                chart4.setLayoutParams(lp);
                chart4.setData(new JSONObject(readText("chart_4/overview.json")));

                chart5 = new PersentChartView(this,this);
                chart5.setLayoutParams(lp);
                chart5.setData(new JSONObject(readText("chart_5/overview.json")));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LinearLayout ll = findViewById(R.id.ll);

        ll.addView(chart1);
        ll.addView(chart2);
        ll.addView(chart3);
        ll.addView(chart4);
        ll.addView(chart5);
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

    public String readText(String name) throws IOException {
        InputStream is =  getAssets().open(name);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = br.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

    public void setThemeT(final int i) {
        if (i == 0) {
            bgc = Color.parseColor("#ffffff");
            gridcolor = Color.parseColor("#182D3B");
            textcolor =  Color.parseColor("#000000");
            bgc2 = Color.parseColor("#f0f0f0");
            gridtextcolor = Color.parseColor("#252529");
            tb.setBackgroundColor(bgc);
            cl.setBackgroundColor(bgc2);
            btn.setImageDrawable(getResources().getDrawable(R.drawable.moon));
            title.setTextColor(textcolor);
            barback = Color.parseColor("#E2EEF9");
            zoomtext = Color.parseColor("#108BE3");
            barselect = Color.parseColor("#86A9C4");

        } else {
            bgc = Color.parseColor("#242f3e");
            gridcolor = Color.parseColor("#FFFFFF");
            textcolor =  Color.parseColor("#ffffff");
            bgc2 = Color.parseColor("#1b2433");
            gridtextcolor = Color.parseColor("#A3B1C2");
            tb.setBackgroundColor(bgc);
            cl.setBackgroundColor(bgc2);
            btn.setImageDrawable(getResources().getDrawable(R.drawable.moon2));
            title.setTextColor(textcolor);
            barback = Color.parseColor("#304259");
            zoomtext = Color.parseColor("#48AAF0");
            barselect = Color.parseColor("#6F899E");
        }


        chart1.setTheme();
        chart2.setTheme();
        chart3.setTheme();
        chart4.setTheme();
        chart5.setTheme();
        theme = i;
    }
}
