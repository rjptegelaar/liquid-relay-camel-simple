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
package com.pte.liquid.relay.camel.bean;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.camel.Exchange;

import com.pte.liquid.async.LiquidLegacyTransport;
import com.pte.liquid.relay.Converter;
import com.pte.liquid.relay.Marshaller;
import com.pte.liquid.relay.Transport;
import com.pte.liquid.relay.camel.converter.LiquidRelayExchangeConverterImpl;
import com.pte.liquid.relay.camel.util.LiquidRelayCamelUtil;
import com.pte.liquid.relay.client.stomp.StompTransport;
import com.pte.liquid.relay.marshaller.json.JsonMarshaller;
import com.pte.liquid.relay.model.Message;



public class LiquidRelayBean{
	
	private static final transient Logger logger = Logger.getLogger("LiquidRelayBean");
	private long count = 0;
	
    private Transport stompTransport;
    private Transport asyncTransport;
	private Converter<Exchange> converter;
	private Marshaller marshaller;
		
    private boolean enabled;

    private static LiquidRelayBean liquidRelayBean = null;
    
    public synchronized static LiquidRelayBean getInstance(boolean enabled, String destination, String hostname, int port) {
    	   if(liquidRelayBean == null) {
    		   liquidRelayBean = new LiquidRelayBean(enabled, destination, hostname, port);
    		   logger.info("Created LiquidRelayBean, enabled: " + enabled + ", destination: " + destination + ", hostname: " + hostname + ", port: " + port);
    	   }
    	   
    	   
    	   
    	   return liquidRelayBean;
    	  
    	}
	
    protected LiquidRelayBean(boolean enabled, String destination, String hostname, int port){    	
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
    
    	stompTransport = new StompTransport();
    	stompTransport.setProperties(properties);
    	stompTransport.setMarshaller(marshaller);
    	
    	asyncTransport = new LiquidLegacyTransport(stompTransport);
    	
    	converter = new LiquidRelayExchangeConverterImpl();
    }
    
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
	        	
	        	   	    	    	    	  	   
	        	asyncTransport.send(preMsg);
    		}
    	} catch (Exception e) {
    		
    		stompTransport.destroy();
    		logger.info("Recreating transport because of exception: " + e.getMessage());
		}
		
	}

	public long getCount() {
		return count;
	}



}
