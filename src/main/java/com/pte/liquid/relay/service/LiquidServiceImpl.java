package com.pte.liquid.relay.service;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.camel.Exchange;

import com.pte.liquid.relay.Converter;
import com.pte.liquid.relay.LiquidRelayBean;
import com.pte.liquid.relay.Marshaller;
import com.pte.liquid.relay.Transport;
import com.pte.liquid.relay.camel.converter.LiquidRelayExchangeConverterImpl;
import com.pte.liquid.relay.client.stomp.StompTransport;
import com.pte.liquid.relay.consumer.ExchangeConsumer;
import com.pte.liquid.relay.marshaller.json.JsonMarshaller;

public class LiquidServiceImpl implements LiquidService {

	private static Logger logger = Logger.getLogger("LiquidServiceImpl");
	
	private ExchangeConsumer consumer;
	private LiquidRelayBean producer;
	private boolean enabled = true;
	private static final String DESTINATION = "com.pte.liquid.relay.json.in";
	private static final String HOSTNAME = "localhost";
	private static final int PORT = 33555;
    private Transport transport;
	private Converter<Exchange> converter;
	private Marshaller marshaller;
	private String relayDestination, relayStompHostname, relayStompPort;
	
	
	public LiquidServiceImpl(String relayDestination, String relayStompHostname, String relayStompPort){
		this.relayDestination = relayDestination;
		this.relayStompHostname = relayStompHostname;
		this.relayStompPort = relayStompPort;
		this.start();
	}
	
	@Override
	public void start(){
    	logger.info("Starting liquid interceptor.");
    	//Creating marshaller
        marshaller = new JsonMarshaller();

    	//Creating transport
    	Properties properties = new Properties();
    	
        if(relayDestination!=null){
        	properties.put("relay_destination", relayDestination);
        	logger.info("Getting relay_destination from property file");
        }else{
        	properties.put("relay_destination", DESTINATION);
        	logger.info("Using default relay_destination");
        }
    	
    	
        if(relayStompHostname!=null){
        	properties.put("relay_stomp_hostname", relayStompHostname);
        	logger.info("Getting relay_stomp_hostname from property file");
        }else{
        	properties.put("relay_stomp_hostname", HOSTNAME);
        	logger.info("Using default relay_stomp_hostname");
        }
        
        if(relayStompPort!=null){
        	properties.put("relay_stomp_port", relayStompPort);
        	logger.info("Getting relay_stomp_port from property file");
        }else{
        	properties.put("relay_stomp_port", PORT);
        	logger.info("Using default relay_stomp_port");
        }
        
        transport = new StompTransport();
    	transport.setProperties(properties);
    	transport.setMarshaller(marshaller);
    	
    	//Creating producer
        producer = LiquidRelayBean.getInstance();
    	
    	
    	//Creating converter
    	converter = new LiquidRelayExchangeConverterImpl();
    	
    	//Starting consumer
    	consumer = new ExchangeConsumer(producer.getQueue(), transport, converter, enabled);
    	logger.info("Started liquid interceptor.");
    	  
	}
	
	@Override
	public void stop(){
    	logger.info("Stopping liquid interceptor.");
    	consumer.destroy();
    	transport.destroy();
    	logger.info("Stopped liquid interceptor.");
	}
	
}
