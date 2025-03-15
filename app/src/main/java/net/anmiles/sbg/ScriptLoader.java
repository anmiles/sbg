package net.anmiles.sbg;

import org.json.JSONObject;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScriptLoader {
	public final MainActivity activity;
	public String mainScript;

	public ScriptLoader(MainActivity activity) {
		this.activity = activity;
	}

	public void getScripts(JSONObject urls) {
		if (BuildConfig.preset == "noscript") {
			return;
		}

		this.activity.caller.call(() -> {
			this.mainScript = this.getScript(urls.getJSONObject("mobile"));
			return null;
		});
	}

	public void embedScripts(JSONObject urls) {
		if (BuildConfig.preset == "noscript") {
			return;
		}

		this.embedScript("window.__sbg_urls = " + urls.toString() + ";");
		this.embedScript("window.__sbg_local = " + (BuildConfig.script == "local" ? "true" : "false") + ";");
		this.embedScript("window.__sbg_preset = '" + BuildConfig.preset + "';");
		this.embedScript("window.__sbg_package = '" + BuildConfig.APPLICATION_ID + "';");
		this.embedScript("window.__sbg_package_version = '" + BuildConfig.VERSION_NAME + "';");

		this.embedScript("navigator.clipboard.readText = async () => clipboard.readText();");
		this.embedScript("navigator.clipboard.writeText = async (msg) => clipboard.writeText(msg);");

		this.embedScript(this.mainScript);
	}

	public void embedScript(String script) {
		this.activity.caller.call(() -> {
			this.activity.webView.loadUrl("javascript:(function () {\n\n" + script + "\n})()");
			return null;
		});
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public String readAsset(String assetName) {
		return this.activity.caller.call(() -> {
			InputStream stream = this.activity.getAssets().open(assetName);
			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			return new String(buffer);
		});
	}

	private String getScript(JSONObject data) {
		return this.activity.caller.call(() -> {
			return BuildConfig.script == "local"
				? this.readAsset(data.getString("local"))
				: this.loadScript(data.getString("remote"));
		});
	}

	private String noCache(String link) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		return link.contains("?")
			? link + "&nocache=" + timestamp
			: link + "?nocache=" + timestamp;
	}

	private String loadScript(String link) {
		return this.activity.caller.call(() -> {
			StringBuilder script = new StringBuilder();
			URL url = new URL(noCache(link));
			this.activity.caller.consoleLog(url.toString());
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			Boolean lineShown = false;

			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("//")) {
					if (!lineShown) {
						this.activity.caller.consoleLog(line);
						lineShown = true;
					}
					script.append(line).append("\n");
				}
			}

			reader.close();
			return script.toString();
		});
	}
}
