package com.vdlm.restapi.order;


/**
 * 订单发货Form表单逻辑
 * 
 * @author odin
 */
public class OrderShippedForm extends OrderForm {
	
	/**
	 * 物流公司
	 */
	// @NotNull(message = "{logisticsCompany.notBlank.message}")
	private String logisticsCompany;

	/**
	 * 物流订单号
	 */
	// @NotBlank(message = "{logisticsOrderNo.notBlank.message}")
	private String logisticsOrderNo;

	public String getLogisticsOrderNo() {
		return logisticsOrderNo;
	}

	public void setLogisticsOrderNo(String logisticsOrderNo) {
		this.logisticsOrderNo = logisticsOrderNo;
	}

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }
}
