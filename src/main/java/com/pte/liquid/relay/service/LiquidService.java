package com.pte.liquid.relay.service;

public interface LiquidService {

	void start(String relayDestination, String relayStompHostname, String relayStompPort);

	void stop();

}