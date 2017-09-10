package com.demo.thirdeye.utility;

/**
 * Created by Manu on 8/4/2017.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.demo.thirdeye.R;
import com.demo.thirdeye.beans.News;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView
        .Adapter<RecyclerViewAdapter
        .DataObjectHolder> {
    private static String TAG = "MyRecyclerViewAdapter";
    private List<News> mDataset;
    private static MyClickListener myClickListener;
    private View cardView;
    private ViewGroup parent;


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView heading;
        TextView details;
        ViewPager images;
        TextView viewCount;
        TextView likeCount;
        TextView dislikeCount;
        ImageView profilePic;
        TextView userName;
        TextView DateAndTime;
        TextView likeText;
        TextView dislikeText;
        TextView commentText;
        TabLayout tabLayout;

        public DataObjectHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.news_Heading);
            details = (TextView) itemView.findViewById(R.id.news_details);
            images = (ViewPager) itemView.findViewById(R.id.news_photo);
            viewCount = (TextView) itemView.findViewById(R.id.viewCount);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            dislikeCount = (TextView) itemView.findViewById(R.id.dislikeCount);
            profilePic = (ImageView) itemView.findViewById(R.id.userPic);
            userName = (TextView) itemView.findViewById(R.id.userNameForPost);
            DateAndTime = (TextView) itemView.findViewById(R.id.timeDate);
            tabLayout = (TabLayout) itemView.findViewById(R.id.tab_layout);
            likeText = (TextView) itemView.findViewById(R.id.liketext);
            dislikeText = (TextView) itemView.findViewById(R.id.disliketext);
            commentText = (TextView) itemView.findViewById(R.id.commenttext);



            heading.setTypeface(Settings.AGENCY_FB);
            details.setTypeface(Settings.AGENCY_FB);
            viewCount.setTypeface(Settings.AGENCY_FB);
            likeCount.setTypeface(Settings.AGENCY_FB);
            dislikeCount.setTypeface(Settings.AGENCY_FB);
            userName.setTypeface(Settings.AGENCY_FB);
            DateAndTime.setTypeface(Settings.AGENCY_FB);
            likeText.setTypeface(Settings.AGENCY_FB);
            dislikeText.setTypeface(Settings.AGENCY_FB);
            commentText.setTypeface(Settings.AGENCY_FB);

            itemView.setOnClickListener(this);
            likeText.setOnClickListener(this);
            dislikeText.setOnClickListener(this);
            commentText.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public RecyclerViewAdapter(List<News> myDataset) {
        Log.d(TAG,"Started : "+myDataset.size());

        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        this.parent = parent;
        cardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_for_home_page, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(cardView);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        try {
            Log.d(TAG,"binding started : "+position);

            holder.heading.setText(mDataset.get(position).getHeading());
            holder.details.setText(mDataset.get(position).getDetails());
            holder.details.setSelected(true);
            List<Bitmap> images = mDataset.get(position).getNewsPic();
            PageAdapterForNews newPageAdapter = new PageAdapterForNews(parent.getContext(), images);
            holder.images.setAdapter(newPageAdapter);
            holder.tabLayout.setupWithViewPager(holder.images, true);


            Bitmap pic = mDataset.get(position).getUserProfile().getProfilePic();
            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(parent.getResources(), pic);
            dr.setCornerRadius(50);
            holder.profilePic.setImageDrawable(dr);

            holder.userName.setText(mDataset.get(position).getUserProfile().getUserName());
            holder.DateAndTime.setText(mDataset.get(position).getDate() + " " + mDataset.get(position).getTime());
            holder.viewCount.setText(mDataset.get(position).getViewCount() + "");
            holder.likeCount.setText(mDataset.get(position).getLikeCount() + "");
            holder.dislikeCount.setText(mDataset.get(position).getDislikeCount() + "");
            Log.d(TAG,"binding completed");

        }catch (Exception e)
        {
            Log.d(TAG,"Exception : "+e.getMessage());

        }
    }


    public void addItem(News dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
