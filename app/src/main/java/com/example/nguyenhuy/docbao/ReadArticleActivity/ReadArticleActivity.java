package com.example.nguyenhuy.docbao.ReadArticleActivity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nguyenhuy.docbao.DetailsArticleActivity.DetailsArticleOfflineActivity;
import com.example.nguyenhuy.docbao.MainActivity.MainActivity;
import com.example.nguyenhuy.docbao.R;

import java.util.ArrayList;

public class ReadArticleActivity extends AppCompatActivity {
    ListView lv_tindadoc;
    CustomLvReadArticleAdapter adapter;
   static ArrayList<ReadArticleObject> arrTinDaDoc= new ArrayList<ReadArticleObject>();
    static ArrayList<ReadArticleObject> arrTinDaDocSauKhiLoc= new ArrayList<ReadArticleObject>();

    static int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_was_read);


        // Lấy tin đã đọc gần đầy từ database
        arrTinDaDoc.clear();
        arrTinDaDocSauKhiLoc.clear();
        Cursor dataContacts = MainActivity.databaseArticleWasRead.GetData("SELECT * FROM contacts");
        while (dataContacts.moveToNext()) {    //khi con` du lieu
            int id = dataContacts.getInt(0);
            String title = dataContacts.getString(1); //cot 1
            String link = dataContacts.getString(2);
            ReadArticleObject tinDaDoc = new ReadArticleObject(id, title, link);
            arrTinDaDoc.add(tinDaDoc);
        }
        // lấy 10 cái tin gần nhất, còn lại xóa
        if (arrTinDaDoc.size() >= 11) {
            for (int i = 0; i <= arrTinDaDoc.size() - 11; i++) {
                MainActivity.databaseArticleWasRead.QueryData("DELETE FROM contacts WHERE id='" + arrTinDaDoc.get(i).id + "'");
                arrTinDaDoc.remove(i);
            }
        }
        // đảo arr đưa tin gần nhất lên đầu listview
        for (int i = arrTinDaDoc.size()-1; i >=0 ; i--) {
            arrTinDaDocSauKhiLoc.add(arrTinDaDoc.get(i));
        }


        lv_tindadoc=(ListView) findViewById(R.id.lv_tinDaDoc);

        adapter = new CustomLvReadArticleAdapter(ReadArticleActivity.this,
                R.layout.custom__lv_read_article,
                arrTinDaDocSauKhiLoc);
        lv_tindadoc.setAdapter(adapter);
        lv_tindadoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(ReadArticleActivity.this,DetailsArticleOfflineActivity.class);
                intent.putExtra("link",arrTinDaDocSauKhiLoc.get(i).link);
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
