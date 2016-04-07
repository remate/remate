package com.vdlm.bos.thymeleaf.dialect;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;

import com.vdlm.biz.res.ResourceFacade;

/**
 * 静态资源src url转换。
 * 
 * @author jamesp
 */
public abstract class VdResourceProcessor extends AbstractStandardSingleAttributeModifierAttrProcessor {

	public static final int ATTR_PRECEDENCE = 1000;
	private final String attributeName;
	private final ResourceFacade resourceFacade;

	public VdResourceProcessor(String attributeName, ResourceFacade resourceFacade) {
		super(attributeName);
		this.attributeName = attributeName;
		this.resourceFacade = resourceFacade;
	}

	@Override
	protected String getTargetAttributeValue(Arguments arguments, Element element, String attributeName) {
		String val = element.getAttributeValue(attributeName);
		try {
			final Configuration configuration = arguments.getConfiguration();
			final IStandardExpression expression = StandardExpressions.getExpressionParser(configuration)
					.parseExpression(configuration, arguments, val);
			final Object result = expression.execute(configuration, arguments);
			val = result == null ? "" : result.toString();
		} catch (TemplateProcessingException e) {
			//
		}
		return resourceFacade.resolveUrl(val);
	}

	@Override
	protected String getTargetAttributeName(Arguments arguments, Element element, String attributeName) {
		return this.attributeName;
	}

	@Override
	protected ModificationType getModificationType(Arguments arguments, Element element, String attributeName,
			String newAttributeName) {
		return ModificationType.SUBSTITUTION;
	}

	@Override
	protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String attributeName,
			String newAttributeName) {
		return false;
	}

	@Override
	public int getPrecedence() {
		return ATTR_PRECEDENCE;
	}
}
