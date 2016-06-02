package com.example.lmjin_000.pedarro.Tmap.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lmjin_000.pedarro.R;

import java.util.ArrayList;

/**
 * Created by lmjin_000 on 2016-05-24.
 */
public class ListviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Listviewitem> data;
    private int layout;
    public ListviewAdapter(Context context, int layout, ArrayList<Listviewitem> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getTxt();}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        Listviewitem listviewitem=data.get(position);
        ImageView img = (ImageView)convertView.findViewById(R.id.img);
        img.setImageResource(listviewitem.getImg());
        TextView name=(TextView)convertView.findViewById(R.id.description);
        name.setText(listviewitem.getTxt());
        return convertView;
    }
}