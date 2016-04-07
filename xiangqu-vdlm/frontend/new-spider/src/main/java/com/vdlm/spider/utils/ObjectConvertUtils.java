package com.vdlm.spider.utils;

import java.io.IOException;

import com.vdlm.common.protocol.GsonObjectConverter;
import com.vdlm.common.protocol.ObjectConverter;

/**
 *
 * @author: chenxi
 */

public abstract class ObjectConvertUtils {

	private final static ObjectConverter objectConvert = new GsonObjectConverter();
	
	public static byte[] toBytes(Object value) throws IOException {
		return objectConvert.toBytes(value);
	}

    public static String toString(Object value) throws IOException {
    	return objectConvert.toString(value);
    }

    public static <T> T fromBytes(byte[] value, Class<T> valueType) throws IOException {
    	return objectConvert.fromBytes(value, valueType);
    }

    public static <T> T fromString(String value, Class<T> valueType) throws IOException {
    	return objectConvert.fromString(value, valueType);
    }
}
