package com.princeli.httpserver.codec;

import java.io.IOException;
 

public class Main {
	public static void main(String[] args) throws IOException,
			InterruptedException {
		Server server = new Server();
		server.setEncoding("UTF-8");
		server.setHttpHandler(new HttpHandler() {
			public HttpResponseMessage handle(HttpRequestMessage request) {
				HttpResponseMessage response = new HttpResponseMessage();
				response.setContentType("text/plain");
				response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
				response.appendBody("CONNECTED\n");
				return response;
			}
		});
		server.run();

		Thread.sleep(10000);
		// server.stop();
	}
}
