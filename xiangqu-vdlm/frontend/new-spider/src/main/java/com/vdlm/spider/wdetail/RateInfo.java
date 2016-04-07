package com.vdlm.spider.wdetail;

import java.util.List;

/**
 *
 * @author: chenxi
 */

public class RateInfo {

	private String rateCounts;
	private List<RateDetail> rateDetailList;
	private List<Tag> tagList;
	
	public String getRateCounts() {
		return rateCounts;
	}

	public void setRateCounts(String rateCounts) {
		this.rateCounts = rateCounts;
	}

	public List<RateDetail> getRateDetailList() {
		return rateDetailList;
	}

	public void setRateDetailList(List<RateDetail> rateDetailList) {
		this.rateDetailList = rateDetailList;
	}

	public List<Tag> getTagList() {
		return tagList;
	}

	public void setTagList(List<Tag> tagList) {
		this.tagList = tagList;
	}

	static class RateDetail {
		private String nick;
		private String headPic;
		private String star;
		private String feedback;
		private String subInfo;
		
		public String getNick() {
			return nick;
		}
		
		public void setNick(String nick) {
			this.nick = nick;
		}
		public String getHeadPic() {
			return headPic;
		}
		
		public void setHeadPic(String headPic) {
			this.headPic = headPic;
		}
		
		public String getStar() {
			return star;
		}
		
		public void setStar(String star) {
			this.star = star;
		}
		
		public String getFeedback() {
			return feedback;
		}
		
		public void setFeedback(String feedback) {
			this.feedback = feedback;
		}
		
		public String getSubInfo() {
			return subInfo;
		}
		
		public void setSubInfo(String subInfo) {
			this.subInfo = subInfo;
		}
		
	}
	
	static class Tag {
		private String attribute;
		private String title;
		private String count;
		private String score;
	}
}
