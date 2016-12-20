package com.hjh.server;

import java.io.IOException;
import java.io.InputStream;

public class Request {
	
	private InputStream input;
	private String uri;

	public Request(InputStream input) {
		this.input = input;
	}

	//解析request
	public void parse() {
		StringBuilder request = new StringBuilder();
		int i;
		byte[] buffer = new byte[1024];
		
		try {
			i = input.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			i = -1;
		}
		
		for(int j=0; j<i; j++) {
			request.append((char)buffer[j]);
		}
		
		uri = parseUri(request.toString());
		
	}

	private String parseUri(String requestString) {
		int index1, index2;
		index1 = requestString.indexOf(' ');
		if(index1 != -1) {
			index2 = requestString.indexOf(' ', index1+1);
			if(index1<index2) {
				return requestString.substring(index1+1, index2);
			}
		}
		return null;
	}
	
	public String getUri() {
		return uri;
	}
}
