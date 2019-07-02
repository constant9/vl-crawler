package com.vl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix="scrape")
public class UrlsConfig {

	private List<String> urls;

	public List<String> getUrls() {
		return urls;
	}

	public UrlsConfig setUrls(List<String> urls) {
		this.urls = urls;
		return this;
	}
}
