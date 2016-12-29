package com.hjh;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.hjh.connector.http.Constants;
import com.hjh.connector.http.HttpRequest;
import com.hjh.connector.http.HttpRequestFacade;
import com.hjh.connector.http.HttpResponse;
import com.hjh.connector.http.HttpResponseFacade;

public class ServletProcessor {

	public void process(HttpRequest request, HttpResponse response) {
		// 获取url中servletName
		String urlName = request.getRequestURI();
		String servletName = urlName.substring(urlName.lastIndexOf("/") + 1);
		System.out.println(servletName);

		// 创建url加载器
		URLClassLoader loader = null;
		URLStreamHandler handler = null;
		try {
			URL[] urls = new URL[1];
			String reposident = (new URL("file", null,
					Constants.WEB_ROOT + File.separator)).toString();
			System.out.println(reposident);
			urls[0] = new URL(null, reposident, handler);
			loader = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// 使用加载的类返回页面
		Class myClass = null;
		try {
			myClass = loader.loadClass("com.hjh.servlet."+ servletName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		HttpRequestFacade requestFacade = new HttpRequestFacade(request);
		HttpResponseFacade responseFacade = new HttpResponseFacade(response);
		Servlet servlet = null;
		try {
			servlet = (Servlet) myClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		try {
			servlet.service(requestFacade, responseFacade);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("servlet has been invoked....");

	}

}
