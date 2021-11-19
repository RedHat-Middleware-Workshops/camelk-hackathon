// camel-k: language=java
// camel-k: property=period=5000
// camel-k: dependency=mvn:com.github.javafaker:javafaker:1.0.2

import org.apache.camel.builder.RouteBuilder;
import com.github.javafaker.Faker;

public class AMQSourceProducer extends RouteBuilder {
  
  @Override
  public void configure() throws Exception {

    from("timer:foo?fixedRate=true&period={{period}}")
    .bean(this, "generateFakePerson()")
    .to("log:info")
    .to("amqp:{{jms.destinationType}}:{{jms.destinationName}}?username={{jms.username}}&password={{jms.password}}");
  }

  public String generateFakePerson() {
    Faker faker = new Faker();
    return faker.name().fullName() + " lives on " + faker.address().streetAddress();
  }

}