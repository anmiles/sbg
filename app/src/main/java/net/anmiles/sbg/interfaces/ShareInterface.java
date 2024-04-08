package net.anmiles.sbg.interfaces;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import net.anmiles.sbg.MainActivity;

import java.util.Locale;

public class ShareInterface {
	private final MainActivity activity;

	public ShareInterface(MainActivity activity) {
		this.activity = activity;
	}

	@JavascriptInterface
	@SuppressWarnings({"unused", "deprecation"})
	public boolean open(String url) {
		return this.activity.caller.call(() -> {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
					.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			PackageManager packageManager = this.activity.getPackageManager();
			ResolveInfo defaultActivity = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

			if (defaultActivity == null) {
				return false;
			}

			this.activity.startActivity(intent);
			return true;
		});
	}

	@JavascriptInterface
	@SuppressWarnings({"unused", "deprecation"})
	public boolean navigate(String coords, int zoom) {
		return this.activity.caller.call(() -> {
			String geoUri = String.format(Locale.getDefault(), "geo:%s?z=%d", coords, zoom);
			String geoUriWithQuery = String.format(Locale.getDefault(), "%s&q=%s", geoUri, Uri.encode(coords));

			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
					.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			PackageManager packageManager = this.activity.getPackageManager();

			ResolveInfo defaultActivity = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

			if (defaultActivity == null) {
				return false;
			}

			if (defaultActivity.activityInfo.name.startsWith("com.google.")) {
				intent.setData(Uri.parse(geoUriWithQuery));
			}

			this.activity.startActivity(intent);
			return true;
		});
	}
}
