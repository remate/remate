/**
 * 
 */
package com.vdlm.spider.htmlparser;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.htmlparser.Node;
import org.htmlparser.util.NodeIterator;
import org.junit.Test;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 8:13:37 PM Nov 3, 2014
 */
public class HtmlParserTest {

	@Test
	public void testParser() throws Exception {
		final String htmlContent = FileUtils.readFileToString(new File("C:\\Users\\Think\\Desktop\\weibo_index.html"));

		final org.htmlparser.Parser parser = HtmlParserProvider.createParser(htmlContent);
		
		NodeIterator iterator = parser.elements();
		
		while(iterator.hasMoreNodes()) {
			final Node node = iterator.nextNode();
			
			System.out.println(node);
		}
		
		System.out.println(iterator);
	}
}
