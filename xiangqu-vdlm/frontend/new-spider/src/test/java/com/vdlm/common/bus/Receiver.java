package com.vdlm.common.bus;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;


/**
 *
 * @auther chenxi
 */

public class Receiver implements BusSignalListener<Event> {

	public Receiver(BusSignalManager bsm) {
		bsm.bind(Event.class, this);
	}

	@Override
	public void signalFired(Event signal) {
		System.out.println(signal.getName());
	}

}
