package com.vdlm.spider;

/**
 *
 * @author: chenxi
 */

public enum SpideItemType {

	ITEM,
	MITEM,
	DESC,
	GROUP_IMG,
	SKUS;
	
	public static SpideItemType fromName(String name) {
		for (final SpideItemType type : SpideItemType.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
	}
	
	public static SpideItemType fromOrdinal(int ordinal) {
		for (final SpideItemType type : SpideItemType.values()) {
            if (type.ordinal() == ordinal) {
                return type;
            }
        }
        return null;
	}
}
