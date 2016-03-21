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
package com.pte.liquid.relay.camel.util;

import java.util.UUID;

import org.apache.camel.Exchange;

import com.pte.liquid.relay.Constants;

public class LiquidRelayCamelUtil {

	 public static String determineCorrelation(Exchange exchange){
			String correlationId = "";	
			if(exchange!=null){
				if(exchange.getIn().getHeader(Constants.CORRELATION_ID_PROPERTY_NAME, String.class)!=null){
					correlationId = exchange.getIn().getHeader(Constants.CORRELATION_ID_PROPERTY_NAME, String.class);
				} else {
					correlationId = UUID.randomUUID().toString();
				}
			}	
			return correlationId;
		}
		
		public static int determineOrder(Exchange exchange){
			int order = 0;	
			if(exchange!=null){
				if(exchange.getIn().getHeader(Constants.ORDER_PROPERTY_NAME, Integer.class)!=null){
					order = exchange.getIn().getHeader(Constants.ORDER_PROPERTY_NAME, Integer.class);
				}			
			}	
			return order;
		}
		
		public static String determineParent(Exchange exchange){
			String parentID = "";	
			if(exchange!=null){
				if(exchange.getIn().getHeader(Constants.PARENT_ID_PROPERTY_NAME, String.class)!=null){
					parentID = exchange.getIn().getHeader(Constants.PARENT_ID_PROPERTY_NAME, String.class);
				}			
			}	
			return parentID;
		}
		
		public static void setCorrelationID(String correlationID, Exchange exchange){		
			exchange.getIn().setHeader(Constants.CORRELATION_ID_PROPERTY_NAME, correlationID);		
		}
		
		public static void setOrder(int order, Exchange exchange){		
			exchange.getIn().setHeader(Constants.ORDER_PROPERTY_NAME, order + 1);		
		}
		
		public static void setParentID(String parentID,Exchange exchange){		
			exchange.getIn().setHeader(Constants.PARENT_ID_PROPERTY_NAME, parentID);		
		}

}
