package com.princeli.httpserver.codec;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.princeli.httpserver.codec.HttpServerProtocolCodecFactory;

public class Server {
	/** Default HTTP port */
	private static final int DEFAULT_PORT = 8080;
	private NioSocketAcceptor acceptor;
	private boolean isRunning;

	private String encoding;
	private HttpHandler httpHandler;

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
		//HttpRequestDecoder.defaultEncoding = encoding;
		//HttpResponseEncoder.defaultEncoding = encoding;
	}

	public HttpHandler getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(HttpHandler httpHandler) {
		this.httpHandler = httpHandler;
	}

	/**
	 * 启动HTTP服务端箭筒HTTP请求
	 * 
	 * @param port要监听的端口号
	 * @throws IOException
	 */
	public void run(int port) throws IOException {
		synchronized (this) {
			if (isRunning) {
				System.out.println("Server is already running.");
				return;
			}
			acceptor = new NioSocketAcceptor();
			acceptor.getFilterChain().addLast(
					"protocolFilter",
					new ProtocolCodecFilter(
							new HttpServerProtocolCodecFactory(encoding)));
			// acceptor.getFilterChain().addLast("logger", new LoggingFilter());
			ServerHandler handler = new ServerHandler();
			handler.setHandler(httpHandler);
			acceptor.setHandler(handler);
			acceptor.bind(new InetSocketAddress(port));
			isRunning = true;
			System.out.println("Server now listening on port " + port);
		}
	}

	/**
	 * 使用默认端口8080
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		run(DEFAULT_PORT);
	}

	/**
	 * 停止监听HTTP服务
	 */
	public void stop() {
		synchronized (this) {
			if (!isRunning) {
				System.out.println("Server is already stoped.");
				return;
			}
			isRunning = false;
			try {
				acceptor.unbind();
				acceptor.dispose();
				System.out.println("Server is stoped.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
