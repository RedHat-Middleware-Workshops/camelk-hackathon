package org.demo;

import org.apache.camel.Exchange;
import java.io.*;
import java.util.Random;
import java.util.HashMap;

public class Helper {
	
	int counter = 0;

	HashMap clients = new HashMap();


	//test response
	String ack = "{\n    \"status\":\"ack\"\n}";

	//sets ACK response
	public void setAck(Exchange exchange)
	{
		exchange.getIn().setBody(ack);
	}

	public String isLast(Exchange exchange)
	{
		counter++;

		if(counter == 55)
		{
			counter = 0;
			return "true";
		}

		return "false";
	}

	public void subscribe(Exchange exchange) throws Exception
	{
		// String clientId = exchange.getIn().getHeader("user-id",String.class);
		String clientId = exchange.getProperty("user-id",String.class);

		PipedOutputStream pos = (PipedOutputStream)clients.get(clientId);

		if(pos == null)
		{
			System.out.println("activating user ["+clientId+"]");

			PipedOutputStream newpos = new PipedOutputStream();
			PipedInputStream pis = new PipedInputStream(newpos);

			//sets the input stream as a response
			exchange.getIn().setBody(pis);

			clients.put(clientId, newpos);

			newpos.write(("Activating stream, please wait...\n").getBytes());
			newpos.flush();
		}
		else
		{
			exchange.getIn().setBody("user ["+clientId+"] already active\n");
			// System.out.println("pos available");
			// pos.write(("test\n").getBytes());
			// pos.flush();
			// pos.close();
			// clients.remove("client1");
		}	

	}


	public void streamPrices(Exchange exchange) throws Exception
	{
		String clientId = exchange.getIn().getHeader("id",String.class);
		boolean last = exchange.getIn().getHeader("last",Boolean.class);
		// String payload = exchange.getIn().getHeader("payload",String.class);
		String payload = exchange.getIn().getBody(String.class);


		PipedOutputStream pos = (PipedOutputStream)clients.get(clientId);

		if(pos == null)
			System.out.println("stream not found for ["+clientId+"]");
		else
		{
			try
			{
				System.out.println("sending data: "+payload);
				pos.write((payload+"\n").getBytes());
				pos.flush();

				if(last)
				{
					System.out.println("closing stream...");

					pos.write(("End of stream, thank you for using this service.\n").getBytes());
					pos.flush();
					pos.close();
					clients.remove(clientId);
				}
			}
			catch(java.io.IOException e)
			{
				System.out.println("It appears stream was closed, removing cache entry...");
				clients.remove(clientId);
				// e.printStackTrace();
				// ?throw e;
			}
		}	

	}

}
