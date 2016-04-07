package com.vdlm.pwd;

import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * 
 * @author kerns<mijiale@ixiaopu.com>
 * @since 2015年11月24日上午11:46:42
 * 具体的可以在http://10.18.10.103:54321/config.co?group=XIANGQU&dataId=VDLM 里面找
 */
public class KKKDPasswordEncode {
	
	private PasswordEncoder pwdEncoder=new StandardPasswordEncoder("pwd.seed!@#$#@!");
	@Test
	public void test()
	{
		
		System.out.println("e2bb61327a906a0bb04d8a62327cf2dc883b956cfb8f7bc3a811c104e4fd1e835e0b93c3299b9e2d");
		System.out.println(pwdEncoder.encode("1dfaa490d5e0fb6370e5c7715b28171a"));
	}
}
