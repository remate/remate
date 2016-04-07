/**
 * 
 */
package com.vdlm.spider;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:28:58 PM Aug 11, 2014
 */
public enum DeviceType {
	ANDROID(1), IOS(2), OTHER(3);

	private int value;

	private DeviceType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public static DeviceType fromName(String name) {
        for (final DeviceType type : DeviceType.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }

	public static DeviceType valueOf(int value) {
		switch (value) {
		case 1:
			return ANDROID;
		case 2:
			return IOS;
		case 3:
			return OTHER;
		default:
			throw new IllegalArgumentException("invalid enum value:" + value);
		}
	}
}
