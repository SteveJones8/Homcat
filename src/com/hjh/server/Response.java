package com.hjh.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class Response {

	private final int BUFFER_SIZE = 1024;
	private OutputStream output;
	private Request request;
	private String ERROR_MSG = "HTTP/1.1 404 File Not Found \r\n"
			+ "Content-Type: text/html\r\n"
			+ "Content-Length: 23\r\n" + "\r\n"
			+ "<h1>File Not Found</h1>";

	public Response(OutputStream output) {
		this.output = output;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	// 发送web_root下的静态文件至客户端
	public void sendStaticResource() {
		byte[] bytes = new byte[1024];
		FileInputStream fis = null;
		
		try {
			File file = new File(HttpServer1.WEB_ROOT, request.getUri());
			System.out.println(file.getName());
			if(file.exists()) {
				fis = new FileInputStream(file);
				int len = 0;
				while( (len = fis.read(bytes, 0, BUFFER_SIZE)) != -1) {
					output.write(bytes, 0, len);
				}
			} else {
				output.write(ERROR_MSG.getBytes());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
}
