package com.vdlm.restapi.order;

import java.util.List;

import com.vdlm.dal.model.OrderMessage;

public class OrderMessageVO {

	private OrderMessage leaderMsg;
	
	private List<OrderMessage> repsMsg;

	public OrderMessage getLeaderMsg() {
		return leaderMsg;
	}

	public void setLeaderMsg(OrderMessage leaderMsg) {
		this.leaderMsg = leaderMsg;
	}

	public List<OrderMessage> getRepsMsg() {
		return repsMsg;
	}

	public void setRepsMsg(List<OrderMessage> repsMsg) {
		this.repsMsg = repsMsg;
	}
	
}
