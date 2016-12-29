package com.hjh.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpConnector implements Runnable{
	
	//连接器是否已停止
	private boolean stopped = false;
	
	private String scheme = "http";
	
	public String getScheme() {
		return scheme;
	}

	@Override
	public void run() {
		
		int port = 8080;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(!stopped) {
			
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				continue;
			}
			
			HttpProcessor processor = new HttpProcessor(this);
			try {
				processor.process(socket);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	
}
