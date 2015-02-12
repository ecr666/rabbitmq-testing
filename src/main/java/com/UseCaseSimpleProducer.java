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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Created by eranda on 2/9/15.
 */
public class UseCaseSimpleProducer {
	private final static String QUEUE_NAME = "queue2";
	private final static String EXCHANGE_NAME = "exchange2";

	public static void main(String[] argv)
			throws java.io.IOException,
			       InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		try{
			channel.exchangeDeclarePassive(EXCHANGE_NAME);
		}
		catch(java.io.IOException e){
			channel.exchangeDeclare(EXCHANGE_NAME,"direct");
		}

		try{
			channel.queueDeclarePassive(QUEUE_NAME);
		}
		catch(java.io.IOException e){
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		}
		channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,QUEUE_NAME);

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(QUEUE_NAME, true, consumer);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			System.out.println(" [x] Received '" + message + "'");
		}
	}
}
