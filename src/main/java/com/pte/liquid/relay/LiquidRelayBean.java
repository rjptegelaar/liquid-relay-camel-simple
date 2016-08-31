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
	
	private static final int QUEUE_SIZE = 10000;
	private static final int THRESHOLD = 500;
	
	private final LinkedBlockingQueue<Exchange> queue = new LinkedBlockingQueue<Exchange>(QUEUE_SIZE);
		
    private boolean enabled;

    private static LiquidRelayBean liquidRelayBean = null;
        
    public static LiquidRelayBean getInstance(boolean enabled) {
    	   if(liquidRelayBean == null) {
    		   liquidRelayBean = new LiquidRelayBean(enabled);
    	   }
    	   return liquidRelayBean;
    }
	
    protected LiquidRelayBean(boolean enabled){    	
    	this.enabled = enabled;
    }	
    
    
	public void process(Exchange exchange) throws Exception {
		try{	
			if(enabled){
				if(queue.remainingCapacity() <= THRESHOLD){
					logger.warning("Threashold reached, dumping logging message because volume is to high.");
				}else{
					logger.info("Processing exchange.");
					queue.put(exchange);
				}					
			}						    		
    	} catch (Exception e) {
			//Empty by design
		}
		
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
