package com.hjh.server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;

public class ServletProcessor {
	
	private Request request;
	private Response response;

	public ServletProcessor(Request request, Response response) {
		this.request = request;
		this.response = response;
	}

	public void process(){
		//获取url中servletName
		String urlName = request.getUri();
		String servletName = urlName.substring(urlName.lastIndexOf("/") + 1);
		System.out.println(servletName);
		
		
		//创建url加载器
		URLClassLoader loader = null;
		URLStreamHandler handler = null;
		try {
			URL[] urls = new URL[1];
			String reposident = (new URL("file",null, Constant.WEB_ROOT+File.separator)).toString();
			System.out.println(reposident);
			urls[0] = new URL(null, reposident, handler);
			loader = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//使用加载的类返回页面
		Class myClass = null;
		try {
			myClass = loader.loadClass("com.hjh.servlet."+servletName);
			Servlet servlet = (Servlet) myClass.newInstance();
			servlet.service(request, response);
			System.out.println("servlet has been invoked....");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
