/**
 * 
 */
package com.vdlm.spider;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:16:06 AM Jul 19, 2014
 */
public enum ShopType {

	TAOBAO(1), TMALL(2), TAOBAO_OR_TMALL(3);//, TAOBAO_INDEX(4), TMALL_INDEX(5);

	private int value;

	private ShopType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public static ShopType valueOf(int value) {
		switch (value) {
		case 1:
			return TAOBAO;
		case 2:
			return TMALL;
		case 3:
			return TAOBAO_OR_TMALL;
//		case 4:
//			return TAOBAO_INDEX;
//		case 5:
//			return TMALL_INDEX;
		default:
			throw new IllegalArgumentException("invalid enum value:" + value);
		}
	}

//	public static ShopType toIndex(ShopType shopType) {
//		switch (shopType) {
//		case TAOBAO:
//			return TAOBAO_INDEX;
//		case TMALL:
//			return TMALL_INDEX;
//		default:
//			return null;
//		}
//	}

//	public static ShopType indexTo(ShopType shopType) {
//		switch (shopType) {
//		case TAOBAO_INDEX:
//			return TAOBAO;
//		case TMALL_INDEX:
//			return TMALL;
//		default:
//			return shopType;
//		}
//	}
}
