package com.demo.thirdeye.utility;

import android.content.Context;
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
 * Created by ammu on 7/28/2017.
 */

public class CustomPageAdapter extends PagerAdapter {

    private Context context;
    private List<DataObject> dataObjectList;
    private LayoutInflater layoutInflater;
    public CustomPageAdapter(Context context, List<DataObject> dataObjectList){
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
        View view = this.layoutInflater.inflate(R.layout.pager_list_items, container, false);

        ImageView displayImage = (ImageView)view.findViewById(R.id.large_image);
        TextView displayCaption = (TextView)view.findViewById(R.id.caption);
        TextView displayCaptionDetail = (TextView)view.findViewById(R.id.captionDetail);

        displayImage.setImageResource(this.dataObjectList.get(position).getImageId());
        displayCaption.setText(this.dataObjectList.get(position).getCaption());
        displayCaption.setTypeface(Settings.AGENCY_FB);
        displayCaptionDetail.setText(this.dataObjectList.get(position).getCaptionDetails());
        displayCaptionDetail.setTypeface(Settings.AGENCY_FB);

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
