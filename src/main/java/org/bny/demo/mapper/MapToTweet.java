package org.bny.demo.mapper;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.bny.demo.model.Tweet;

public class MapToTweet implements MapFunction<String,Tweet>{

	ObjectMapper jsonMapper=new ObjectMapper();
	@Override
	public Tweet map(String text) throws Exception {
		JsonNode node=jsonMapper.readTree(text);
		JsonNode tweetText=node.get("text");
		JsonNode tweetLang=node.get("lang");
		
		String txt=tweetText==null?"":tweetText.textValue();
		String lang=tweetLang==null?"":tweetLang.textValue();
		return new Tweet(txt, lang);
	}

	

}
