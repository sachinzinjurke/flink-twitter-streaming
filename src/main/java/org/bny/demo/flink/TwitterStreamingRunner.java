package org.bny.demo.flink;

import java.util.Properties;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.bny.demo.mapper.SelectEnglishAndTokenizeFlatMap;


//[--twitter-source.consumerKey &lt;key&gt; --twitter-source.consumerSecret &lt;secret&gt; --twitter-source.token &lt;token&gt; --twitter-source.tokenSecret &lt;tokenSecret&gt;]</code><br>

public class TwitterStreamingRunner {

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
		
		DataStream<Tuple2<String, Integer>> tweets = streamSource
				// selecting English tweets and splitting to (word, 1)
				.flatMap(new SelectEnglishAndTokenizeFlatMap())
				// group by words and sum their occurrences
				.keyBy(0).sum(1);
		
		tweets.print();
		env.execute("Twitter Streaming Example");
		
		
	}

}
