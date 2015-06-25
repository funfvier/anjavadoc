package com.funfvier.anjavadoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.funfvier.anjavadoc.R;
import com.funfvier.anjavadoc.entity.EPackage;

import java.util.Arrays;

/**
 * Created by lshi on 25.06.2015.
 */
public class PackageAdaper extends ArrayAdapter<EPackage> {
    private final Context context;
    private final EPackage[] packages;

    public PackageAdaper(Context context, int resource, EPackage[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.packages = Arrays.copyOf(objects, objects.length);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.package_item, parent, false);
        TextView nameView = (TextView) rowView.findViewById(R.id.label);
        nameView.setText(packages[position].getName());
        return rowView;
    }
}
