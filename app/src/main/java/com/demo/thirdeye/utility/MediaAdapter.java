package com.demo.thirdeye.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.thirdeye.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manu on 12/09/17.
 */

public class MediaAdapter extends BaseAdapter {
    private Context mContext;
    private final List<Bitmap> selectedMediaFileList;

    public MediaAdapter(Context c, List<Bitmap> selectedMediaFileList) {
        mContext = c;
        this.selectedMediaFileList = selectedMediaFileList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return selectedMediaFileList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.media_card, null);
            TextView size = (TextView) grid.findViewById(R.id.mediaSize);
            TextView time = (TextView) grid.findViewById(R.id.mediaTime);

            ImageView image = (ImageView) grid.findViewById(R.id.mediaImageView);
            BitmapDrawable ob = new BitmapDrawable(mContext.getResources(), selectedMediaFileList.get(position));
            image.setBackground(ob);
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}