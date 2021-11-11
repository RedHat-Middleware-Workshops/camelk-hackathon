package org.demo;
import java.util.*;
import org.apache.camel.Exchange;
public class Helper {
    public Map getMappingInputs(Exchange exchange)
    {
        HashMap<String,Object> inputs = new HashMap<String,Object>();
        inputs.put("customer",   exchange.getProperty("atlasmap-source-1"));
        inputs.put("subscription", exchange.getProperty("atlasmap-source-2"));
        inputs.put("offer",        exchange.getProperty("atlasmap-source-3"));
        return inputs;
    }
}