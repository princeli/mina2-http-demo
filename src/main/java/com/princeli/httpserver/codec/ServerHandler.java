/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.princeli.httpserver.codec;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

 

 
/**
 * An {@link IoHandler} for HTTP.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev$, $Date$
 */
public class ServerHandler extends IoHandlerAdapter {
	
	private HttpHandler handler;

	public HttpHandler getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}
	
    @Override
    public void sessionOpened(IoSession session) {
        // set idle time to 60 seconds
    	session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        // Check that we can service the request context
    	HttpRequestMessage request = (HttpRequestMessage) message;
    	HttpResponseMessage response = handler.handle(request);
    	
		// HttpResponseMessage response = new HttpResponseMessage();
		// response.setContentType("text/plain");
		// response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		// response.appendBody("CONNECTED");

        // msg.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
        // byte[] b = new byte[ta.buffer.limit()];
        // ((ByteBuffer)ta.buffer.rewind()).get(b);
        // msg.appendBody(b);
        // System.out.println("####################");
        // System.out.println("  GET_TILE RESPONSE SENT - ATTACHMENT GOOD DIAMOND.SI="+d.si+
        // ", "+new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSS").format(new java.util.Date()));
        // System.out.println("#################### - status="+ta.state+", index="+message.getIndex());

        //// Unknown request
        // response = new HttpResponseMessage();
        // response.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
        // response.appendBody(String.format(
        // "<html><body><h1>UNKNOWN REQUEST %d</h1></body></html>",
        // HttpResponseMessage.HTTP_STATUS_NOT_FOUND));

		if (response != null) {
			session.write(response).addListener(IoFutureListener.CLOSE);
		}
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
    	session.close(false);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
    	session.close(false);
    }
}
