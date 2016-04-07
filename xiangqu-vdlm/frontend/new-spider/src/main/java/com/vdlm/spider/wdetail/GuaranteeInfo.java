package com.vdlm.spider.wdetail;

import java.util.List;

/**
 *
 * @author: chenxi
 */

public class GuaranteeInfo {

	private List<Guarantee> guarantees;
	
	public List<Guarantee> getGuarantees() {
		return guarantees;
	}

	public void setGuarantees(List<Guarantee> guarantees) {
		this.guarantees = guarantees;
	}

	static class Guarantee {
		private String title;
		private String icon; // FIXME
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getIcon() {
			return icon;
		}
		
		public void setIcon(String icon) {
			this.icon = icon;
		}
		
	}
}
