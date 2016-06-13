package com.example.android.customanalogclock.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by lucky_luke on 6/10/2016.
 */
public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private int[] mImageList;

    public ImageAdapter(Context c, int[] imageList) {
        mContext = c;
        mImageList = imageList;
    }

    @Override
    public int getCount() {
        return mImageList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (null == convertView) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(160, 160));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mImageList[position]);
        return imageView;
    }
}
