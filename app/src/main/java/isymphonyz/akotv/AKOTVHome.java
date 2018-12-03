package isymphonyz.akotv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import isymphonyz.akotv.adapter.AKOTVChannelListAdapter;
import isymphonyz.akotv.adapter.AKOTVHomeMenuListAdapter;
import isymphonyz.akotv.connection.AllowAPI;
import isymphonyz.akotv.service.HUD;
import isymphonyz.akotv.utils.AppJavaScriptProxy;
import isymphonyz.akotv.utils.HomeKeyLocker;
import isymphonyz.akotv.utils.MyConfiguration;
import isymphonyz.akotv.utils.UrlCache;

public class AKOTVHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = AKOTVHome.this.getClass().getSimpleName();

    private RelativeLayout layout;
    private ProgressBar progressBar;
    private ImageView btnMenu;
    private AKOTVHomeMenuListAdapter adapterMenu;
    private ListView listViewMenu;
    private RelativeLayout layoutTitle;
    private VideoView videoView;
    private ListView listView;
    private WebView webView;
    private AKOTVChannelListAdapter adapter;
    private RelativeLayout.LayoutParams paramsLandscape;
    private RelativeLayout.LayoutParams paramsPortrait;

    private String urlVideo = MyConfiguration.CHOMPHON_CHANNEL;
    private String urlYoutube = MyConfiguration.YOUTUBE_CHANNEL;

    private ArrayList<Integer> logoList = null;
    private ArrayList<String> nameList = null;
    private ArrayList<String> titleList = null;
    private ArrayList<Boolean> isFavoriteList = null;

    private ArrayList<Integer> menuLogoList = null;
    private ArrayList<String> menuNameList = null;

    private AllowAPI allowAPI;

    private HomeKeyLocker mHomeKeyLocker;

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //hideSystemUI();

        Intent intent = new Intent(getApplicationContext(), HUD.class);
        //startService(intent);

        setContentView(R.layout.home);

        checkPermission();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) {
            Log.d(TAG, "actionBar");
            actionBar.setHomeButtonEnabled(false); // disable the button
            actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
            actionBar.setDisplayShowHomeEnabled(false); // remove the icon
            actionBar.setHomeAsUpIndicator(null);
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                //askedForOverlayPermission = true;
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 11);
            }
        }*/

        mHomeKeyLocker = new HomeKeyLocker();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mHomeKeyLocker.lock(AKOTVHome.this);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setBackgroundColor(Color.BLACK);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layoutTitle = (RelativeLayout) findViewById(R.id.layoutTitle);
        layoutTitle.setVisibility(View.GONE);

        btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        menuLogoList = new ArrayList<Integer>();
        menuLogoList.add(R.mipmap.ic_logo);
        menuLogoList.add(R.mipmap.ic_youtube_01);

        menuNameList = new ArrayList<String>();
        menuNameList.add(getText(R.string.app_name).toString());
        menuNameList.add(getText(R.string.home_txt_menu_youtube).toString());

        adapterMenu = new AKOTVHomeMenuListAdapter(this);
        adapterMenu.setLogoList(menuLogoList);
        adapterMenu.setNameList(menuNameList);
        listViewMenu = (ListView) findViewById(R.id.listViewMenu);
        listViewMenu.setAdapter(adapterMenu);
        listViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(menuNameList.get(position).contains(getText(R.string.home_txt_menu_youtube).toString())) {
                    /*Intent intent = null;
                    try {
                        intent =new Intent(Intent.ACTION_VIEW);
                        intent.setPackage("com.google.android.youtube");
                        intent.setData(Uri.parse(urlYoutube));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(urlYoutube));
                        startActivity(intent);
                    }*/
                    videoView.stopPlayback();
                    videoView.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                } else {
                    videoView.setVideoPath(urlVideo);
                    videoView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        paramsLandscape = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        paramsLandscape.addRule(RelativeLayout.CENTER_IN_PARENT);

        paramsPortrait = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsPortrait.addRule(RelativeLayout.BELOW, R.id.layoutTitle);

        progressBar.setVisibility(View.VISIBLE);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setZOrderOnTop(true);
        videoView.setVideoPath(urlVideo);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                //progressBar.setVisibility(View.VISIBLE);
                //videoView.setVideoPath(urlVideo);
                return false;
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.VISIBLE);
                videoView.setVideoPath(urlVideo);
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);
                videoView.start();
            }
        });
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "videoView.setOnTouchListener");
                layoutTitle.setVisibility(View.VISIBLE);
                new CountDownTimer(2500, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        layoutTitle.setVisibility(View.GONE);
                    }

                }.start();
                return false;
            }
        });

        logoList = new ArrayList<Integer>();
        logoList.add(R.mipmap.ic_logo);

        nameList = new ArrayList<String>();
        nameList.add("Chomphon Channel");

        titleList = new ArrayList<String>();
        titleList.add("ชุมพรแชนแนล");

        isFavoriteList = new ArrayList<Boolean>();
        isFavoriteList.add(false);
        isFavoriteList.add(true);

        adapter = new AKOTVChannelListAdapter(this);
        adapter.setLogoList(logoList);
        adapter.setNameList(nameList);
        adapter.setTitleList(titleList);
        adapter.setIsFavoriteList(isFavoriteList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        //listView.setVisibility(View.INVISIBLE);

        webView = (WebView) findViewById(R.id.webView);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layoutTitle.setVisibility(View.VISIBLE);
                new CountDownTimer(2500, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        layoutTitle.setVisibility(View.GONE);
                    }

                }.start();
                return false;
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        webView.setWebViewClient(webViewClient);
        //webView.setWebViewClient(new Browser());
        webView.setWebChromeClient(new MyWebClient());


        webView.addJavascriptInterface(new AppJavaScriptProxy(this, webView), "androidAppProxy");
        //webView.loadUrl(url);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("fromAndroid()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //store / process result received from executing Javascript.
                }
            });
        }

        webView.loadUrl(urlYoutube);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: ");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged: " + hasFocus);
        if (hasFocus) {
            //hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        allowAPI = new AllowAPI();
        allowAPI.setListener(new AllowAPI.AllowAPIListener() {
            @Override
            public void onAllowAPIPreExecuteConcluded() {

            }

            @Override
            public void onAllowAPIPostExecuteConcluded(String result) {
                try {
                    Log.d(TAG, "result: " + result);
                    JSONObject jObj = new JSONObject(result);
                    String status = jObj.optString("allow");

                    Log.d(TAG, "status: " + status);
                    if (status.equals("1")) {

                    } else {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        allowAPI.execute("");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("AKOTVHome", "onConfigurationChanged");
        // Pass any configuration change to the drawer toggls
        //mDrawerToggle.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen for landscape and portrait
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*if(videoView.isShown()) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.d("AKOTVHome", "Landscape");
                this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                videoView.setLayoutParams(paramsLandscape);
                layoutTitle.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                layout.setBackgroundColor(Color.BLACK);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ){
                Log.d("AKOTVHome", "Portrait");
                this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                videoView.setLayoutParams(paramsPortrait);
                layoutTitle.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layout.setBackgroundColor(Color.TRANSPARENT);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }*/
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("AKOTVHome", "Landscape");
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            videoView.setLayoutParams(paramsLandscape);
            layoutTitle.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            layout.setBackgroundColor(Color.BLACK);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ){
            Log.d("AKOTVHome", "Portrait");
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoView.setLayoutParams(paramsPortrait);
            layoutTitle.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            layout.setBackgroundColor(Color.TRANSPARENT);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // SAVE YOUR DATA IN "outstate" BUNDLE
        super.onSaveInstanceState(outState);
        Log.d("AKOTVHome", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // RESTORE YOUR DATA FROM savedInstanceState.
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("AKOTVHome", "onRestoreInstanceState");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class WebViewClientImpl extends WebViewClient {

        private Activity activity = null;
        private UrlCache urlCache = null;

        public WebViewClientImpl(Activity activity) {
            this.activity = activity;
            this.urlCache = new UrlCache(activity);

            this.urlCache.register("http://tutorials.jenkov.com/", "tutorials-jenkov-com.html",
                    "text/html", "UTF-8", 5 * UrlCache.ONE_MINUTE);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            return false;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if(url.startsWith("http://tutorials.jenkov.com/images/logo.png")){
                String mimeType = "image/png";
                String encoding = "";
                URL urlObj = null;
                InputStream input = null;
                try {
                    urlObj = new URL(url);
                    URLConnection urlConnection = urlObj.openConnection();
                    urlConnection.getInputStream();
                    input = urlConnection.getInputStream();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                WebResourceResponse response = new WebResourceResponse(mimeType, encoding, input);

                return response;
            }

            return this.urlCache.load(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("AKOTVHome", "URL: " + url);

            if("http://tutorials.jenkov.com/".equals(url)){
                this.urlCache.load("http://tutorials.jenkov.com/java/index.html");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.akotvhome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class Browser
            extends WebViewClient
    {
        Browser() {}

        public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
        {
            paramWebView.loadUrl(paramString);
            return true;
        }
    }

    public class MyWebClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public MyWebClient() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (AKOTVHome.this == null) {
                return null;
            }
            return BitmapFactory.decodeResource(AKOTVHome.this.getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)AKOTVHome.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            AKOTVHome.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            AKOTVHome.this.setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = AKOTVHome.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = AKOTVHome.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)AKOTVHome.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            AKOTVHome.this.getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
            }

        }

    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
