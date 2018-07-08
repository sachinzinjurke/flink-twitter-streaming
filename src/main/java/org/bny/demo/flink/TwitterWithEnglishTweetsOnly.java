package org.bny.demo.flink;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.bny.demo.mapper.MapToTweet;
import org.bny.demo.model.Tweet;

public class TwitterWithEnglishTweetsOnly {



	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		final ParameterTool params = ParameterTool.fromArgs(args);
		DataStream<String> streamSource=null;
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		if (params.has(TwitterSource.CONSUMER_KEY) &&
				params.has(TwitterSource.CONSUMER_SECRET) &&
				params.has(TwitterSource.TOKEN) &&
				params.has(TwitterSource.TOKEN_SECRET)
				) {
			 streamSource = env.addSource(new TwitterSource(params.getProperties()));
		} else {
			System.out.println("Twitter credentials not provided!!");
			System.exit(0);
		}
		
		streamSource
				.map(new MapToTweet())
				.filter(new FilterFunction<Tweet>() {
					@Override
					public boolean filter(Tweet tweet) throws Exception {
						return tweet.getLang().equals("en");
					}
				})
				.print();
		env.execute("Twitter Streaming Example");
		
		
	}



}
