package com.hjh;

import com.hjh.connector.http.HttpRequest;
import com.hjh.connector.http.HttpResponse;

public class StaticResourceProcessor {
	
	public void process(HttpRequest request, HttpResponse response) {
		response.sendStaticResource();
	}

}
