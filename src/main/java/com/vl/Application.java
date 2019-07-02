package com.vl;

import com.vl.contorllers.UrlController;
import com.vl.messaging.CrawlResult;
import com.vl.messaging.IngestionFinishedEvent;
import com.vl.repositories.cassandra.SlicesRepository;
import com.vl.services.SliceService;
import com.vl.workers.Crawlers;
import com.vl.workers.Splitters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication(exclude = { JpaRepositoriesAutoConfiguration.class, CassandraRepositoriesAutoConfiguration.class})
@ComponentScan
@EnableCassandraRepositories(basePackageClasses = SlicesRepository.class)
public class Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private ApplicationContext appContext;

	@EventListener(ApplicationReadyEvent.class)
	public void crawlAfterBeansUp() {
		UrlsConfig urlsConfig = appContext.getBean(UrlsConfig.class);
		List<String> urls = urlsConfig.getUrls();
		if(urls != null && !urls.isEmpty()){
			SliceService sliceService = appContext.getBean(SliceService.class);
			sliceService.crawlUrls(new HashSet<>(urls));
		}
	}

	@EventListener
	public void ingestionFinishedEventHandler(IngestionFinishedEvent event){
		logger.info("finishing event received, closing application");
		System.exit(0);
	}

	@Bean(name = "crawlResultQueue")
	public BlockingQueue<CrawlResult> crawlResultsQueue(){
		return new LinkedBlockingQueue<>();
	}
}
