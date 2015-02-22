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

import java.io.IOException;
import com.rabbitmq.client.*;

public class DeadLetterReceiver {

	static String QUEUE_NAME = "callback-queue";
	static String HOSTNAME = "localhost";

	public static void main(String[] args) throws IOException,
	                                              ShutdownSignalException, ConsumerCancelledException,
	                                              InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOSTNAME);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		//creating reply queue
		try{
			channel.queueDeclarePassive(QUEUE_NAME);
		}
		catch(java.io.IOException e){
			if(!channel.isOpen())    channel=connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		}

		System.out.println(" [*] Waiting for responses. To exit press CTRL+C");
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(QUEUE_NAME, true, consumer);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			BasicProperties props = delivery.getProperties();
			String message = new String(delivery.getBody());
			System.out.println(" [x] Response received '" + message + "'");
			System.out.println("Correlation id : " + props.getCorrelationId());
		}
	}
}