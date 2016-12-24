package com.hjh.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

public class Response implements ServletResponse{

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
			File file = new File(Constant.WEB_ROOT, request.getUri());
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

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(output, true);
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub
		
	}
}
