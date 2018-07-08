package org.bny.demo.model;

public class Tweet {
	private String text;
	private String lang;
	public Tweet(String text, String lang) {
		super();
		this.text = text;
		this.lang = lang;
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
	@Override
	public String toString() {
		return "Tweet [text=" + text + ", lang=" + lang + "]";
	}
	
	

}
