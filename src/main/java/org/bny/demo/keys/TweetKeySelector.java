package org.bny.demo.keys;

import org.apache.flink.api.java.functions.KeySelector;
import org.bny.demo.model.Tweet;

public class TweetKeySelector implements KeySelector<Tweet, String>{

	@Override
	public String getKey(Tweet tweet) throws Exception {
		return tweet.getLang();
	}

}
