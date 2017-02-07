package com.udacity.anton.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.udacity.anton.popularmovies.data.VideoObject;

/**
 * Created by anton on 07/02/17.
 */

public class VideoAdapter extends ArrayAdapter<VideoObject> {
    public VideoAdapter(Context context, VideoObject[] videos) {
        super(context, 0, videos);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoObject video=getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_list_item, parent, false);
        }

        TextView textView= (TextView) convertView.findViewById(R.id.video_item_text);
        textView.setText(video.getName());

        return convertView;
    }
}
