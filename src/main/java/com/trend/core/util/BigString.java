package com.trend.core.util;

public class BigString {

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public byte[] compress(){
		return ByteUtils.compress(content.getBytes());
	}

	@Override
	public String toString() {
		return content;
	}
	
	public BigString(String content){
		this.content=content;
	}
}
