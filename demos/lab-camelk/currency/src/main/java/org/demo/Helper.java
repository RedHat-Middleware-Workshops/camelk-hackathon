package org.demo;

import org.apache.camel.Exchange;
import java.io.*;
import java.util.Random;

public class Helper {
	
	//test response
	String ack = "{\n    \"status\":\"ack\"\n}";

	//sets ACK response
	public void setAck(Exchange exchange)
	{
		exchange.getIn().setBody(ack);
	}


	//prepares byte stream of requested size (in MegaBytes)
	// public void prepareStream(int size, Exchange exchange) throws Exception
	public void prepareStream(Exchange exchange) throws Exception
	{
		//prepares in/out streams
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream(pos);

		//sets the input stream as a response
		exchange.getIn().setBody(pis);

		//trigger thread to generate data bytes
		// streamWriter sw = new streamWriter(pos, size);
		streamWriter sw = new streamWriter(pos, 1);
		sw.start();
	}

	//Helper thread to generate data bytes
	//The byte stream generated simulates documents of big size.
	//For simplicity the file generated looks like:
	//
	//   ---- start of document ----
	//   hello world stream
	//   ...................(N times)
	//   hello world stream
	//   ---- end of document ----
  	private class streamWriter extends Thread {

  		//variables
	  	PipedOutputStream pos;
	  	int size;

	  	public streamWriter(PipedOutputStream stream, int size)
	  	{
	  		pos = stream;
	  		this.size = size;
	  	}


	    public void run()
	    {
	    	//main body of the file generated
	    	byte[] response = "hello world stream\n".getBytes();

	    	//helper variables
			long kb = 1024;
			long mb = 1024*kb;
			long gb = 1024*mb;

			// long limit = 4*gb;
			long limit = size*mb;
			long count = 0;

			Random r = new Random();
			int min = 1000;
			int max = 1200;

			// System.out.println("count = "+count+", limit = "+limit);

			try
			{
				//header of document
				// pos.write("---- start of document ----\n".getBytes());
				// pos.flush();

				//body of document
				while(count < limit)
				{
					// pos.write(response);
					// pos.flush();
					// count+=response.length;


					// int randomNum = 1000 + (int)(Math.random() * 1200);
					int randomNum = r.nextInt((max - min) + 1) + min;
	


					byte[] gbpEur = ("1."+randomNum+"\n").getBytes();

					pos.write(gbpEur);
					pos.flush();
					count+=gbpEur.length;

					Thread.sleep(1000l);
				}

				//tail of document
				pos.write("---- end of document ----\n".getBytes());
				pos.flush();

				//once done, we close the stream
				pos.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			System.out.println("stream completed.");
	    }
	}


}
