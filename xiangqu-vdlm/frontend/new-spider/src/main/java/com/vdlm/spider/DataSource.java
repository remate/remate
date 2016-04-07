package com.vdlm.spider;

public enum DataSource {
	MOBILE(1), PC(2);

	private int value;

	private DataSource(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public static DataSource fromName(String name) {
        for (final DataSource from : DataSource.values()) {
            if (from.getValue() == Integer.valueOf(name)) {
                return from;
            }
        }
        return null;
    }

	public static DataSource valueOf(int value) {
		switch (value) {
		case 1:
			return MOBILE;
		case 2:
			return PC;
		default:
			throw new IllegalArgumentException("invalid enum value:" + value);
		}
	}
}
