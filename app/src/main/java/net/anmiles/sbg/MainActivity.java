package net.anmiles.sbg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import net.anmiles.sbg.interfaces.ClipboardInterface;
import net.anmiles.sbg.interfaces.GameInterface;
import net.anmiles.sbg.interfaces.ShareInterface;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
	public ScriptLoader scriptLoader;
	public Caller caller;
	public WebView webView;
	public Boolean backButtonEnabled;

	private JSONObject urls;

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);

		ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, 777);

		this.scriptLoader = new ScriptLoader(this);
		this.caller = new Caller(this);

		this.urls = this.caller.call(() -> new JSONObject(this.scriptLoader.readAsset("urls.json")));

		this.scriptLoader.getScripts(this.urls);

		this.webView = this.findViewById(R.id.webview);

		WebSettings webSettings = this.webView.getSettings();
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setGeolocationEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		this.webView.setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null);

		this.webView.setWebViewClient(new CustomWebViewClient(this));

		this.webView.setWebChromeClient(new WebChromeClient() {
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}
		});

		this.webView.addJavascriptInterface(new ClipboardInterface(this), "clipboard");
		this.webView.addJavascriptInterface(new GameInterface(this), "__sbg_game");
		this.webView.addJavascriptInterface(new ShareInterface(this), "__sbg_share");

		if (savedInstanceState == null) {
			String startUrl = this.caller.call(() -> this.urls.getJSONObject("homepage").getString("remote"));
			this.webView.loadUrl(startUrl);
		}
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		this.webView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		this.webView.restoreState(savedInstanceState);
	}

	@Override
	public void onBackPressed() {
		if (this.backButtonEnabled) {
			this.webView.loadUrl("javascript:document.dispatchEvent(new Event(\"backbutton\"))");
		} else {
			if (this.webView.canGoBack()) {
				this.webView.goBack();
			} else {
				this.finish();
			}
		}
	}

	public void onPageStarted() {
		this.backButtonEnabled = false;
		this.scriptLoader.embedScripts(this.urls);
	}
}
