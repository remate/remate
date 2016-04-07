package com.vdlm.pwd;

import java.security.SecureRandom;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;

/**
 * 
 * @author kerns<mijiale@ixiaopu.com>
 * @since 2015年11月3日下午2:21:44
 */
public class SecureRandomBytesKeyGenerator implements BytesKeyGenerator{
	private final SecureRandom random;

	private final int keyLength;

	/**
	 * Creates a secure random key generator using the defaults.
	 */
	public SecureRandomBytesKeyGenerator() {
		this(DEFAULT_KEY_LENGTH);
	}

	/**
	 * Creates a secure random key generator with a custom key length.
	 */
	public SecureRandomBytesKeyGenerator(int keyLength) {
		this.random = new SecureRandom();
		this.keyLength = keyLength;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public byte[] generateKey() {
		byte[] bytes = new byte[keyLength];
		random.nextBytes(bytes);
		return bytes;
	}

	private static final int DEFAULT_KEY_LENGTH = 8;
}
