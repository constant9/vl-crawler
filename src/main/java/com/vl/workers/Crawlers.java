package com.vl.workers;

import com.vl.messaging.CrawlResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.*;

@Component
public class Crawlers {
	private static final Logger logger = LoggerFactory.getLogger(Crawlers.class);

	private ExecutorService executorService;

	@Resource(name = "crawlResultQueue")
	private BlockingQueue<CrawlResult> crawlResultsQueue;

	public Crawlers(@Value("${workers.crawler.number}") int workers){
		executorService = Executors.newFixedThreadPool(workers);
		logger.info("initialized {} crawler workers", workers);
	}

	public void crawl(Set<String> urls){
		CompletableFuture<?>[] futures = urls.stream()
				.map(url -> new Crawler(url, crawlResultsQueue))
				.map(task -> CompletableFuture.runAsync(task, executorService))
				.toArray(CompletableFuture[]::new);
		CompletableFuture.allOf(futures).join();
		try {
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("error while terminating service", e);
		}
		//inserting the poison pill to end batch
		crawlResultsQueue.add(new CrawlResult(null, null));
	}

	private static class Crawler implements Runnable{
		private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

		private String url;
		BlockingQueue<CrawlResult> crawlResultsQueue;

		public Crawler(String url, BlockingQueue<CrawlResult> crawlResultsQueue) {
			this.url = url;
			this.crawlResultsQueue = crawlResultsQueue;
		}

		@Override
		public void run() {
			logger.debug("crawling to " + url);
			String text = crawl(url);
			logger.debug("retrieved {} bytes from {}", text.length(), url);
			if(text != null){
				crawlResultsQueue.add(new CrawlResult(url, text));
			}
			logger.debug("finished crawl of " + url);
		}

		private String crawl(String targetUrl){
			String result = null;
			try {
				URL url = new URL(targetUrl);
				HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				String line = null;
				StringBuilder tmp = new StringBuilder();
				BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				while ((line = in.readLine()) != null) {
					tmp.append(line);
				}
				result = tmp.toString();
			} catch (Exception e) {
				logger.error("Error retrieving HTML from " + targetUrl, e);
			}
			return result;
		}
	}
}
