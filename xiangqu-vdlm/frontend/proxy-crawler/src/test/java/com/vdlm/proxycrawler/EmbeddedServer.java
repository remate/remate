package com.vdlm.proxycrawler;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.resources.VirtualDirContext;

public class EmbeddedServer {

	public static void main(String[] args) throws Exception {
		final Tomcat tomcat = new Tomcat();
		tomcat.setPort(13080);
		tomcat.setBaseDir("target/tomcat");
		tomcat.getConnector().setURIEncoding("UTF-8");
		final Context ctx = tomcat.addWebapp("/", new File("src/main/webapp").getAbsolutePath());
		tomcat.getConnector().setURIEncoding("UTF-8");

		// declare an alternate location for your "WEB-INF/classes" dir:
		final File additionWebInfClasses = new File("target/classes");
//		final File additionWebInfClasses2 = new File("../new-spider/target/classes");
		final VirtualDirContext resources = new VirtualDirContext();
		resources.setExtraResourcePaths("/WEB-INF/classes=" + additionWebInfClasses);
		ctx.setResources(resources);

		tomcat.start();
		tomcat.getServer().await();
	}
}
