package com.vl.contorllers;

import com.google.common.collect.Sets;
import com.vl.model.SliceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.vl.services.SliceService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/api")
public class UrlController {

	@Autowired
	private SliceService sliceService;

	@RequestMapping(value = "/urls/crawl", method = RequestMethod.POST)
	public String getUrlContent(@RequestBody List<String> crawlTargets) {
		sliceService.crawlUrls(Sets.newHashSet(crawlTargets));
		return "started crawling";
	}

	@RequestMapping(value = "/urls/{url}", method = RequestMethod.GET)
	public String getUrlContent(@PathVariable String url) {
		String urlContent = sliceService.getUrlContent(url);
		return validateResult(urlContent, url);
	}

	//@RequestMapping(value = "/urls/{url}/slice/{slice}", method = RequestMethod.GET)
	public SliceDto getUrlSliceContent(@PathVariable String url,
									   @PathVariable int slice) {
		SliceDto urlSlice = sliceService.getUrlSlice(url, slice);
		return validateResult(urlSlice, url + "#" + slice);
	}

	private static <T> T validateResult(T result, String arg){
		if(result == null) {
			throw new NoSuchElementException(arg);
		}
		return result;
	}

}
