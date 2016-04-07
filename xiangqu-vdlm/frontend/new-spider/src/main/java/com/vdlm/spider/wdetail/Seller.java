package com.vdlm.spider.wdetail;

import java.util.List;

/**
 *
 * @author: chenxi
 */

public class Seller {

	private String userNumId;
	private String type;
	private String nick;
	private String creditLevel;
	private String goodRatePercentage;
	private String shopTitle;
	private String shopId;
	private String weitaoId;
	private String fansCount;
	private String fansCountText;
	private List<EvaluateInfo> evaluateInfo;
	private String bailAmount;
	private String picUrl;
	private String starts;
	private String certificateLogo;
	private List<ActionUnit> actionUnits;
	
	public String getUserNumId() {
		return userNumId;
	}

	public void setUserNumId(String userNumId) {
		this.userNumId = userNumId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getCreditLevel() {
		return creditLevel;
	}

	public void setCreditLevel(String creditLevel) {
		this.creditLevel = creditLevel;
	}

	public String getGoodRatePercentage() {
		return goodRatePercentage;
	}

	public void setGoodRatePercentage(String goodRatePercentage) {
		this.goodRatePercentage = goodRatePercentage;
	}

	public String getShopTitle() {
		return shopTitle;
	}

	public void setShopTitle(String shopTitle) {
		this.shopTitle = shopTitle;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getWeitaoId() {
		return weitaoId;
	}

	public void setWeitaoId(String weitaoId) {
		this.weitaoId = weitaoId;
	}

	public String getFansCount() {
		return fansCount;
	}

	public void setFansCount(String fansCount) {
		this.fansCount = fansCount;
	}

	public String getFansCountText() {
		return fansCountText;
	}

	public void setFansCountText(String fansCountText) {
		this.fansCountText = fansCountText;
	}

	public List<EvaluateInfo> getEvaluateInfo() {
		return evaluateInfo;
	}

	public void setEvaluateInfo(List<EvaluateInfo> evaluateInfo) {
		this.evaluateInfo = evaluateInfo;
	}

	public String getBailAmount() {
		return bailAmount;
	}

	public void setBailAmount(String bailAmount) {
		this.bailAmount = bailAmount;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getStarts() {
		return starts;
	}

	public void setStarts(String starts) {
		this.starts = starts;
	}

	public String getCertificateLogo() {
		return certificateLogo;
	}

	public void setCertificateLogo(String certificateLogo) {
		this.certificateLogo = certificateLogo;
	}

	public List<ActionUnit> getActionUnits() {
		return actionUnits;
	}

	public void setActionUnits(List<ActionUnit> actionUnits) {
		this.actionUnits = actionUnits;
	}

	public String getScore() {
		final List<EvaluateInfo> eifs = getEvaluateInfo();
		final StringBuilder sb = new StringBuilder("");
		if (eifs != null && eifs.size() > 0) {
			EvaluateInfo eif;
			for (int i = 0; i < eifs.size(); i++) {
				eif = eifs.get(0);
				sb.append(eif.getScore());
				if (i < eifs.size() - 1) {
					sb.append("-");
				}
			}
		}
		return sb.toString();
	}
	
	static class EvaluateInfo {
		private String title;
		private String score;
		private String highGap;
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getScore() {
			return score;
		}
		
		public void setScore(String score) {
			this.score = score;
		}
		
		public String getHighGap() {
			return highGap;
		}
		
		public void setHighGap(String highGap) {
			this.highGap = highGap;
		}
		
	}
	
	static class ActionUnit {
		private String name;
		private String value;
		private String url;
		private String track;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public String getUrl() {
			return url;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		
		public String getTrack() {
			return track;
		}
		
		public void setTrack(String track) {
			this.track = track;
		}
		
	}
}
