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
package com.pte.liquid.relay;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.apache.camel.Exchange;

public class LiquidRelayBean{
	private static Logger logger = Logger.getLogger("LiquidRelayBean");
	
    private Transport transport;
	private Converter<Exchange> converter;
	private Marshaller marshaller;

	private static final int QUEUE_SIZE = 10000;
	private static final int THRESHOLD_SIZE = 500;
	
	private final LinkedBlockingQueue<Exchange> queue = new LinkedBlockingQueue<Exchange>(QUEUE_SIZE);
		
    private boolean enabled;

    private static LiquidRelayBean liquidRelayBean = null;
        
    public static LiquidRelayBean getInstance(boolean enabled, String destination, String hostname, int port) {
    	   if(liquidRelayBean == null) {
    		   liquidRelayBean = new LiquidRelayBean(enabled, destination, hostname, port);
    	   }
    	   return liquidRelayBean;
    }
	
    protected LiquidRelayBean(boolean enabled, String destination, String hostname, int port){    	
    	this.enabled = enabled;
    	
//    	marshaller = new JsonMarshaller();
//    	
//    	 Properties properties = new Properties();
//    	
//        if(destination!=null){
//        	properties.put("relay_destination", destination);
//        }
//        if(hostname!=null){
//        	properties.put("relay_stomp_hostname", hostname);
//        }
//        if(port>0){
//        	properties.put("relay_stomp_port", port);
//        }
//    	
//    	transport = new StompTransport();
//    	transport.setProperties(properties);
//    	transport.setMarshaller(marshaller);
//    	converter = new LiquidRelayExchangeConverterImpl();
    	
    }
    
//	public void process(Exchange exchange) throws Exception {
//		try{				
//			
//			
//			
//    		if(enabled){
//    			    		
//	        	Message preMsg = converter.convert(exchange);  
//	        	String correlationID = LiquidRelayCamelUtil.determineCorrelation(exchange);
//	        	String parentId = LiquidRelayCamelUtil.determineParent(exchange);
//	        	int order = LiquidRelayCamelUtil.determineOrder(exchange);
//	        	String messageID = preMsg.getId();
//	        	
//	        	LiquidRelayCamelUtil.setCorrelationID(correlationID, exchange);
//	        	preMsg.setCorrelationID(correlationID);
//	        	
//	        	LiquidRelayCamelUtil.setParentID(messageID, exchange);
//	        	preMsg.setParentID(parentId);
//	        	
//	        	LiquidRelayCamelUtil.setOrder(order, exchange);
//	        	preMsg.setOrder(order);	
//	        	
//	        	   	    	    	    	  	   
//	        	transport.send(preMsg);
//    		}else{
//    			//Empty by design
//    		}
//    	} catch (Exception e) {
//			//Empty by design
//		}
//		
//	}
    

	
	public void process(Exchange exchange) throws Exception {
		try{				
			if(queue.remainingCapacity() <= THRESHOLD_SIZE){
				logger.warning("Threashold reached, dumping logging message because volume is to high.");
			}else{
				logger.info("Processing exchange.");
				queue.put(exchange);
			}	
			
			
    		
    	} catch (Exception e) {
			//Empty by design
		}
		
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public Converter<Exchange> getConverter() {
		return converter;
	}

	public void setConverter(Converter<Exchange> converter) {
		this.converter = converter;
	}

	public Marshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LinkedBlockingQueue<Exchange> getQueue() {
		return queue;
	}
	
	

}
