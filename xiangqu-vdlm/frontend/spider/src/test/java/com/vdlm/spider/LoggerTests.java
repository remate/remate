/**
 * 
 */
package com.vdlm.spider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:41:43 AM Aug 20, 2014
 */
public class LoggerTests {
	Log log = LogFactory.getLog(getClass());
	Logger log4j = Logger.getLogger(getClass());
	org.slf4j.Logger slf4j = LoggerFactory.getLogger(getClass());

	@Test
	public void testDebug() {
		System.out.println("------ commons-logging ----------");
		System.out.println(log.isDebugEnabled());
		log.debug("commons-logging debug");

		System.out.println("------ log4j ----------");
		System.out.println(log4j.isDebugEnabled());
		log4j.debug("log4j debug");

		System.out.println("------ slf4j ----------");
		System.out.println(slf4j.isDebugEnabled());
		slf4j.debug("slf4j debug");
	}

	@Test
	public void testInfo() {
		System.out.println("------ commons-logging ----------");
		System.out.println(log.isInfoEnabled());
		log.info("commons-logging info");

		System.out.println("------ log4j ----------");
		System.out.println(log4j.isInfoEnabled());
		log4j.info("log4j info");

		System.out.println("------ slf4j ----------");
		System.out.println(slf4j.isInfoEnabled());
		slf4j.info("slf4j info");
	}

	@Test
	public void testWarn() {
		System.out.println("------ commons-logging ----------");
		System.out.println(log.isWarnEnabled());
		log.warn("commons-logging warn");

		System.out.println("------ log4j ----------");
		log4j.warn("log4j warn");

		System.out.println("------ slf4j ----------");
		System.out.println(slf4j.isWarnEnabled());
		slf4j.warn("slf4j warn");
	}

	@Test
	public void testError() {
		System.out.println("------ commons-logging ----------");
		System.out.println(log.isErrorEnabled());
		log.error("commons-logging error");

		System.out.println("------ log4j ----------");
		log4j.debug("log4j debug");
		log4j.info("log4j info");
		log4j.warn("log4j warn");
		log4j.error("log4j error");

		System.out.println("------ slf4j ----------");
		System.out.println(slf4j.isErrorEnabled());
		slf4j.debug("slf4j debug");
		slf4j.info("slf4j info");
		slf4j.warn("slf4j warn");
		slf4j.error("slf4j error");
	}
}
