package org.cboard.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.annotation.Resource;
import javax.jms.*;


/**
 * Created by ok_vince on 2018-04-22.
 */
public class ProducerServiceImpl implements  ProducerService {

    @Autowired
    JmsTemplate jmsTemplate;

    @Resource(name="queueDestination")
    Destination destination;
    @Override
    public void sendMessage(String message) {
    jmsTemplate.send(destination, new MessageCreator() {
        @Override
        public Message createMessage(Session session) throws JMSException {
            TextMessage textMessage=session.createTextMessage("Hello Activemq："+message);
            System.out.print("发布成功！！");
            return textMessage;
        }
    });
    }
}
