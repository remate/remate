package com.vdlm.config;

import javax.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpSession;

public class SessionWrapper extends MockHttpSession {
	private final HttpSession httpSession;

	public SessionWrapper(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	@Override
	public Object getAttribute(String name) {
		return this.httpSession.getAttribute(name);
	}

}