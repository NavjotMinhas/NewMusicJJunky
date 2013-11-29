package com.musicjunky.extensions.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GooglePlayCrawler {

	private String url;
	private String source;
	private String title;
	private String description;
	private String image;
	private String rating;
	private boolean isDeveloperPage;
	private ArrayList<String> list;

	public GooglePlayCrawler(String url, boolean isDeveloperPage) {

		this.url = url;
		this.isDeveloperPage = isDeveloperPage;
		readSource();
		if (!isDeveloperPage) {
			setTitle();
			setDescription();
			setRating();
			setImage();
		} else {
			setDeveloperApplications();
		}
	}

	private void readSource() {
		try {
			StringBuilder builder = new StringBuilder();
			URL ytUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) ytUrl.openConnection();
			conn.setRequestProperty(
					"User-Agent",
					"<em>"
							+ "Mozilla/5.0 (X11; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0"
							+ "</em>");
			conn.setReadTimeout(20000 /* milliseconds */);
			conn.setConnectTimeout(30000 /* milliseconds */);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			source = builder.toString();
		} catch (IOException e) {
			// Some better error handling needed
			e.printStackTrace();
		}
	}

	private void setDeveloperApplications() {
		list = new ArrayList<String>();
		Pattern pattern = Pattern
				.compile("<a class=\\\"card-content-link\\\" href=\\\"(.*?)\\\">");
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			list.add("https://play.google.com" + matcher.group(1));
		}
	}

	private void setTitle() {
		Pattern pattern = Pattern.compile("<title>(.*?)</title>");
		Matcher matcher = pattern.matcher(source);
		if (matcher.find()) {
			title = matcher.group(1);
		}
	}

	private void setDescription() {
		Pattern pattern = Pattern
				.compile("<div class=\\\"app-orig-desc\\\">(.*?)</div>");
		Matcher matcher = pattern.matcher(source);
		if (matcher.find()) {
			description = matcher.group(1);
		}
	}

	private void setImage() {
		Pattern pattern = Pattern
				.compile("<img class=\\\"cover-image\\\" src=\\\"(.*?)\\\">");
		Matcher matcher = pattern.matcher(source);
		if (matcher.find()) {
			image = matcher.group(1);
		}
	}
	
	private void setRating() {
		Pattern pattern = Pattern
				.compile("<div class=\\\"score\\\">([0-9\\.]*?)</div>");
		Matcher matcher = pattern.matcher(source);
		if (matcher.find()) {
			rating = matcher.group(1);
		}
	}

	public ArrayList<String> getDeveloperApplications() throws Exception {
		if (!isDeveloperPage) {
			throw new Exception(
					"This is not a developer page therefore the applications by the developer cannot be retrieved");
		}
		return list;
	}

	public String getTitle() throws Exception {
		if (isDeveloperPage) {
			throw new Exception(
					"This is a developer page and the title could not be retrieved");
		}
		return title;
	}

	public String getDescription() throws Exception {
		if (isDeveloperPage) {
			throw new Exception(
					"This is a developer page and the description could not be retrieved");
		}
		return description;
	}

	public String getImage() throws Exception {
		if (isDeveloperPage) {
			throw new Exception(
					"This is a developer page and the image could not be retrieved");
		}
		return rating;
	}
	
	public String getRating() throws Exception {
		if (isDeveloperPage) {
			throw new Exception(
					"This is a developer page and the rating could not be retrieved");
		}
		return rating;
	}

}
