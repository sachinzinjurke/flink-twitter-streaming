package org.bny.demo.flink;

import java.util.Date;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.apache.flink.util.Collector;
import org.bny.demo.keys.TweetKeySelector;
import org.bny.demo.mapper.MapToTweet;
import org.bny.demo.model.Tweet;

public class TwitterWithWindowPerLanguage {



	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		final ParameterTool params = ParameterTool.fromArgs(args);
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		DataStream<String> streamSource=null;
		//Setting time of tweet to ingestion i.e. when we have tweet available in flink
		
		env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);
		
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
				.keyBy(new TweetKeySelector())
				.timeWindow(Time.seconds(10))
				.apply(new WindowFunction<Tweet,Tuple3<String,Long,Date>, String, TimeWindow>() {

					@Override
					public void apply(String key, TimeWindow timeWindow,
							Iterable<Tweet> tweets,
							Collector<Tuple3<String, Long, Date>> collector)
							throws Exception {
						long count=0;
						for (Tweet tweet : tweets) {
							count++;
						}
						System.out.println("**********************");
						System.out.println("Lang :: " + key + " , Count ::  "+count);
						System.out.println("**********************");
						collector.collect(new Tuple3<>(key,count,new Date(timeWindow.getEnd())));
					}
					
				})
				.print();
		env.execute("Twitter Streaming Example");
		
		
	}



}
