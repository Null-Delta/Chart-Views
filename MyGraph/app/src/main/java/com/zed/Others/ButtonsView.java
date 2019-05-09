package com.zed.Others;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ButtonsView extends LinearLayout {
    CheckButton[] btns;
    boolean isadd = false;
    ArrayList<LinearLayout> ll = new ArrayList<>();
    public ButtonsView(Context context, CheckButton[] btns) {
        super(context);
        this.btns = btns;

        for(int i = 0; i < btns.length; i++){
            btns[i].setPatent(this,i);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));

        ll.add(new LinearLayout(context));
        ll.get(0).setLayoutParams(lp);
        this.setOrientation(VERTICAL);
        this.addView(ll.get(0));
        //ll.addView(btns[0]);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!isadd){
            int ii = 0;
            for (int i = 0; i < btns.length; i++) {


                int ww = 0;
                for (int n = ii; n < i; n++) {
                    ww += btns[n].width + btns[n].left * 2;
                }
                Log.i("Testing", String.valueOf(ww + btns[i].width) +"   " +  String.valueOf(ii));

                if (ww + btns[i].width < MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingLeft()) {
                    if (btns[i].getParent() != null) {
                        ((ViewGroup) btns[i].getParent()).removeView(btns[i]); // <- fix
                    }
                    ll.get(ll.size() - 1).addView(btns[i]);
                } else {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //lp.gravity = Gravity.CENTER_HORIZONTAL;
                    lp.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));

                    ll.add(new LinearLayout(getContext()));
                    ll.get(ll.size() - 1).setLayoutParams(lp);
                    this.addView(ll.get(ll.size() - 1));

                    if (btns[i].getParent() != null) {
                        ((ViewGroup) btns[i].getParent()).removeView(btns[i]); // <- fix
                    }

                    ll.get(ll.size() - 1).addView(btns[i]);
                    ii = i;
                }

            }
            isadd = true;
        }
        }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);


    }

    //        int ii = 0;

//            ViewGroup v = (ViewGroup)(btns[i].getParent());
//            v.removeAllViews();
//            if(w + btns[i].width < getWidth()) {
//                ll.addView(btns[i]);
//            }
//            else {
//                ll = new LinearLayout(this.getContext());
//                this.addView(ll);
//                btns[]
//                ll.addView(btns[i]);
//                ii = i;
//            }
    }
