package com.vl.workers;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.vl.messaging.CrawlResult;
import com.vl.messaging.IngestionFinishedEvent;
import com.vl.repositories.cassandra.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.vl.repositories.cassandra.Slice;
import com.vl.repositories.cassandra.SlicesRepository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.*;

@Component
public class Splitters {
	private static final Logger logger = LoggerFactory.getLogger(Splitters.class);
	@Value("${slice.max.size:10000}")
	private int maxChunk = 30;
	private ExecutorService executorService;

	@Resource(name = "crawlResultQueue")
	private BlockingQueue<CrawlResult> crawlResultsQueue;

	@Autowired
	private SlicesRepository slicesRepository;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	Thread queueConsumerThread;

	public Splitters(@Value("${workers.splitter.number}") int workers){
		executorService = Executors.newFixedThreadPool(workers);
		logger.info("initialized {} splitter workers", workers);
	}

	@PostConstruct
	public void init(){
		queueConsumerThread = new Thread( () -> {
			while (true) {
				try {
					CrawlResult crawlResult = crawlResultsQueue.take();
					boolean isPoisonPill = crawlResult.getUrl() == null;
					if (isPoisonPill) {
						logger.info("shutting down splitter workers");
						executorService.shutdown();
						executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
						break;
					} else {
						executorService.execute(new TextSplitter(crawlResult, maxChunk,
								slicesRepository));
					}
				} catch (InterruptedException e) {
					logger.error("error", e);
					break;
				}
			}
			logger.info("shutting down consumer queueConsumerThread for splitters");
			logger.info("notifying app");
			applicationEventPublisher.publishEvent(new IngestionFinishedEvent());
		});
		queueConsumerThread.setDaemon(true);
		queueConsumerThread.start();
	}

	private static class TextSplitter implements Runnable{
		private static final Logger logger = LoggerFactory.getLogger(TextSplitter.class);

		private CrawlResult crawlResult;
		private SlicesRepository slicesRepository;
		private int maxChunk;

		public TextSplitter(CrawlResult crawlResult, int size, SlicesRepository slicesRepository)
		{
			this.crawlResult = crawlResult;
			this.slicesRepository = slicesRepository;
			this.maxChunk = size;
		}

		@Override
		public void run() {
			List<Slice> slices = Lists.newArrayList();
			try {
				logger.debug("splitting result of " + crawlResult.getUrl());
				Iterable<String> chunks = Splitter.fixedLength(maxChunk)
						.split(crawlResult.getContent());
				int chunkId = 0;
				for (String chunk : chunks) {
					Slice slice =  new Slice()
							.setKey(new Key(crawlResult.getUrl(),chunkId))
							.setContent(chunk);
					slices.add(slice);
					chunkId++;
				}

				slicesRepository.saveAll(slices);
				logger.debug("saved {} slices of {}", slices.size(), crawlResult.getUrl());
			} catch (Exception e) {
				logger.error("error splitting " + crawlResult.getUrl(), e);
			}
		}
	}
}
