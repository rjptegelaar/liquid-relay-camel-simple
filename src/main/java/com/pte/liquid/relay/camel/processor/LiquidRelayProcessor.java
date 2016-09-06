//Copyright 2014 Paul Tegelaar
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package com.pte.liquid.relay.camel.processor;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.pte.liquid.relay.Converter;
import com.pte.liquid.relay.Marshaller;
import com.pte.liquid.relay.Transport;
import com.pte.liquid.relay.camel.converter.LiquidRelayExchangeConverterImpl;
import com.pte.liquid.relay.camel.util.LiquidRelayCamelUtil;
import com.pte.liquid.relay.client.stomp.StompTransport;
import com.pte.liquid.relay.marshaller.json.JsonMarshaller;
import com.pte.liquid.relay.model.Message;

public class LiquidRelayProcessor implements Processor{
	private static final transient Logger logger = Logger.getLogger("LiquidRelayProcessor");
	private long count = 0;
	
    private Transport transport;
	private Converter<Exchange> converter;
	private Marshaller marshaller;
		
    private boolean enabled;

    private static LiquidRelayProcessor liquidRelayProcessor = null;
    
    public synchronized static LiquidRelayProcessor getInstance(boolean enabled, String destination, String hostname, int port) {
    	   if(liquidRelayProcessor == null) {
    		   liquidRelayProcessor = new LiquidRelayProcessor(enabled, destination, hostname, port);
    	   }
    	   logger.info("Created LiquidRelayProcessor, enabled: " + enabled + ", destination: " + destination + ", hostname: " + hostname + ", port: " + port);
    	   return liquidRelayProcessor;
    	}
	
    protected LiquidRelayProcessor(boolean enabled, String destination, String hostname, int port){    	
    	this.enabled = enabled;
    	
    	marshaller = new JsonMarshaller();
    	
    	 Properties properties = new Properties();
    	
        if(destination!=null){
        	properties.put("relay_destination", destination);
        }
        if(hostname!=null){
        	properties.put("relay_stomp_hostname", hostname);
        }
        if(port>0){
        	properties.put("relay_stomp_port", port);
        }
    	
    	transport = new StompTransport();
    	transport.setProperties(properties);
    	transport.setMarshaller(marshaller);
    	converter = new LiquidRelayExchangeConverterImpl();
    	
    }
   
	@Override
	public void process(Exchange exchange) throws Exception {
		try{
    		if(enabled){
    			    
    			count++;
    			if((count%1000)==0){
    				logger.info("Sent " + count + " messages");
    			}
    			
	        	Message preMsg = converter.convert(exchange);  
	        	String correlationID = LiquidRelayCamelUtil.determineCorrelation(exchange);
	        	String parentId = LiquidRelayCamelUtil.determineParent(exchange);
	        	int order = LiquidRelayCamelUtil.determineOrder(exchange);
	        	String messageID = preMsg.getId();
	        	
	        	LiquidRelayCamelUtil.setCorrelationID(correlationID, exchange);
	        	preMsg.setCorrelationID(correlationID);
	        	
	        	LiquidRelayCamelUtil.setParentID(messageID, exchange);
	        	preMsg.setParentID(parentId);
	        	
	        	LiquidRelayCamelUtil.setOrder(order, exchange);
	        	preMsg.setOrder(order);	
	        	
	        	   	    	    	    	  	   
	        	transport.send(preMsg);
    		}else{
    			if(logger.isLoggable(Level.FINEST)){
    				logger.finest("Skipping message because liquid is disabled");
    			}
    		}
    	} catch (Exception e) {
			//Empty by design
		}
		
	}

	public long getCount() {
		return count;
	}


}
