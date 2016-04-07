/**
 * 
 */
package com.vdlm.spider;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:11:15 PM Jul 19, 2014
 */
public abstract class Statics {

	public static final String ENCODE = "GBK";
	
	public static final String UTF8 = "UTF-8";

	public static final String[] IMG_SUFFIX = { ".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG", ".gif", ".GIF",
			".bmp", ".BMP" };

	public static final int NORMAL = 1;
	public static final int INCOMPLETED_INFO = 2;
	public static final int SOLD_OUT = 3;
	public static final int NOT_FOUND = 4;

	public static final int IMG_GROUP = 1;
	public static final int IMG_SKU = 2;
	public static final int IMG_DETAIL = 3;

	public static final String HTTP_URL_PREFIX = "http://";

	public static final int ONE_DAY_SECONDS = 24 * 60 * 60;
}
