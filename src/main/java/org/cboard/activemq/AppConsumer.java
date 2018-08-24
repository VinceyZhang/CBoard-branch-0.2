package org.cboard.activemq;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by ok_vince on 2018-04-22.
 */
public class AppConsumer {
  public static void main(String args[]){
      ApplicationContext context=new ClassPathXmlApplicationContext("spring-activemq.xml");

  }
}
