package it.vetprofit.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.provider.Settings;
import android.webkit.*;
import android.widget.Toast;
import java.io.*;

public class MainActivity extends Activity {
  private WebView web;
  private ValueCallback<Uri[]> fileCallback;
  private static final int PICK_FILE=10;

  @Override public void onCreate(Bundle b){super.onCreate(b);
    web=new WebView(this); setContentView(web);
    WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true); s.setAllowContentAccess(true);
    web.setWebViewClient(new WebViewClient());
    web.setWebChromeClient(new WebChromeClient(){
      @Override public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p){
        if(fileCallback!=null)fileCallback.onReceiveValue(null); fileCallback=cb;
        try{startActivityForResult(p.createIntent(),PICK_FILE);}catch(Exception e){fileCallback=null;return false;} return true;
      }
    });
    web.setDownloadListener((url,ua,cd,mime,len)->{
      if(url!=null && url.startsWith("blob:")){
        web.evaluateJavascript("(function(){var x=localStorage.getItem('vetprofit');return x;})()", value -> Toast.makeText(this,"Usa Impostazioni → Crea backup: nella versione APK il backup verrà condiviso in un aggiornamento successivo.",Toast.LENGTH_LONG).show());
      }
    });
    web.loadUrl("file:///android_asset/index.html");
  }
  @Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(req==PICK_FILE&&fileCallback!=null){fileCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res,data));fileCallback=null;}}
  @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}
