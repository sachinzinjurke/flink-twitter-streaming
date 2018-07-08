package org.bny.demo.mapper;

import java.util.StringTokenizer;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.util.Collector;

public class SelectEnglishAndTokenizeFlatMap implements FlatMapFunction<String, Tuple2<String, Integer>>{

	private transient ObjectMapper jsonParser;
	@Override
	public void flatMap(String value, Collector<Tuple2<String, Integer>> collector)
			throws Exception {

		if (jsonParser == null) {
			jsonParser = new ObjectMapper();
		}
		JsonNode jsonNode = jsonParser.readValue(value, JsonNode.class);
		boolean isEnglish = jsonNode.has("user") && jsonNode.get("user").has("lang") && jsonNode.get("user").get("lang").asText().equals("en");
		boolean hasText = jsonNode.has("text");
		if (isEnglish && hasText) {
			// message of tweet
			StringTokenizer tokenizer = new StringTokenizer(jsonNode.get("text").asText());

			// split the message
			while (tokenizer.hasMoreTokens()) {
				String result = tokenizer.nextToken().replaceAll("\\s*", "").toLowerCase();

				if (!result.equals("")) {
					collector.collect(new Tuple2<>(result, 1));
				}
			}
		}
		
	}

}
