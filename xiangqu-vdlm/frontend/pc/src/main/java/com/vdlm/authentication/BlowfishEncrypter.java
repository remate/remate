package com.vdlm.authentication;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Cookie加密算法
 * 
 * @author Taylor xuwei840916@hotmail.com
 * @version 2009-3-10 下午09:37:49
 */
public class BlowfishEncrypter {
	private final static Logger logger = Logger.getLogger(BlowfishEncrypter.class);
	private static String CIPHER_KEY = "iufriend8080";
	private static String CIPHER_NAME = "Blowfish/CFB8/NoPadding";
	private static String KEY_SPEC_NAME = "Blowfish";
	private static SecretKeySpec secretKeySpec = null;
	private static IvParameterSpec ivParameterSpec = null;
	private static final ThreadLocal<BlowfishEncrypter> encrypter_pool = new ThreadLocal<BlowfishEncrypter>();
	Cipher enCipher;
	Cipher deCipher;

	private BlowfishEncrypter() {
		try {
			secretKeySpec = new SecretKeySpec(CIPHER_KEY.getBytes(), KEY_SPEC_NAME);
			ivParameterSpec = new IvParameterSpec((DigestUtils.md5Hex(CIPHER_KEY).substring(0, 8)).getBytes());

			enCipher = Cipher.getInstance(CIPHER_NAME);
			deCipher = Cipher.getInstance(CIPHER_NAME);
			enCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			deCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		} catch (Exception e) {
			logger.error("[BlowfishEncrypter]", e);
		}
	}

	/**
	 * 获取一个加密实例
	 * 
	 * @return
	 */
	public static BlowfishEncrypter getEncrypter() {
		BlowfishEncrypter encrypter = (BlowfishEncrypter) encrypter_pool.get();

		if (encrypter == null) {
			encrypter = new BlowfishEncrypter();
			encrypter_pool.set(encrypter);
		}

		return encrypter;
	}

	/**
	 * 加密
	 * 
	 * @param str
	 * @return
	 */
	public String encrypt(String str) {
		String result = null;

		if (StringUtils.isNotEmpty(str)) {
			try {
				byte[] utf8 = str.getBytes();
				byte[] enc = enCipher.doFinal(utf8);

				result = new String(Base64.encodeBase64(enc));
			} catch (Exception ex) {
				logger.error("Cookie加密出错", ex);
			}
		}

		return result;
	}

	/**
	 * 解密
	 * 
	 * @param str
	 * @return
	 */
	public String decrypt(String str) {
		String result = null;

		if (StringUtils.isNotEmpty(str)) {
			try {
				byte[] dec = Base64.decodeBase64(str.getBytes());
				result = new String(deCipher.doFinal(dec));
			} catch (Exception ex) {
				logger.warn("Cookie == " + str + " == 解密出错 ", ex);
				result = "";
			}
		}

		return result;
	}
}
