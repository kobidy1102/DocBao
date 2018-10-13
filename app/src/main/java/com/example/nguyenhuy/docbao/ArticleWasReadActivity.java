package com.example.nguyenhuy.docbao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ArticleWasReadActivity extends AppCompatActivity {
    ListView lv_tindadoc;
    CustomLvArticleWasReadAdapter adapter;
   static ArrayList<ArticleWasReadObject> arrTinDaDoc= new ArrayList<ArticleWasReadObject>();
     static int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_was_read);
       if(flag==0) {
           ArrayList<ArticleWasReadObject> arrTinDaDoc2 = this.getIntent().getExtras().getParcelableArrayList("arr");

           for (int i = arrTinDaDoc2.size(); i > 0; i--) {       //dao mang, lay 20 phan tu
               arrTinDaDoc.add(arrTinDaDoc2.get(i - 1));
           }
           flag=1;
       }


        lv_tindadoc=(ListView) findViewById(R.id.lv_tinDaDoc);

        adapter = new CustomLvArticleWasReadAdapter(ArticleWasReadActivity.this,
                R.layout.custom__lv_article_was_read,
                arrTinDaDoc);
        lv_tindadoc.setAdapter(adapter);
        lv_tindadoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(ArticleWasReadActivity.this,DetailsArticleOfflineActivity.class);
                intent.putExtra("link",arrTinDaDoc.get(i).link);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
