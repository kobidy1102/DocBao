package com.example.nguyenhuy.docbao;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DetailsArticleActivity extends AppCompatActivity {
    WebView webView;
  //  ProgressDialog dialog;
    Button btn_share;
    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;
    String duongLink;
    String image, date,title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_details_article);
        webView=(WebView) findViewById(R.id.wv);
       // btn_share= (Button) findViewById(R.id.btn_share);
        shareDialog= new ShareDialog(DetailsArticleActivity.this);
//        dialog = new ProgressDialog(DetailsArticleActivity.this);
//        dialog.setMessage("      Loading...");
//        dialog.setCancelable(false);
//        dialog.show();

        Intent intent=getIntent();
        duongLink=intent.getStringExtra("link");
        image=intent.getStringExtra("image");
        date=intent.getStringExtra("date");
        title=intent.getStringExtra("title");
        webView.getSettings().setAllowFileAccess( true );
       // webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 30);
        webView.getSettings().setAppCacheMaxSize(Long.MAX_VALUE);
        //Log.d("max","....."+Long.MAX_VALUE);
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.getSettings().setBuiltInZoomControls(true);
       // webView.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);
        //webView.getSettings().setTextZoom(50);
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default

     //   if ( !isNetworkAvailable() ) { // loading offline
     //       webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
     //   }

        webView.loadUrl(duongLink);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            //    dialog.dismiss();

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.nguyenhuy.docbao",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_btn_share,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.mn_share){
            if(shareDialog.canShow(ShareLinkContent.class)){
                shareLinkContent= new ShareLinkContent.Builder()
                        .setContentTitle("1")
                        .setContentDescription("2")
                        .setContentUrl(Uri.parse(duongLink))
                        .build();
            }
            shareDialog.show(shareLinkContent);
        }else if(item.getItemId()==R.id.mn_save){
            MainActivity.databaseHandlerLuuTin.QueryData("INSERT INTO contacts VALUES(null,'" +image+ "','"+title+"','"+duongLink+"','"+date+"')");
            Toast.makeText(DetailsArticleActivity.this,"Đã Lưu",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}


