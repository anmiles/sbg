package net.anmiles.sbg;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class Caller {
	private final MainActivity activity;

	public Caller(MainActivity activity) {
		this.activity = activity;
	}

	private String escapeJS(String[] lines) {
		String[] escapedLines = Stream.of(lines)
			.map(line -> line
				.replace("\\", "\\\\")
				.replace("'", "\\'")
				.replace("\n", "\\n")
			).toArray(String[]::new);

		return "'" + String.join("\\n", escapedLines) + "'";
	}

	public <T> T call(Callable<T> fn) {
		try {
			return fn.call();
		} catch (Exception e) {
			this.alert(e.toString());
			this.consoleError(e);
			e.printStackTrace();
			return null;
		}
	}

	public void alert(String message) {
		this.activity.webView.post(() -> {
			Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
		});
	}

	private void consoleError(Exception exception) {
		ArrayList<String> lines = new ArrayList<>();

		lines.add(exception.getMessage());

		for (StackTraceElement element : exception.getStackTrace()) {
			lines.add("    at " + element.toString());
		}

		this.activity.webView.post(() -> {
			this.activity.scriptLoader.embedScript("console.error(" + this.escapeJS(lines.toArray(new String[0])) + ")");
		});
	}
}
