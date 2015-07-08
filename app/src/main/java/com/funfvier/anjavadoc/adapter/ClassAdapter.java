package com.funfvier.anjavadoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.funfvier.anjavadoc.R;
import com.funfvier.anjavadoc.entity.EClass;
import com.funfvier.anjavadoc.entity.EPackage;

import java.util.Arrays;

/**
 * Created by lshi on 25.06.2015.
 */
public class ClassAdapter extends ArrayAdapter<EClass> {
    private final Context context;
    private final EClass[] classes;

    public ClassAdapter(Context context, int resource, EClass[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.classes = Arrays.copyOf(objects, objects.length);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.class_item, parent, false);
        TextView nameView = (TextView) rowView.findViewById(R.id.label);
        nameView.setText(classes[position].getName());
        TextView descriptionView = (TextView) rowView.findViewById(R.id.classDescription);
        descriptionView.setText(classes[position].getDescriptionShort());
        return rowView;
    }
}
