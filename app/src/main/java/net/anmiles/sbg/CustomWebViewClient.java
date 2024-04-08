package net.anmiles.sbg;

import android.graphics.Bitmap;
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient {
	private final MainActivity activity;

	public CustomWebViewClient(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
		this.activity.onPageStarted();
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public boolean shouldOverrideUrlLoading(android.webkit.WebView view, WebResourceRequest request) {
		if (request.getUrl().toString().contains("window.close")) {
			this.activity.finish();
			return true;
		}

		return false;
	}
}
