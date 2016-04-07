package com.vdlm.common.bus;

/**
 *
 * @auther chenxi
 */

public class Event {

	public Event(String name) {
		name_ = name;
	}

	public String getName() {
		return name_;
	}

	private final String name_;
}
