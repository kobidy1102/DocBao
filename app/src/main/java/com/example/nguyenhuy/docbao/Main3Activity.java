package com.example.nguyenhuy.docbao;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {
    ListView lv_tindadoc;
    CustomAdapter_lv_tinDaDoc adapter;
   static ArrayList<TinDaDoc> arrTinDaDoc= new ArrayList<TinDaDoc>();
     static int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
      //  Intent intent= getIntent();
      //  Bundle arr=intent.getBundleExtra("BUNDLE");
       if(flag==0) {
           ArrayList<TinDaDoc> arrTinDaDoc2 = this.getIntent().getExtras().getParcelableArrayList("arr");

           for (int i = arrTinDaDoc2.size(); i > 0; i--) {       //dao mang, lay 20 phan tu
               arrTinDaDoc.add(arrTinDaDoc2.get(i - 1));
           }
           flag=1;
       }
      // Toast.makeText(Main3Activity.this,""+flag,Toast.LENGTH_SHORT).show();



        lv_tindadoc=(ListView) findViewById(R.id.lv_tinDaDoc);

        adapter = new CustomAdapter_lv_tinDaDoc(Main3Activity.this,
                R.layout.custom_layout_lv_tindadoc,
                arrTinDaDoc);
        lv_tindadoc.setAdapter(adapter);
        lv_tindadoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(Main3Activity.this,Main4Activity.class);
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
