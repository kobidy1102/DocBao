package com.example.nguyenhuy.docbao.MainActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nguyenhuy.docbao.ReadArticleActivity.ReadArticleActivity;
import com.example.nguyenhuy.docbao.DatabaseHandler;
import com.example.nguyenhuy.docbao.DetailsArticleActivity.DetailsArticleActivity;
import com.example.nguyenhuy.docbao.R;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvArticle;
    CustomLvArticleAdapter adapter;
    ArrayList<ArticleObject> arrArticle;

    static String linkWeb = "http://vietnamnet.vn/rss/home.rss";
    static String titleWeb = "VietNamNet.vn";
    boolean isLoading = true;
    static int idWebSite = 0;
    MenuItem itemMenu_type_article;
    public static DatabaseHandler databaseArticleWasRead;
    public static DatabaseHandler databaseSavedArticle;


    WebSiteObject website;
    ArrayList<WebSiteObject> arrMenuWeb = new ArrayList<WebSiteObject>();
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    String tenWeb[] = {"VietNamNet.vn", "VnExpress.net", "DânTrí.com.vn", "24h.com.vn", "DânViệt.vn", "Tin Đã Lưu"};
    int[] iconWeb = {R.drawable.ic_vnnet, R.drawable.ic_exx, R.drawable.ic_dantri, R.drawable.ic_24h, R.drawable.ic_danviet, R.mipmap.star};
    ListView lvWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvArticle = (ListView) findViewById(R.id.lv);
        drawerLayout = (DrawerLayout) findViewById(R.id.menuWeb);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        arrArticle = new ArrayList<ArticleObject>();
        itemMenu_type_article = (MenuItem) findViewById(R.id.mn_);

        adapter = new CustomLvArticleAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrArticle);
        lvArticle.setAdapter(adapter);

        for (int i = 0; i < tenWeb.length; i++) {
            website = new WebSiteObject(iconWeb[i], tenWeb[i]);
            arrMenuWeb.add(website);
        }
        lvWebsite = (ListView) findViewById(R.id.lv_menuWeb);
        final CustomLvWebsiteAdapter customAdapter_menuWeb = new CustomLvWebsiteAdapter(MainActivity.this, R.layout.custom__lv_website, arrMenuWeb);
        lvWebsite.setAdapter(customAdapter_menuWeb);

        //tạo database 
        databaseArticleWasRead = new DatabaseHandler(this, "TinDaDoc.sqlite", null, 1);
        databaseArticleWasRead.QueryData("CREATE TABLE IF NOT EXISTS contacts(id INTEGER PRIMARY KEY AUTOINCREMENT, title NVARCHAR(100),link VARCHAR(100))");

        databaseSavedArticle = new DatabaseHandler(this, "TinDaLuu.sqlite", null, 1);
        databaseSavedArticle.QueryData("CREATE TABLE IF NOT EXISTS contacts(id INTEGER PRIMARY KEY AUTOINCREMENT, img NVARCHAR(100),title NVARCHAR(100),link VARCHAR(100),date NVARCHAR(20))");

        // kiểm tra mạng
        if (isNetworkAvailable() == true) {
            // Tin đã lưu
            if (idWebSite == 5) {
                getSavedArticleFromDatabase();
            } else{ new ReadDataFromURL().execute(linkWeb);}
        } else {
            showDialogWhenNoNetwork();
        }

        




       // drawerLayout = (DrawerLayout) findViewById(R.id.menuWeb);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //hien cai button
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // checkOpenOrClose=true;
                Log.e("xx", "click ben trai");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //  checkOpenOrClose=false;
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        MainActivity.this.setTitle(titleWeb);




        // event click listview
        listviewArticleClick();

        listviewWebsiteClick();



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
        // isLoading=false;


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);

    }

    // click trang bao nao thi menu trang bao do hien
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loaibao, menu);
        getMenuInflater().inflate(R.menu.menu_loaibao_vnexpress, menu);
        getMenuInflater().inflate(R.menu.menu_loaibao_dantri, menu);
        getMenuInflater().inflate(R.menu.menu_loaibao_24h, menu);
        getMenuInflater().inflate(R.menu.menu_loaibao_danviet, menu);

        showMenuOfWebsite(idWebSite, menu);

        return super.onCreateOptionsMenu(menu);

    }


    // event click  menu  bên phải || click drawerlayout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        isLoading = false;
        //click item menu_TypeBao

        String link = selectItemRightMenu(item);

        // nếu có click vào cái listview
        if(link.equals("abc")==false) {
            arrArticle.clear();
            new ReadDataFromURL().execute(link);
            isLoading = true;
            Log.e("xx", "click menu ben phải ==" + link);
        }
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }


    class ReadDataFromURL extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("      Loading...");
            if (isLoading == true) {
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            //  ArrayList<NewsModel> arr=new ArrayList<NewsModel>();
            String url = strings[0];
         //   arrArticle.clear();
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("item");
                for (org.jsoup.nodes.Element item : elements) {
                    String title = item.select("title").text();
                    String link = item.select("link").text();
                    String date = item.select("pubDate").text();
                    String des = item.select("description").text();
                    //     Log.d("des",des);
                    org.jsoup.nodes.Document docImage = Jsoup.parse(des);
                    String image = docImage.select("img").get(0).attr("src");
                    //  Log.d("img",image);
                    Log.d("link", link);
                    String title2 = title.replace("'", "*");
                    String title3 = title2.replace("&#34;", " ");
                    title3 = title3.replace("&#40;", " ");
                    title3 = title3.replace("&#41;", " ");
                    title3 = title3.replace("&#39;", " ");

                    date = date.replace("(GMT+7)", "");
                    date = date.replace("GMT+7", "");

                    date = date.replace("+0700", "");
                    if (date.indexOf(",") != -1)
                        date = date.substring(date.indexOf(",") + 1);
                    date = date.replace("Jan", "/01/");
                    date = date.replace("Feb", "/02/");
                    date = date.replace("Mar", "/03/");
                    date = date.replace("Apr", "/04/");
                    date = date.replace("May", "/05/");
                    date = date.replace("June", "/06/");
                    date = date.replace("July", "/07/");
                    date = date.replace("Aug", "/08/");
                    date = date.replace("Sept", "/09/");
                    date = date.replace("Oct", "/10/");
                    date = date.replace("Nov", "/11/");
                    date = date.replace("Dec", "/12/");
                    if (date.length() > 11)
                        date = date.substring(0, date.length() - 10);


                    arrArticle.add(new ArticleObject(title3, link, image, date));
                    Log.d("arr", "" + arrArticle.size());

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("error ", "" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

//            adapter = new CustomLvArticleAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrArticle);
//            lvArticle.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if (isLoading == true) {
                dialog.dismiss();
            }
            super.onPostExecute(s);

            Log.d("arr", "...." + arrArticle.size());
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    private String selectItemRightMenu(MenuItem item) {

        String link = "abc";
        switch (item.getItemId()) {
            case R.id.mn_trangChu:
                link = "http://vietnamnet.vn/rss/home.rss";
                isLoading = true;
                break;
            case R.id.mn_phapLuat:
                link = "http://vietnamnet.vn/rss/phap-luat.rss";
                isLoading = true;
                break;
            case R.id.mn_congNghe:
                link = "http://vietnamnet.vn/rss/cong-nghe.rss";
                isLoading = true;
                break;
            case R.id.mn_kinhDoanh:
                link = "http://vietnamnet.vn/rss/kinh-doanh.rss";
                isLoading = true;
                break;
            case R.id.mn_giaoDuc:
                link = "http://vietnamnet.vn/rss/giao-duc.rss";
                isLoading = true;
                break;
            case R.id.mn_thoiSu:
                link = "http://vietnamnet.vn/rss/thoi-su.rss";
                isLoading = true;
                break;
            case R.id.mn_giaiTri:
                link = "http://vietnamnet.vn/rss/giai-tri.rss";
                isLoading = true;
                break;
            case R.id.mn_sucKhoe:
                link = "http://vietnamnet.vn/rss/suc-khoe.rss";
                isLoading = true;
                break;
            case R.id.mn_theThao:
                link = "http://vietnamnet.vn/rss/the-thao.rss";
                isLoading = true;
                break;
            case R.id.mn_doiSong:
                link = "http://vietnamnet.vn/rss/doi-song.rss";
                isLoading = true;
                break;
            case R.id.mn_theGioi:
                link = "http://vietnamnet.vn/rss/the-gioi.rss";
                isLoading = true;
                break;
            case R.id.mn_batDongSan:
                link = "http://vietnamnet.vn/rss/bat-dong-san.rss";
                isLoading = true;
                break;
            case R.id.mn_banDoc:
                link = "http://vietnamnet.vn/rss/ban-doc.rss";
                isLoading = true;
                break;
            case R.id.mn_tinMoiNong:
                link = "http://vietnamnet.vn/rss/tin-moi-nong.rss";
                isLoading = true;
                break;
            case R.id.mn_tinNoiBat:
                link = "http://vietnamnet.vn/rss/tin-noi-bat.rss";
                isLoading = true;
                break;
            case R.id.mn_tuanVietNam:
                link = "http://vietnamnet.vn/rss/tuanvietnam.rss";
                isLoading = true;
                break;
            case R.id.mn_gocNhinThang:
                link = "http://vietnamnet.vn/rss/goc-nhin-thang.rss";
                isLoading = true;
                break;

            //vnexpress
            case R.id.mn_ex_trangChu:
                link = "https://vnexpress.net/rss/tin-moi-nhat.rss";  // trang chu dang bi loi
                isLoading = true;
                break;
            case R.id.mn_ex_thoiSu:
                link = "https://vnexpress.net/rss/thoi-su.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_theGioi:
                link = "https://vnexpress.net/rss/the-gioi.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_kinhDoanh:
                link = "https://vnexpress.net/rss/kinh-doanh.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_startup:
                link = "https://vnexpress.net/rss/startup.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_giaiTri:
                link = "https://vnexpress.net/rss/giai-tri.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_theThao:
                link = "https://vnexpress.net/rss/the-thao.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_phapLuat:
                link = "https://vnexpress.net/rss/phap-luat.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_giaoDuc:
                link = "https://vnexpress.net/rss/giao-duc.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_sucKhoe:
                link = "https://vnexpress.net/rss/suc-khoe.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_giaDinh:                /////////////
                link = "https://vnexpress.net/rss/gia-dinh.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_duLich:                  ////////////
                link = "https://vnexpress.net/rss/du-lich.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_khoaHoc:
                link = "https://vnexpress.net/rss/khoa-hoc.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_soHoa:
                link = "https://vnexpress.net/rss/so-hoa.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_xe:
                link = "https://vnexpress.net/rss/oto-xe-may.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_congDong:
                link = "https://vnexpress.net/rss/cong-dong.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_tamsu:
                link = "https://vnexpress.net/rss/tam-su.rss";
                isLoading = true;
                break;
            case R.id.mn_ex_cuoi:
                link = "https://vnexpress.net/rss/cuoi.rss";
                isLoading = true;
                break;

            //dantri
            case R.id.mn_dt_tc:
                link = "http://dantri.com.vn/trangchu.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_sk:
                link = "http://dantri.com.vn/suc-khoe.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_xh:
                link = "http://dantri.com.vn/xa-hoi.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_gt:
                link = "http://dantri.com.vn/giai-tri.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_gdkh:
                link = "http://dantri.com.vn/giao-duc-khuyen-hoc.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_tt:
                link = "http://dantri.com.vn/the-thao.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_tg:
                link = "http://dantri.com.vn/the-gioi.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_kd:
                link = "http://dantri.com.vn/kinh-doanh.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_otoxm:
                link = "http://dantri.com.vn/o-to-xe-may.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_sms:
                link = "http://dantri.com.vn/suc-manh-so.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_tygt:
                link = "http://dantri.com.vn/tinh-yeu-gioi-tinh.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_cl:
                link = "http://dantri.com.vn/chuyen-la.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_vl:
                link = "http://dantri.com.vn/viec-lam.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_nst:
                link = "http://dantri.com.vn/nhip-song-tre.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_pl:
                link = "http://dantri.com.vn/phap-luat.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_bd:
                link = "http://dantri.com.vn/ban-doc.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_vh:
                link = "http://dantri.com.vn/van-hoa.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_dh:
                link = "http://dantri.com.vn/du-hoc.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_dl:
                link = "http://dantri.com.vn/du-lich.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_ds:
                link = "http://dantri.com.vn/doi-song.rss";
                isLoading = true;
                break;
            case R.id.mn_dt_khcn:
                link = "http://dantri.com.vn/khoa-hoc-cong-nghe.rss";
                isLoading = true;
                break;

            //24h
            case R.id.mn_24h_tc:
                link = "http://www.24h.com.vn/upload/rss/tintuctrongngay.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_bd:
                link = "http://www.24h.com.vn/upload/rss/bongda.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_anhs:
                link = "http://www.24h.com.vn/upload/rss/anninhhinhsu.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_tt:
                link = "http://www.24h.com.vn/upload/rss/thoitrang.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_tcbds:
                link = "http://www.24h.com.vn/upload/rss/taichinhbatdongsan.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_at:
                link = "http://www.24h.com.vn/upload/rss/amthuc.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_ld:
                link = "http://www.24h.com.vn/upload/rss/lamdep.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_phim:
                link = "http://www.24h.com.vn/upload/rss/phim.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_gddh:
                link = "http://www.24h.com.vn/upload/rss/giaoducduhoc.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_btcs:
                link = "http://www.24h.com.vn/upload/rss/bantrecuocsong.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_tt2:
                link = "http://www.24h.com.vn/upload/rss/thethao.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_cntt:
                link = "http://www.24h.com.vn/upload/rss/congnghethongtin.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_otoxm:
                link = "http://www.24h.com.vn/upload/rss/otoxemay.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_dl:
                link = "http://www.24h.com.vn/upload/rss/dulich.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_skds:
                link = "http://www.24h.com.vn/upload/rss/suckhoedoisong.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_cuoi24h:
                link = "http://www.24h.com.vn/upload/rss/cuoi24h.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_tg:
                link = "http://www.24h.com.vn/upload/rss/tintucquocte.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_dss:
                link = "http://www.24h.com.vn/upload/rss/doisongshowbiz.rss";
                isLoading = true;
                break;
            case R.id.mn_24h_gt:
                link = "http://www.24h.com.vn/upload/rss/giaitri.rss";
                isLoading = true;
                break;


            // danviet
            case R.id.mn_dv_trangChu:
                link = "http://danviet.vn/rss/tin-tuc-1001.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_theGioi:
                link = "http://danviet.vn/rss/the-gioi-1007.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_theThao:
                link = "http://danviet.vn/rss/the-thao-1035.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_phapLuat:
                link = "http://danviet.vn/rss/phap-luat-1008.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_kinhTe:
                link = "http://danviet.vn/rss/kinh-te-1004.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_nhaNong:
                link = "http://danviet.vn/rss/nha-nong-1009.rss";
                isLoading = true;
                break;

            case R.id.mn_dv_giaDinh:
                link = "http://danviet.vn/rss/gia-dinh-1023.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_congNghe:
                link = "http://danviet.vn/rss/cong-nghe-1030.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_otoxm:
                link = "http://danviet.vn/rss/o-to-xe-may-1034.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_banDoc:
                link = "http://danviet.vn/rss/ban-doc-1043.rss";
                isLoading = true;
                break;
            case R.id.mn_dv_duLich:
                link = "http://danviet.vn/rss/du-lich-1097.rss";
                isLoading = true;
                break;
            default:
                break;

        }

        return link;
    }


    private void showMenuOfWebsite(int idWebSite, Menu menu) {


        if (idWebSite == 0) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);


        } else if (idWebSite == 1) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);


        } else if (idWebSite == 2) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);

        } else if (idWebSite == 3) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(false);

        } else if (idWebSite == 4) {
            Toast.makeText(MainActivity.this, "tên bác", Toast.LENGTH_SHORT).show();
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(true);

        } else if (idWebSite == 5) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);

        }
    }


    private  void listviewArticleClick(){

        final boolean[] isLongClick = {false};

        lvArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override        //load qua activity 2 khi kick vao item listview
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //insert data       chu y chu insert
                if (isLongClick[0] == false) {
                    databaseArticleWasRead.QueryData("INSERT INTO contacts VALUES(null,'" + arrArticle.get(i).title + "','" + arrArticle.get(i).link + "')");
                    Intent intent = new Intent(MainActivity.this, DetailsArticleActivity.class);
                    intent.putExtra("link", arrArticle.get(i).link);
                    intent.putExtra("image", arrArticle.get(i).image);
                    intent.putExtra("title", arrArticle.get(i).title);
                    intent.putExtra("date", arrArticle.get(i).date);
                    startActivity(intent);
                }
            }

        });
        //long click lv

        lvArticle.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (idWebSite == 5) {
                    isLongClick[0] = true;
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("     Xác Nhận...");
                    alertDialog.setMessage("Bạn có thực sự muốn xóa tin này!");
                    alertDialog.setIcon(R.drawable.war);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i2) {
                            databaseSavedArticle.QueryData("DELETE FROM contacts WHERE id='" + arrArticle.get(i).id + "'");
                            arrArticle.remove(i);
                            adapter.notifyDataSetChanged();
                            isLongClick[0] = false;
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setNeutralButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isLongClick[0] = false;
                            dialogInterface.dismiss();
                        }
                    });

                    alertDialog.show();

                }
                return false;

            }
        });
    }

    private void getSavedArticleFromDatabase(){
        arrArticle.clear();
        Cursor dataContacts = databaseSavedArticle.GetData("SELECT * FROM contacts");
        while (dataContacts.moveToNext()) {    //khi con` du lieu
            int id = dataContacts.getInt(0);

            String img = dataContacts.getString(1);
            String title = dataContacts.getString(2); //cot 1
            String link = dataContacts.getString(3);
            String date = dataContacts.getString(4);
            arrArticle.add(0, new ArticleObject(id, title, link, img, date));
            adapter.notifyDataSetChanged();
        }
    }


    private  void showDialogWhenNoNetwork(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Không có kết nối internet");
        alertDialog.setMessage("Bạn có muốn xem lại những tin đã đọc gần đây !");
        alertDialog.setIcon(R.drawable.errorw);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, ReadArticleActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.setNeutralButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                System.exit(0);
            }
        });

        alertDialog.show();
    }

    private  void listviewWebsiteClick(){
        lvWebsite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                idWebSite = i;

                Log.e("xx", "clickmenuwweb");
                if (i == 1) {
                    linkWeb = "https://vnexpress.net/rss/tin-moi-nhat.rss";
                    titleWeb = "VnExpress.net";
                } else if (i == 0) {
                    linkWeb = "http://vietnamnet.vn/rss/home.rss";
                    titleWeb = "VietNamNet.vn";

                } else if (i == 2) {
                    linkWeb = "http://dantri.com.vn/trangchu.rss";
                    titleWeb = "DânTrí.com.vn";
                } else if (i == 3) {
                    linkWeb = "http://www.24h.com.vn/upload/rss/tintuctrongngay.rss";
                    titleWeb = "24h.com.vn";

                } else if (i == 4) {
                    linkWeb = "http://danviet.vn/rss/tin-tuc-1001.rss";
                    titleWeb = "DânViệt.vn";

                } else if (i == 5) {
                    getSavedArticleFromDatabase();
                    Log.e("xx", "size=" + arrArticle.size());
                    titleWeb = "Tin Đã Lưu";

                }
                //cap nhap lai menu chon loai bao cua trang web
                if (idWebSite != 5) {
                    arrArticle.clear();
                    new ReadDataFromURL().execute(linkWeb);
                    Log.e("xx", "sss" + i);
                }
                Log.e("xx", "" + i);
                invalidateOptionsMenu();
                MainActivity.this.setTitle(titleWeb);
                drawerLayout.closeDrawers();  //dong cai tab chon web bao'
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("abc"," destroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("abc"," resum");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("abc"," restart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("abc"," stop");

    }
}
