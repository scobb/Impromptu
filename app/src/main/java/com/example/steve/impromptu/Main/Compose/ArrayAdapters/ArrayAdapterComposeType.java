package com.example.steve.impromptu.Main.Compose.ArrayAdapters;

import com.example.steve.impromptu.R;

import android.content.Context;
import java.util.ArrayList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Main.Compose.FragmentComposeType.Type;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;

/**
 * Created by dlambert on 11/16/14.
 */

public class ArrayAdapterComposeType extends ArrayAdapter<Type> {

    private ArrayList<Type> typesList;
    private Context context;
    private Event myEvent;

    private Integer[] typePictureId = {
            R.drawable.drinking,
            R.drawable.food,
            R.drawable.sport_icon,
            R.drawable.studying,
            R.drawable.tv,
            R.drawable.working_out
    };

    private LayoutInflater l_Inflater;

    public ArrayAdapterComposeType(Context context, int textViewResourceId, ArrayList<Type> types, Event myEvent) {
        super(context, textViewResourceId, types);
        this.myEvent = myEvent;
        this.context = context;
        this.typesList = new ArrayList<Type>();
        this.typesList.addAll(types);
        l_Inflater = LayoutInflater.from(context);

    }


    public int getCount() {
        return typesList.size();
    }

//    public Object getItem(int position) {
//        return typesList.get(position);
//    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView typeName;
        ImageView typeImage;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.template_type_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.typeName = (TextView) view.findViewById(R.id.templateTypeItem_textView_typeName);
            viewHolder.typeImage = (ImageView) view.findViewById(R.id.templateTypeItem_imageView_typePicture);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(0x00FF00);
                    for (Type type : typesList) {
                        type.setSelected(false);
                    }
                    typesList.get(position).setSelected(true);
                }
            });
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        holder = (ViewHolder) view.getTag();
        holder.typeName.setText(typesList.get(position).getName());
        holder.typeImage.setImageResource(typePictureId[position]);
        return view;
    }

}

