package org.bny.demo.model;

import java.util.List;

public class HashTagTweet {
	private String text;
	private String lang;
	private List<String>tags;
	public HashTagTweet(String text, String lang, List<String> tags) {
		super();
		this.text = text;
		this.lang = lang;
		this.tags = tags;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	@Override
	public String toString() {
		return "HashTagTweet [text=" + text + ", lang=" + lang + ", tags="
				+ tags + "]";
	}
	
	
}
