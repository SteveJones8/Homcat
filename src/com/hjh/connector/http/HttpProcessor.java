package com.hjh.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import com.hjh.ServletProcessor;
import com.hjh.StaticResourceProcessor;
import com.hjh.util.RequestUtil;
import com.hjh.util.StringManager;

public class HttpProcessor {

	HttpConnector connector;
	HttpRequest request;
	HttpResponse response;
	private HttpRequestLine requestLine = new HttpRequestLine();
	protected StringManager sm = StringManager
			.getManager(Constants.Package);

	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}

	public void process(Socket socket) throws IOException, ServletException {

		SocketInputStream input = null;
		OutputStream output = null;

		input = new SocketInputStream(socket.getInputStream(), 2048);
		output = socket.getOutputStream();

		request = new HttpRequest(input);
		response = new HttpResponse(output);
		response.setRequest(request);

		parseRequest(input, output);
		parseHeaders(input);

		if (request.getRequestURI().contains("/servlet/")) {
			ServletProcessor processor = new ServletProcessor();
			processor.process(request, response);
		} else {
			StaticResourceProcessor processor = new StaticResourceProcessor();
			processor.process(request, response);
		}

		socket.close();
	}

	private void parseHeaders(SocketInputStream input) throws IOException,
			ServletException {
		while (true) {
			HttpHeader header = new HttpHeader();

			// Read the next header
			input.readHeader(header);
			if (header.nameEnd == 0) {
				if (header.valueEnd == 0) {
					return;
				} else {
					throw new ServletException(
							sm.getString("httpProcessor.parseHeaders.colon"));
				}
			}

			String name = new String(header.name, 0, header.nameEnd);
			String value = new String(header.value, 0,
					header.valueEnd);
			request.addHeader(name, value);
			// do something for some headers, ignore others.
			if (name.equals("cookie")) {
				Cookie cookies[] = RequestUtil
						.parseCookieHeader(value);
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals(
							"jsessionid")) {
						// Override anything requested
						// in the URL
						if (!request.isRequestedSessionIdFromCookie()) {
							// Accept only the first
							// session id cookie
							request.setRequestedSessionId(cookies[i]
									.getValue());
							request.setRequestedSessionCookie(true);
							request.setRequestedSessionURL(false);
						}
					}
					request.addCookie(cookies[i]);
				}
			} else if (name.equals("content-length")) {
				int n = -1;
				try {
					n = Integer.parseInt(value);
				} catch (Exception e) {
					throw new ServletException(
							sm.getString("httpProcessor.parseHeaders.contentLength"));
				}
				request.setContentLength(n);
			} else if (name.equals("content-type")) {
				request.setContentType(value);
			}
		} // end while
	}

	private void parseRequest(SocketInputStream input, OutputStream output)
			throws IOException, ServletException {

		input.readRequestLine(requestLine);
		String method = new String(requestLine.method, 0,
				requestLine.methodEnd);
		String uri = null;
		String protocol = new String(requestLine.protocol, 0,
				requestLine.protocolEnd);

		if (method.length() < 1) {
			throw new ServletException(
					"Missing HTTP request method");
		} else if (requestLine.uriEnd < 1) {
			throw new ServletException("Missing HTTP request URI");
		}

		// 问号的位置
		int question = requestLine.indexOf("?");
		if (question > 0) {
			request.setQueryString(new String(requestLine.uri,
					question + 1, requestLine.uriEnd
							- question - 1));
			uri = new String(requestLine.uri, 0, question);
		} else {
			request.setQueryString(null);
			uri = new String(requestLine.uri, 0, requestLine.uriEnd);
		}
		
		System.out.println("--------------------------------------"+uri+"----------------------------------");

		// 若uri带http标识，去除http标识
		if (!uri.startsWith("/")) {
			int pos = uri.indexOf("://");

			if (pos != 0) {
				pos = uri.indexOf("/", pos + 3);
				if (pos == 0) {
					uri = "";
				} else {
					uri = uri.substring(pos + 3);
				}
			}
		}

		String match = ";jsessionid=";
		int semicolon = uri.indexOf(match);
		if (semicolon >= 0) {
			String rest = uri.substring(semicolon + match.length());
			int semicolon2 = rest.indexOf(';');
			if (semicolon2 >= 0) {
				request.setRequestedSessionId(rest.substring(0,
						semicolon2));
				rest = rest.substring(semicolon2);
			} else {
				request.setRequestedSessionId(rest);
				rest = "";
			}
			request.setRequestedSessionURL(true);
			uri = uri.substring(0, semicolon) + rest;
		} else {
			request.setRequestedSessionId(null);
			request.setRequestedSessionURL(false);
		}

		// Normalize URI (using String operations at the moment)
		String normalizedUri = normalize(uri);

		// Set the corresponding request properties
		((HttpRequest) request).setMethod(method);
		request.setProtocol(protocol);
		if (normalizedUri != null) {
			((HttpRequest) request).setRequestURI(normalizedUri);
		} else {
			((HttpRequest) request).setRequestURI(uri);
		}

		if (normalizedUri == null) {
			throw new ServletException("Invalid URI: " + uri + "'");
		}

	}

	protected String normalize(String path) {
		if (path == null)
			return null;
		// Create a place for the normalized path
		String normalized = path;

		// Normalize "/%7E" and "/%7e" at the beginning to "/~"
		if (normalized.startsWith("/%7E")
				|| normalized.startsWith("/%7e"))
			normalized = "/~" + normalized.substring(4);

		// Prevent encoding '%', '/', '.' and '\', which are special
		// reserved
		// characters
		if ((normalized.indexOf("%25") >= 0) // %
				|| (normalized.indexOf("%2F") >= 0) // /
				|| (normalized.indexOf("%2E") >= 0) // .
				|| (normalized.indexOf("%5C") >= 0) // \
				|| (normalized.indexOf("%2f") >= 0)
				|| (normalized.indexOf("%2e") >= 0)
				|| (normalized.indexOf("%5c") >= 0)) {
			return null;
		}

		if (normalized.equals("/."))
			return "/";

		// Normalize the slashes and add leading slash if necessary
		if (normalized.indexOf('\\') >= 0)
			normalized = normalized.replace('\\', '/');
		if (!normalized.startsWith("/"))
			normalized = "/" + normalized;

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index)
					+ normalized.substring(index + 1);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index)
					+ normalized.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0)
				break;
			if (index == 0)
				return (null); // Trying to go outside our
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2)
					+ normalized.substring(index + 3);
		}

		if (normalized.indexOf("/...") >= 0)
			return (null);

		return (normalized);

	}

}
