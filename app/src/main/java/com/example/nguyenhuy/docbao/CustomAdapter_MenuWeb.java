package com.example.nguyenhuy.docbao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by nguyenhuy on 14/11/2017.
 */

public class CustomAdapter_MenuWeb extends ArrayAdapter<MenuWeb>{
    public CustomAdapter_MenuWeb(Context context, int resourse, List<MenuWeb> items) {
        super(context, resourse, items);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi=LayoutInflater.from(getContext());
            v= vi.inflate(R.layout.custom_layout_lv_menuweb,null);
        }

        // Get item
        MenuWeb menuWeb = getItem(position);
        if (menuWeb!=null) {
            TextView tenMenu = (TextView) v.findViewById(R.id.tv_tenMenu);
            tenMenu.setText(menuWeb.tenMenu);

            ImageView iconMenu = (ImageView) v.findViewById(R.id.img_iconMenu);
            Picasso.with(getContext()).load(menuWeb.image).into(iconMenu);


        }

        return v;
    }
}
