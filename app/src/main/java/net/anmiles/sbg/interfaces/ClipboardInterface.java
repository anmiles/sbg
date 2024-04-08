package net.anmiles.sbg.interfaces;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.webkit.JavascriptInterface;

import net.anmiles.sbg.MainActivity;

public class ClipboardInterface {
	private final MainActivity activity;

	public ClipboardInterface(MainActivity activity) {
		this.activity = activity;
	}

	private ClipboardManager GetClipboardManager() {
		return (ClipboardManager) this.activity.getSystemService(Context.CLIPBOARD_SERVICE);
	}

	@JavascriptInterface
	@SuppressWarnings("unused")
	public String readText() {
		return this.activity.caller.call(() -> {
			ClipboardManager clipboardManager = this.GetClipboardManager();
			ClipData clip = clipboardManager.getPrimaryClip();
			return String.valueOf(clip.getItemAt(0).getText());
		});
	}

	@JavascriptInterface
	@SuppressWarnings("unused")
	public void writeText(String text) {
		this.activity.caller.call(() -> {
			ClipboardManager clipboardManager = this.GetClipboardManager();
			ClipData clip = ClipData.newPlainText("clipboard", text);
			clipboardManager.setPrimaryClip(clip);
			return null;
		});
	}
}
