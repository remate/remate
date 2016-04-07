package com.vdlm.spider.htmlparser;

import org.htmlparser.Parser;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.DefaultParserFeedback;


public class HtmlParserProvider {
	public static Parser createParser(String inputHTML) {
		Lexer mLexer = new Lexer(new Page(inputHTML));
		HtmlParser parser = new HtmlParser(mLexer, new DefaultParserFeedback(DefaultParserFeedback.QUIET));
		return parser;
	}
}
