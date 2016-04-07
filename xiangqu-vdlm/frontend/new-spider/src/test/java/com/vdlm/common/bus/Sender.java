package com.vdlm.common.bus;

import java.util.Date;

import com.vdlm.common.bus.BusSignalManager;


/**
 *
 * @auther chenxi
 */

public class Sender {

	private final BusSignalManager bsm;
	
	public Sender(BusSignalManager bsm) {
		this.bsm = bsm;
	}

	public void send() {
		//bsm.signal(new Event("test"));
		bsm.signal(new Event(new Date().toString()));
	}

}
