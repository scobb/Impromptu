package com.example.steve.impromptu.Main.Compose.ArrayAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Main.Compose.FragmentComposeType.Type;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

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

    @Override
    public int getCount() {
        return typesList.size();
    }

    @Override
    public Type getItem(int position) {
        return typesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("ConvertView", String.valueOf(position));

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.template_type_item, null);
            ImageView vImage = (ImageView) view.findViewById(R.id.templateTypeItem_imageView_typeImage);
            TextView vText = (TextView) view.findViewById(R.id.templateTypeItem_textView_typeName);

            vImage.setImageResource(typePictureId[position]);
            vText.setText(typesList.get(position).getName());
        }
        else {
            view = convertView;
        }
        return view;
    }

}