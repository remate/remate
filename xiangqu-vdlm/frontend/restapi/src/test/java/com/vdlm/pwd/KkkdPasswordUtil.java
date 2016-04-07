package com.vdlm.pwd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;

/**
 * 
 * @author kerns<mijiale@ixiaopu.com>
 * @since 2015年11月2日下午2:06:18
 * 
 */
public class KkkdPasswordUtil {
	private final SecureRandomBytesKeyGenerator saltGenerator = new SecureRandomBytesKeyGenerator();
	private final Digester digester = new Digester("SHA-256", 1024);
	private final byte[] secret = Utf8.encode("pwd.seed!@#$#@!");

	public static void main(String[] args) {
		boolean flag = new KkkdPasswordUtil().matches("42b120e1b35ed5df6423e0ab7c34f875",
				"1ca74f92d81e8263007e266b8aa4f725c9c82c835e4c195d3316877929d1a83100ea09daa3969b98");
		System.out.println(flag);
	}

	@Test
	public void compareXiangquAndKKKDPwd() {
		try {
			InputStream inputStream = KkkdPasswordUtil.class.getResourceAsStream("password.txt");
			InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader buffered=new BufferedReader(streamReader);
			String str="";
			while(true)
			{
				str=buffered.readLine();
				if(str==null)
				{
					break;
				}
				String[] passwords=str.split("\t");
				if(passwords.length!=4)
				{
					continue;
				}
				String kkkdpwd=StringUtils.trim(passwords[2]);
				String xiangqupwd=StringUtils.trim(passwords[3]);
				if(xiangqupwd.length()!=32)
				{
					continue;
				}
				if(!matches(xiangqupwd, kkkdpwd))
				{
					System.out.println(passwords[0]+"\t"+passwords[1]);
				}
			}
					
			
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		byte[] digested = decode(encodedPassword);
		byte[] salt = subArray(digested, 0, saltGenerator.getKeyLength());
		return matches(digested, digest(rawPassword, salt));
	}

	/**
	   * Constant time comparison to prevent against timing attacks.
	   */
	private boolean matches(byte[] expected, byte[] actual) {
		if (expected.length != actual.length) {
			return false;
		}

		int result = 0;
		for (int i = 0; i < expected.length; i++) {
			result |= expected[i] ^ actual[i];
		}
		return result == 0;
	}

	private byte[] decode(CharSequence encodedPassword) {
		return Hex.decode(encodedPassword);
	}

	/**
	   * Extract a sub array of bytes out of the byte array.
	   * @param array the byte array to extract from
	   * @param beginIndex the beginning index of the sub array, inclusive
	   * @param endIndex the ending index of the sub array, exclusive
	   */
	public static byte[] subArray(byte[] array, int beginIndex, int endIndex) {
		int length = endIndex - beginIndex;
		byte[] subarray = new byte[length];
		System.arraycopy(array, beginIndex, subarray, 0, length);
		return subarray;
	}

	private byte[] digest(CharSequence rawPassword, byte[] salt) {
		byte[] digest = digester.digest(concatenate(salt, secret, Utf8.encode(rawPassword)));
		return concatenate(salt, digest);
	}

	/**
	   * Combine the individual byte arrays into one array.
	   */
	public static byte[] concatenate(byte[]... arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] newArray = new byte[length];
		int destPos = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, newArray, destPos, array.length);
			destPos += array.length;
		}
		return newArray;
	}
}
