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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map.Entry;

 


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * A {@link MessageEncoder} that encodes {@link HttpResponseMessage}.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev$, $Date$
 */
public class HttpResponseEncoder  implements MessageEncoder<HttpResponseMessage> {
	/**
	 * 字符编码
	 */
	private CharsetEncoder encoder;
	
	private static final byte[] CRLF = new byte[] { 0x0D, 0x0A };

	public static String defaultEncoding;

	public CharsetEncoder getEncoder() {
		return encoder;
	}

	public void setEncoder(CharsetEncoder encoder) {
		this.encoder = encoder;
	}

	public HttpResponseEncoder() {
		encoder = Charset.forName(defaultEncoding).newEncoder();
	}

	public void encode(IoSession session, HttpResponseMessage message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer buf = IoBuffer.allocate(256);
		// Enable auto-expand for easier encoding
		buf.setAutoExpand(true);

		try {
			// output all headers except the content length
			if (encoder == null) {
				encoder = Charset.defaultCharset().newEncoder();
			}
			buf.putString("HTTP/1.1 ", encoder);
			buf.putString(String.valueOf(message.getResponseCode()), encoder);
			switch (message.getResponseCode()) {
			case HttpResponseMessage.HTTP_STATUS_SUCCESS:
				buf.putString(" OK", encoder);
				break;
			case HttpResponseMessage.HTTP_STATUS_NOT_FOUND:
				buf.putString(" Not Found", encoder);
				break;
			}
			buf.put(CRLF);
			for (Entry<String, String> entry : message.getHeaders().entrySet()) {
				buf.putString(entry.getKey(), encoder);
				buf.putString(": ", encoder);
				buf.putString(entry.getValue(), encoder);
				buf.put(CRLF);
			}
			// now the content length is the body length
			buf.putString("Content-Length: ", encoder);
			buf.putString(String.valueOf(message.getBodyLength()), encoder);
			buf.put(CRLF);
			buf.put(CRLF);
			// add body
			buf.put(message.getBody());
			// System.out.println("\n+++++++");
			// for (int i=0; i<buf.position();i++)System.out.print(new
			// String(new byte[]{buf.get(i)}));
			// System.out.println("\n+++++++");
		} catch (CharacterCodingException ex) {
			ex.printStackTrace();
		}

		buf.flip();
		out.write(buf);

	}

 
}
