package com.zed.Others;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.zed.Charts.R;

public class CheckButtonsAdapter extends BaseAdapter {
    private Context mContext;

    public CheckButton[] btns;
    public CheckButtonsAdapter(Context c,CheckButton[] btns) {
        mContext = c;
        this.btns = btns;
    }

    public int getCount() {
        return btns.length;
    }

    public Object getItem(int position) {
        return btns[position];
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        return btns[position];
    }
}