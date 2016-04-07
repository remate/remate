/**
 * 
 */
package com.vdlm.spider;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.druid.filter.config.ConfigTools;
import com.vdlm.dal.mybatis.IdTypeHandler;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:32:07 PM Jul 17, 2014
 */
public class SimpleTests {
	@Test
	public void outPrint() throws Exception {
		final File file = new File("C:\\TempFiles\\productId.txt");
		final Set<String> set = new HashSet<String>();
		set.addAll(FileUtils.readLines(file));
		System.out.println(set.size());
		for (String line : set) {
			System.out.println(line + "=1");
		}
	}

	@Test
	public void testEncodeDruid() throws Exception {
		System.out.println(ConfigTools.encrypt("S1P2fFfS"));
	}

	@Test
	public void testUrlEncode() throws Exception {
		HtmlUtils.htmlUnescape("");
		System.out.println(URLEncoder.encode("&", "utf-8"));
	}

	@Test
	public void testEncode() {
		String ids = "16640079";
		for (String id : ids.split(",")) {
			System.out.print(IdTypeHandler.encode(Long.parseLong(id)));
			System.out.print(',');
		}
	}

	@Test
	public void testEncode2() throws Exception {
		for (String id : FileUtils.readLines(new File("C:\\Users\\Think\\Desktop\\productId.txt"))) {
			System.out.print("'");
			System.out.print(IdTypeHandler.encode(Long.parseLong(id)));
			System.out.print("',");
		}
	}

	@Test
	public void testDecode() {
		String ids = "3n9gyo";
		for (String id : ids.split(";")) {
			System.out.print(IdTypeHandler.decode(id));
			System.out.print(',');
		}
	}

	@Test
	public void testDecode2() throws Exception {
		for (String id : FileUtils.readLines(new File("C:\\Users\\Think\\Desktop\\productId.txt"))) {
			System.out.print(IdTypeHandler.decode(id));
			System.out.print(',');
		}
	}

	@Test
	public void testLength() {
		System.out
				.println("http://gi3.md.alicdn.com/imgextra/i3/1779071637/TB2TU.OXVXXXXbVXpXXXXXXXXXX_!!1779071637.jpg_60x60q90.jpg"
						.length());
	}
}
