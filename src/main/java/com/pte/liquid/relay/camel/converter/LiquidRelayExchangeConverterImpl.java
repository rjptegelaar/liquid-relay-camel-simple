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
package com.pte.liquid.relay.camel.converter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.MessageHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pte.liquid.relay.Constants;
import com.pte.liquid.relay.Converter;
import com.pte.liquid.relay.exception.RelayException;
import com.pte.liquid.relay.model.Message;

public class LiquidRelayExchangeConverterImpl implements Converter<Exchange>{

	private static final transient Logger LOG = LoggerFactory.getLogger(LiquidRelayExchangeConverterImpl.class);

	private final static String ESB_TYPE_PROPERTY_VALUE = "APACHE_CAMEL";
	
	
	//CamelCorrelationId
	
	@Override
	public Message convert(Exchange exchange) throws RelayException {		
		return convertExchange(exchange);
	}
	
	private Message convertExchange(Exchange exchange){			
		Message newMsg = new Message();
		newMsg.setHeader(Constants.ESB_TYPE_PROPERTY_NAME, ESB_TYPE_PROPERTY_VALUE);
		if(exchange!=null){
			
			
			
			//Get message history from message
			List<MessageHistory> list = exchange.getProperty(Exchange.MESSAGE_HISTORY, List.class);
			if(list!=null && list.size()>=2){
				MessageHistory currentStep = list.get(list.size()-2);		
				String label = currentStep.getNode().getLabel();
				String routeID = exchange.getFromRouteId();
				String contextName = exchange.getContext().getName();
				String location = createLocationName(routeID, label, contextName, Constants.LOCATION_SEPERATOR);
				newMsg.setLocation(location);
				
			}
			
			//Set intercept time
			Date now = new Date();
			newMsg.setSnapshotTime(now);
			newMsg.setSnapshotTimeMillis(now.getTime());
			//Set headers
			Map<String, Object> exchangeHeaders = exchange.getIn().getHeaders();
			
			if(exchangeHeaders!=null){
			
				Set<String> headerNames = exchangeHeaders.keySet();
				
				for (String headerName : headerNames) {		
					newMsg.setHeader(headerName, exchange.getIn().getHeader(headerName, String.class));
				}		
			}
						
			//Set body
			if(exchange.getIn().getBody(String.class)!=null){
				newMsg.createPart("EXCHANGE_IN", exchange.getIn().getBody(String.class));	
			}
		}
		return newMsg;
	}
	
	private String createLocationName(String routeID, String label, String contextName, String locationSeperator){
				
		StringBuffer sb = new StringBuffer();
		
		sb.append(contextName);
		sb.append(locationSeperator);
		sb.append(routeID);
		sb.append(locationSeperator);
		sb.append(label);
		
		return sb.toString();
	}
	
	
	

}
