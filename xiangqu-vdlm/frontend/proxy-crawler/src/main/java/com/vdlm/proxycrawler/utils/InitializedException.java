/**
 * 
 */
package com.vdlm.proxycrawler.utils;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:21:38 PM Jul 20, 2014
 */
public class InitializedException extends RuntimeException {

	private static final long serialVersionUID = 7058196094587702897L;

	public InitializedException() {
		super();
	}

	public InitializedException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializedException(String message) {
		super(message);
	}

	public InitializedException(Throwable cause) {
		super(cause);
	}

}
