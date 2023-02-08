package com.poc.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.poc.main.database.*;

import java.util.HashMap;

public class POCActivity extends Activity {

    public static final String WEB
            = "file:///android_asset/";
    private boolean isFirstTime;
    private WebView webview;
    private WindowManager wm;
    private View overlayView;
    private boolean createOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);

        boolean forcerecreate = false;
        boolean ownerVersion = AbstractBO.hasOwnerPermission();
        DatabaseDataHelper dataHelper;
        if (ownerVersion) {
            DatabaseOwnerContext oc = new DatabaseOwnerContext(this);
            dataHelper = new DatabaseDataHelper(oc);
        } else {
            DatabaseContext dc = new DatabaseContext(this);
            dataHelper = new DatabaseDataHelper(dc);
        }

        try {
            if (forcerecreate) {
                dataHelper.destroyCurrentVersion();
            }

            boolean dbExist = dataHelper.checkDataBase();
            if (!dbExist) {
                isFirstTime = true;
            } else {
                isFirstTime = false;
            }
            dataHelper.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        openWebview();
    }


    @SuppressLint("JavascriptInterface")
    public void openWebview(){
        setContentView(R.layout.dl_webview);
        preventFullScreen();
        setContentView(R.layout.dl_webview);

        String webUrl = WEB+"demo.html";
        try {
            webview = (WebView) findViewById(R.id.webview);

            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webview.setWebChromeClient(new MyCustomWebViewClient());
            webview.addJavascriptInterface(new DLJavascriptInterface(this), "Android");
            webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webview.getSettings().setUseWideViewPort(true);
            webview.loadUrl(webUrl);
            webview.requestFocus(View.FOCUS_DOWN);

            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url)  {
                    System.out.println("webUrl done!" + url);

                }
            });

            if (android.os.Build.VERSION.SDK_INT >= 21) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
            } else {
                CookieManager.getInstance().setAcceptCookie(true);
            }
            webview.setScrollbarFadingEnabled(false);
            webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

            this.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    LinearLayout relativeLayout = ((LinearLayout)findViewById(R.id.relativeMain));
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    int height = relativeLayout.getContext().getResources().getDisplayMetrics().heightPixels;
                    System.out.println("#height=" +height);
                    int diff = height - r.bottom;
                    if (diff != 0) {
                        System.out.println("#diff="+diff);
                        if(diff<0){
                            diff = 0;
                        }

                        relativeLayout.setPadding(0, 0, 0, diff);
                    } else {
                        System.out.println("#diff="+0);
                        if (relativeLayout.getPaddingBottom() != 0) {
                            relativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                    webview.setPadding(0,0,0,0);
                    System.out.println("#WEBVIEWH="+webview.getHeight());
                    System.out.println("#WEBVIEWW="+webview.getWidth());

                    System.out.println("#WEBVIEWPT="+webview.getPaddingTop());
                    System.out.println("#WEBVIEPB="+webview.getPaddingBottom());
                    System.out.println("#RLH="+relativeLayout.getHeight());
                    System.out.println("#RLW="+relativeLayout.getWidth());

                    System.out.println("#RLPT="+relativeLayout.getPaddingTop());
                    System.out.println("#RLPB="+relativeLayout.getPaddingBottom());

                    //forceWebviewResize();

                }
            });


        }catch(Exception ers) {
            ers.printStackTrace();
        }
    }

    private void preventFullScreen(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private class MyCustomWebViewClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(POCActivity.this)
                    .setTitle("PoC")
                    .setMessage(message)
                    .show();
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(POCActivity.this)
                    .setTitle("PoC")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    result.cancel();
                                }
                            })
                    .create()
                    .show();

            return true;
        }
    }

    public class DLJavascriptInterface {
        private Context context;

        DLJavascriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void onCancel() {
            System.out.println("onCancel boot");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(POCActivity.this, "onCancel called.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public String isSubmitted(){
            System.out.println("isSubmitted keyData=");
            return "Works";
        }

        @JavascriptInterface
        public void onSubmit(String values) {
            System.out.println("onSubmit boot " + values);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(POCActivity.this, "onSubmit called.",
                            Toast.LENGTH_LONG).show();;
                }
            });
        }
    }

    public void createOverlay(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        overlayView = layoutInflater.inflate(R.layout.splash, null);
        wm.addView(overlayView, params);
    }
    public void removeOverlay(){
        try {
            wm.removeView(overlayView);
        }catch (Exception ers){
            ers.printStackTrace();
        }catch(Error errr){
            errr.printStackTrace();
        }
    }
}
