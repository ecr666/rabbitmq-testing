/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com;

import java.util.HashMap;
import java.util.Map;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class DeadLetterSender {

	private final static String QUEUE_NAME = "request-queue";
	private final static String EXCHANGE_NAME = "request-exchange";
	private final static String HOSTNAME = "localhost";
	private final static String REPLY_QUEUE_NAME = "callback-queue";

	public static void main(String[] argv) throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOSTNAME);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		try{
			channel.exchangeDeclarePassive(EXCHANGE_NAME);
		}
		catch(java.io.IOException e){
			if(!channel.isOpen())    channel=connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME,"direct",true);
		}

		try{
			channel.queueDeclarePassive(QUEUE_NAME);
		}
		catch(java.io.IOException e){
			if(!channel.isOpen())    channel=connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		}
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME);

		// Request Message
		String message = "<soapenv:Envelope"
		                 + " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
		                 + "<soapenv:Body>\n"
		                 + "  <p:greet xmlns:p=\"http://service.wso2.org\">\n"
		                 + "     <in>" + "IBM" + "</in>\n" + "  </p:greet>\n"
		                 + "</soapenv:Body>\n" + "</soapenv:Envelope>";

		// Adding Message Properties
		AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties()
				.builder();
		builder.messageId("007");
		builder.contentType("text/xml");
		builder.correlationId("1111");
		builder.replyTo(REPLY_QUEUE_NAME);
		builder.contentEncoding("UTF-8");

		// Custom user properties
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("SOAP_ACTION", "getQuote");
		builder.headers(headers);

		// Publish the message to exchange
		channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, builder.build(),message.getBytes());
		channel.close();
		connection.close();
	}
}

