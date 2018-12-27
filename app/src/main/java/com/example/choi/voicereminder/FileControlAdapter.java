package com.example.choi.voicereminder;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.AnyRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by choi on 2018. 3. 12..
 */

class FileControlAdapter extends ArrayAdapter<FileList> {
    Activity context;
    List<FileList> list;
    LayoutInflater inflater;

    public FileControlAdapter(Activity context, int resourceId, List<FileList> list) {
        super(context, resourceId, list);
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    private class Viewholder {
        private TextView TVFileName;
        private TextView TVFileSubname;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FileList getItem(int i) {
        return null;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final Viewholder holder;

        // Get the data item for this position
        FileList filelist  = list.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.filelist_container, viewGroup, false);
            holder = new Viewholder();

            // matching
            holder.TVFileName = (TextView) view.findViewById(R.id.TVFileName);
            holder.TVFileSubname= (TextView)view.findViewById(R.id.TVFileSubname);
            view.setTag(holder);
        } else {
            holder = (Viewholder) view.getTag();
        }

        final String Subname = filelist.subname;
        final String name = filelist.name;
        holder.TVFileName.setText(filelist.name);
        holder.TVFileSubname.setText(filelist.subname);

        final View v = view;



        return view;
    }

    public String getname(int position)
    {
        return list.get(position).name;
    }

    public String getsubname(int position)
    {
        return list.get(position).subname;
    }

}
