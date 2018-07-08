package org.bny.demo.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.bny.demo.model.HashTagTweet;

public class MapToHashTagTweet implements MapFunction<String,HashTagTweet>{

	ObjectMapper jsonMapper=new ObjectMapper();
	@Override
	public HashTagTweet map(String text) throws Exception {
		JsonNode node=jsonMapper.readTree(text);
		JsonNode tweetText=node.get("text");
		JsonNode tweetLang=node.get("lang");
		JsonNode tweetEntities=node.get("entities");
		List<String>tweetHashTagList=new ArrayList<>();
		String txt=tweetText==null?"":tweetText.textValue();
		String lang=tweetLang==null?"":tweetLang.textValue();
		if(tweetEntities!=null){
			
			JsonNode tweetHashTags=tweetEntities.get("hashtags");
			if(tweetHashTags!=null){
				//System.out.println("*********&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
				for (JsonNode jsonNode : tweetHashTags) {
					String hashTag=jsonNode.get("text").textValue();
				////	System.out.println("hashTag" + hashTag);
					if(hashTag!=null){
						tweetHashTagList.add(hashTag);
					}
				}
			}
			
		}
		
			return new HashTagTweet(txt,lang ,tweetHashTagList);

	}

	

}
