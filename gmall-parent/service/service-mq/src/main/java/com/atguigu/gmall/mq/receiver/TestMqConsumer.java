package com.atguigu.gmall.mq.receiver;


import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestMqConsumer {


    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = "confirm.exchange",autoDelete = "false"),
            value = @Queue(value = "confirm.queue",autoDelete = "false"),
            key = {"confirm.routing"}
    ))
    public void consumeMessage(Channel channel, Message message, String str) throws IOException {

        String messageStr = message.getBody().toString();
        System.out.println(messageStr);
        System.out.println(str);
        System.out.println("消费消息");

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag,false);
    }

}
