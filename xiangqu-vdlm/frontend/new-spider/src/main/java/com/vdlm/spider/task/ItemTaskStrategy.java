package com.vdlm.spider.task;



/**
 *
 * @author: chenxi
 */

public class ItemTaskStrategy {

	static enum Strategies {
		V1,
		V2,
		W;
		
		public static Strategies fromName(String name) {
			for (final Strategies strategy : Strategies.values()) {
	            if (strategy.name().equals(name)) {
	                return strategy;
	            }
	        }
	        return null;
		}
		
		public static Strategies fromOrdinal(int ordinal) {
			for (final Strategies strategy : Strategies.values()) {
	            if (strategy.ordinal() == ordinal) {
	                return strategy;
	            }
	        }
	        return null;
		}
	}
	
	private Strategies strategy = Strategies.W;

	public Strategies getStrategy() {
		return strategy;
	}

	public void setStrategy(int ordinal) {
		this.strategy = Strategies.fromOrdinal(ordinal);
	}
	
	public void setStrategy(String name) {
		this.strategy = Strategies.fromName(name);
	}

}
