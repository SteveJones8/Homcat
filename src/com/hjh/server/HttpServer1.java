package com.hjh.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpServer1 {

	public static final String WEB_ROOT = "F:" + File.separator + "webroot";
	
	public static final String SHUTDOWN_COMMAND = "SHUTDOWN";
	
	public boolean shutdown = false;
	
	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("server stop");
			System.exit(1);
		}
		
		while(!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			
			try {
				socket = serverSocket.accept();
				
				input = socket.getInputStream();
				Request request = new Request(input);
				request.parse();
				
				output = socket.getOutputStream();
				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();
				
				socket.close();
				
				if(request.getUri().equals(SHUTDOWN_COMMAND)) {
					shutdown = true;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public static void main(String[] args) {
		HttpServer1 server = new HttpServer1();
		System.out.println("server start");
		server.await();
	}
}
