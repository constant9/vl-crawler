package com.vl.messaging;

public class CrawlResult {
	private String url;
	private String content;

	public CrawlResult() {
	}

	public CrawlResult(String url, String content) {
		this.url = url;
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public CrawlResult setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getContent() {
		return content;
	}

	public CrawlResult setContent(String content) {
		this.content = content;
		return this;
	}
}
