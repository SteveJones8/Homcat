package com.hjh.server;

public class StaticResourceProcessor {
	
	Response response;

	public StaticResourceProcessor(Response response) {
		this.response = response;
	}

	public void send() {
		response.sendStaticResource();
	}

}
