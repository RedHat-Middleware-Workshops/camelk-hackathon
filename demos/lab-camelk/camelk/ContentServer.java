import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.*;
import java.util.HashMap;


public class ContentServer extends RouteBuilder {


    // <route id="stream-response">
    //   <from uri="direct:stream-response"/>
    //   <log message="stream response activated."/>
    //   <log message="Generating byte stream to send back."/>
    //   <bean ref="helper" method="subscriptionStream"/>
    //   <wireTap uri="direct:start-stream"/>
    // </route>

	HashMap clients = new HashMap();


  @Override
  public void configure() throws Exception {
	  from("undertow:http://0.0.0.0:8080/currency/gbp-eur?useStreaming=true")
	  	// .setBody().simple("hello");

		// .process(new Processor() {
		// 	        public void process(Exchange exchange) throws Exception {
		// 	            String headers = exchange.getIn().getHeader("kafka.HEADERS",Object.class);
		// 	           System.out.println("class: "+headers.getClass().getName());
		// 	       }
  //   			});


		.process(new Processor() {
			        public void process(Exchange exchange) throws Exception {

						PipedOutputStream pos = new PipedOutputStream();
						PipedInputStream pis = new PipedInputStream(pos);

						//sets the input stream as a response
						exchange.getIn().setBody(pis);

						clients.put("client1",pos);

			       }
    			});

		// .to("controlbus:route?routeId=streamer&action=start");


	  from("timer:sim").id("streamer")
	  			// .noAutoStartup()
				.process(new Processor() {
			        public void process(Exchange exchange) throws Exception {
						// PipedInputStream pis = new PipedInputStream();
						// exchange.getIn().setBody(pis);
						System.out.println("Sim running...");

						PipedOutputStream pos = (PipedOutputStream)clients.get("client1");

						if(pos == null)
							System.out.println("pos null");
						else
						{
							System.out.println("pos available");
							pos.write(("test\n").getBytes());
							pos.flush();
							// pos.close();
							// clients.remove("client1");
						}	

			       }
    			});

  }
}
