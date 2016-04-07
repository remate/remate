/**
 * 
 */
package com.vdlm.spider;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:52:08 PM Jul 26, 2014
 */
public enum ParserType {

	TAOBAO_ITEM(1), TMALL_ITEM(2), TAOBAO_SEARCH(3), TMALL_SEARCH(4), TAOBAO_INDEX(5), TMALL_INDEX(6), TAOBAO_ITEMLIST(
			7), TMALL_ITEMLIST(8);

	private int value;

	private ParserType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public static ParserType valueOf(int value) {
		switch (value) {
		case 1:
			return TAOBAO_ITEM;
		case 2:
			return TMALL_ITEM;
		case 3:
			return TAOBAO_SEARCH;
		case 4:
			return TMALL_SEARCH;
		case 5:
			return TAOBAO_INDEX;
		case 6:
			return TMALL_INDEX;
		case 7:
			return TAOBAO_ITEMLIST;
		case 8:
			return TMALL_ITEMLIST;
		default:
			throw new IllegalArgumentException("invalid enum value:" + value);
		}
	}

	public static ParserType index(ShopType shopType) {
		switch (shopType) {
		case TMALL:
			return ParserType.TMALL_INDEX;
		default:
			return ParserType.TAOBAO_INDEX;
		}
	}

	public static ParserType item(ShopType shopType) {
		switch (shopType) {
		case TMALL:
			return ParserType.TMALL_ITEM;
		default:
			return ParserType.TAOBAO_ITEM;
		}
	}
	
	public static ParserType search(ShopType shopType) {
		switch (shopType) {
		case TMALL:
			return ParserType.TMALL_SEARCH;
		default:
			return ParserType.TAOBAO_SEARCH;
		}
	}
}
