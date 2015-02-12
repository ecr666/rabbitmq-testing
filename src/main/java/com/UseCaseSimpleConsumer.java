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

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Created by eranda on 2/9/15.
 */
public class UseCaseSimpleConsumer {
	private final static String QUEUE_NAME = "queue1";
	public static void main(String[] argv)
			throws java.io.IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setPort(5672);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		try{
			channel.exchangeDeclarePassive("exchange1");
		}
		catch(java.io.IOException e){
			channel.exchangeDeclare("exchange1","direct");
		}

		String param="IBM";
		String msg=
				"<m:placeOrder xmlns:m=\"http://services.samples\">\n" +
				"    <m:order>\n" +
				"        <m:price>" + getRandom(100, 0.9, true) + "</m:price>\n" +
				"        <m:quantity>" + (int) getRandom(10000, 1.0, true) + "</m:quantity>\n" +
				"        <m:symbol>" + param + "</m:symbol>\n" +
				"    </m:order>\n" +
				"</m:placeOrder>";

		channel.basicPublish("exchange1", QUEUE_NAME , new AMQP.BasicProperties.Builder().contentType("text/plain").build(), msg.getBytes());
		System.out.println(" [x] Sent '" + msg + "'");
		channel.close();
		connection.close();
	}

	private static double getRandom(double base, double varience, boolean onlypositive) {
		double rand = Math.random();
		return (base + ((rand > 0.5 ? 1 : -1) * varience * base * rand))
		       * (onlypositive ? 1 : (rand > 0.5 ? 1 : -1));
	}
}
