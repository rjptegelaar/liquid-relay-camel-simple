//Copyright 2015 Paul Tegelaar
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
package com.pte.liquid.relay.consumer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.camel.Exchange;

import com.pte.liquid.relay.Converter;
import com.pte.liquid.relay.Transport;
import com.pte.liquid.relay.camel.util.LiquidRelayCamelUtil;
import com.pte.liquid.relay.exception.RelayException;
import com.pte.liquid.relay.model.Message;

public class ExchangeConsumer implements Runnable{

	private static Logger logger = Logger.getLogger("ExchangeConsumer");
	
	private boolean keepRunning = true;
	private LinkedBlockingQueue<Exchange> queue;	
	private Transport transport;
	private long wait = 1000;
	private boolean enabled;
	private Converter<Exchange> converter;
	
	public ExchangeConsumer(LinkedBlockingQueue<Exchange> queue, Transport transport, Converter<Exchange> converter, boolean enabled){
		this.queue = queue;
		this.transport = transport;
		this.converter = converter;
		this.enabled = enabled;
	}
	
	public void destroy(){
		keepRunning = false;
	}

	public void run() {
		while(keepRunning){
			try {
				Exchange exchange = null;
				while(keepRunning && (exchange = queue.poll())!=null){								
					if(enabled){
			    		
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
				}
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				//Do nothing
			} catch (RelayException e) {
				//Do nothing	
			}
			
			
			
		}			
	}

	
}
