package com.vl.model;

import java.util.List;

public class CrawlTargets {
	private List<String> urls;

	public List<String> getUrls() {
		return urls;
	}

	public CrawlTargets setUrls(List<String> urls) {
		this.urls = urls;
		return this;
	}
}
