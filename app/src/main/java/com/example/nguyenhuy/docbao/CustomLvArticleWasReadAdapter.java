package com.example.nguyenhuy.docbao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nguyenhuy on 17/11/2017.
 */

public class CustomLvArticleWasReadAdapter extends ArrayAdapter<ArticleWasReadObject> {
    public CustomLvArticleWasReadAdapter(Context context, int resourse, List<ArticleWasReadObject> items) {
        super(context, resourse, items);

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi=LayoutInflater.from(getContext());
            v= vi.inflate(R.layout.custom__lv_article_was_read,null);
        }

        // Get item
        ArticleWasReadObject articleWasRead = getItem(position);
        if (articleWasRead!=null){
            TextView title = (TextView) v.findViewById(R.id.text);
            title.setText(articleWasRead.title);
            //TextView tvPosition = (TextView) v.findViewById(R.id.item_employee_tv_position);
            //tvPosition.setText("Staff");
            ImageView imgManager = (ImageView) v.findViewById(R.id.img);

        }



        return v;
    }



}
