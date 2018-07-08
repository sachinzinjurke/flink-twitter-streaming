package org.bny.demo.flink;

import java.util.Date;
import java.util.List;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.apache.flink.util.Collector;
import org.bny.demo.mapper.MapToHashTagTweet;
import org.bny.demo.model.HashTagTweet;

public class TwitterWithTopHashTags {



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
				.map(new MapToHashTagTweet())
				.flatMap(new FlatMapFunction<HashTagTweet, Tuple2<String,Integer>>() {
					@Override
					public void flatMap(HashTagTweet hashTagTweet,
							Collector<Tuple2<String, Integer>> collector)
							throws Exception {
						List<String>hashTags=hashTagTweet.getTags();
						for (String tag : hashTags) {
							collector.collect(new Tuple2<>(tag,1));
						}
						
					}
					
				}).keyBy(0)
				.timeWindow(Time.minutes(2))
				.sum(1)
				.timeWindowAll(Time.minutes(2))
				.apply(new AllWindowFunction<Tuple2<String,Integer>, Tuple3<Date, String, Integer>, TimeWindow>() {

					@Override
					public void apply(TimeWindow timeWindow,
							Iterable<Tuple2<String, Integer>> iterable,
							Collector<Tuple3<Date, String, Integer>> collector)
							throws Exception {

						String topHashTag=null;
						int count=0;
						for (Tuple2<String,Integer> windowHashTag : iterable) {
							if(windowHashTag.f1 > count){
								topHashTag=windowHashTag.f0;
								count=windowHashTag.f1;
							}
						}
						collector.collect(new Tuple3<>(new Date(timeWindow.getEnd()),topHashTag,count));
					}
					
				})
				.print();
						
				
				env.execute("Twitter Streaming Example");
		
		
	}



}
