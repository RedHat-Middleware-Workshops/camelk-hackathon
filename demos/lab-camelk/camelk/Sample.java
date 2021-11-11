import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class Sample extends RouteBuilder {

  @Override
  public void configure() throws Exception {
	  from("kafka:bruno-test?brokers=172.30.231.119:9092&seekTo=beginning")

		.process(new Processor() {
		        public void process(Exchange exchange) throws Exception {
		            String headers = exchange.getIn().getHeader("kafka.HEADERS",Object.class);
		            // do something with the payload and/or exchange here
		           // exchange.getIn().setBody("Changed body");
		           System.out.println("class: "+headers.getClass().getName());
		       }
    });

      // .log("kafka headers: ${header.kafka.HEADERS.class.name}");
  }
}
