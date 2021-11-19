// camel-k: language=java
// camel-k: property=period=5000

import org.apache.camel.builder.RouteBuilder;

public class AMQTargetConsumer extends RouteBuilder {
  
  @Override
  public void configure() throws Exception {

    from("amqp:{{jms.destinationType}}:{{jms.destinationName}}?username={{jms.username}}&password={{jms.password}}")
    .to("log:info");
  }
}