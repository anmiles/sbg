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
			try {
				this.alert(e.toString());
				this.consoleError(e);
			} catch (Exception inner) {
			} finally {
				e.printStackTrace();
			}
			return null;
		}
	}

	public void alert(String message) throws Exception {
		if (this.activity.webView == null) {
			throw new Exception("Cannot alert message: " + message);
		}

		this.activity.webView.post(() -> {
			Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
		});
	}

	public void consoleLog(String message) throws Exception {
		String escapedMessage = this.escapeJS(new String[]{ message });

		if (this.activity.webView == null) {
			throw new Exception("Cannot log message: " + escapedMessage);
		}

		this.activity.webView.post(() -> {
			this.activity.scriptLoader.embedScript("console.log(" + escapedMessage + ")");
		});
	}

	public void consoleError(Exception exception) throws Exception {
		ArrayList<String> lines = new ArrayList<>();

		lines.add(exception.getMessage());

		for (StackTraceElement element : exception.getStackTrace()) {
			lines.add("    at " + element.toString());
		}

		String message = this.escapeJS(lines.toArray(new String[0]));

		if (this.activity.webView == null) {
			throw new Exception("Cannot alert error message: " + message);
		}

		this.activity.webView.post(() -> {
			this.activity.scriptLoader.embedScript("console.error(" + message + ")");
		});
	}
}
