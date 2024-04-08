package net.anmiles.sbg.interfaces;

import android.webkit.JavascriptInterface;

import net.anmiles.sbg.MainActivity;

public class GameInterface {
	private final MainActivity activity;

	public GameInterface(MainActivity activity) {
		this.activity = activity;
	}

	@JavascriptInterface
	@SuppressWarnings("unused")
	public boolean enableBackButton() {
		this.activity.backButtonEnabled = true;
		return true;
	}
}
