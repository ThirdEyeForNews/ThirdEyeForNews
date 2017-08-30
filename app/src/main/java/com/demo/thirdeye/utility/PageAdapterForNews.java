package com.demo.thirdeye.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.thirdeye.R;
import com.demo.thirdeye.beans.DataObject;

import java.util.List;

/**
 * Created by Manu on 8/4/2017.
 */

public class PageAdapterForNews extends PagerAdapter {

    private Context context;
    private List<Bitmap> dataObjectList;
    private LayoutInflater layoutInflater;
    public PageAdapterForNews(Context context, List<Bitmap> dataObjectList){
        this.context = context;
        this.layoutInflater = (LayoutInflater)this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        this.dataObjectList = dataObjectList;
    }

    @Override
    public int getCount() {
        return dataObjectList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = this.layoutInflater.inflate(R.layout.news_image, container, false);

        ImageView displayImage = (ImageView)view.findViewById(R.id.news_image_view);

        displayImage.setImageBitmap(this.dataObjectList.get(position));

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
