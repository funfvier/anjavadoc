package com.funfvier.anjavadoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.funfvier.anjavadoc.R;
import com.funfvier.anjavadoc.entity.EClass;
import com.funfvier.anjavadoc.entity.EMember;

import java.util.Arrays;

/**
 * Created by lshi on 26.06.2015.
 */
public class MemberAdapter extends ArrayAdapter<EMember> {
    private final Context context;
    private final EMember[] members;

    public MemberAdapter(Context context, int resource, EMember[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.members = Arrays.copyOf(objects, objects.length);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.member_item, parent, false);
        TextView nameView = (TextView) rowView.findViewById(R.id.label);
        nameView.setText(members[position].getName());
        return rowView;
    }
}
