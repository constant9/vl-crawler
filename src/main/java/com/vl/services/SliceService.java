package com.vl.services;

import com.vl.UrlsConfig;
import com.vl.model.SliceDto;
import com.vl.repositories.cassandra.Slice;
import com.vl.repositories.cassandra.SlicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.vl.workers.Crawlers;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SliceService {
	private static final Logger logger = LoggerFactory.getLogger(SliceService.class);

	@Autowired
	private Crawlers crawlers;

	@Autowired
	private UrlsConfig urlsConfig;

	@Autowired
	private SlicesRepository slicesRepository;


	public String getUrlContent(String url){
		List<Slice> allByKeyUrl = slicesRepository.findAllByKeyUrl(url);
		String collect = allByKeyUrl.stream().map(s -> s.getContent())
				.collect(Collectors.joining());
		return collect;
	}


	public SliceDto getUrlSlice(String url, int slice){
		Slice one = slicesRepository.findOneByKeyUrlAndKeySliceId(url, slice);
		if(one != null){
			return new SliceDto()
					.setContent(one.getContent())
					.setNumber(slice)
					.setUrl(url);
		}
		throw new NoSuchElementException(url + "  #  " +slice);
	}


	/**
	 * In a real world scenario i would return a transactionId,
	 * and supply a rest call to monitor the execution progress!
	 */
	public void crawlUrls(Set<String> urls) {
		logger.debug("crawling {} urls", urls.size());
		crawlers.crawl(urls);
	}
}
