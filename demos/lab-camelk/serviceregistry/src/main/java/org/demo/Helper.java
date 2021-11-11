package org.demo;

import org.apache.camel.Exchange;
import java.io.*;
import java.util.*;
// import java.util.Random;
// import java.util.HashMap;
import java.nio.file.*;

import org.demo.camelk.*;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

public class Helper {
	
	int counter = 0;

	HashMap clients = new HashMap();

	Map registry = null;
	HashMap<String, String> integrations = new HashMap<String, String>();

	//test response
	String ack = "{\n    \"status\":\"ack\"\n}";


	//sets ACK response
	public void setRegistry(Map registry) throws Exception
	{
		this.registry = registry;

		System.out.println("Registry: "+registry.get("services"));
	}

	//registers a new service, given its metadata and Camel-K code
	//if the given service already exists, it gets updated.
	public void addService(Map service, String content)
	{
		//ID of service to add
		String addServiceId = (String)service.get("name");

		List services = ((List)registry.get("services"));

		//iterator
		Iterator<Map> iterator = services.iterator();

		//we check if it already exists, if it does, we remove it (the old version)
		while(iterator.hasNext()) {
			String serviceId = (String)iterator.next().get("name");

			if(addServiceId.equals(serviceId))
			{
				System.out.println("Found a service with same ID");
				iterator.remove();
			}
		}

		//the new service is added (or updated if already existed)
		((List)registry.get("services")).add(service);
		integrations.put(addServiceId, content);
	}

	public Map getRegistry()
	{
		return registry;
	}

	//sets ACK response
	public void setAck(Exchange exchange)
	{
		exchange.getIn().setBody(ack);
	}

	public String isLast(Exchange exchange)
	{
		counter++;

		if(counter == 5)
		{
			counter = 0;
			return "true";
		}

		return "false";
	}

	//Creates a Camel-K Integration CustomResource
	public Integration setIntegration(Exchange exchange)
	{
		String userId  = exchange.getIn().getHeader("user-id", String.class);
		String service = exchange.getIn().getHeader("service", String.class);

		String code = exchange.getProperty("integration.code", String.class);
		Integration it = exchange.getIn().getBody(Integration.class);

		it.getMetadata().setName("int-"+service+"-"+userId+"-id");
		it.getSpec().getSources().get(0).setContent(code);
		it.getSpec().getSources().get(0).setName(service+".xml");
		it.getSpec().getConfiguration().get(0).setValue("client.id="+userId);

		// Configuration config = new Configuration();
		// config.setType("property");
		// config.setValue("kafka.topic=bruno-test");
		// it.getSpec().getConfiguration().add(config);

		System.out.println("Integration is: "+it.toString());

		return it;
	}

	//loads the default Camel-K integrations from the classpath
	public String loadIntegration(String id) throws Exception
	{
		InputStream is = new ClassPathResource("integrations/"+id+".xml").getInputStream();

		return org.apache.commons.io.IOUtils.toString(is, "utf-8");
	}

	//Loads integrations (Camel-K code) and caches in memory
	public void loadIntegrations() throws Exception
	{
		List services = ((List)registry.get("services"));

		Iterator<Map> iterator = services.iterator();
		while(iterator.hasNext()) {
			String serviceId = (String)iterator.next().get("name");
			integrations.put(serviceId, loadIntegration(serviceId));
		}
	}

	//gets a cached Camel-K code
	public String getIntegration(String id) throws Exception
	{
		return integrations.get(id);
	}


	public void printIntegrations() throws Exception
	{
		for (Map.Entry<String, String> entry : integrations.entrySet()) {
		    System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}

}
