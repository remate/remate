package com.vdlm.restapi.fragment;

import java.util.List;

import javax.validation.constraints.NotNull;

public class FragmentForm {

	private String id;
	private String name;
	private String description;
	private Boolean showModel;

	@NotNull(message = "{valid.notBlank.message}")
	private List<String> imgs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getShowModel() {
		return showModel;
	}

	public void setShowModel(Boolean showModel) {
		this.showModel = showModel;
	}

	public List<String> getImgs() {
		return imgs;
	}

	public void setImgs(List<String> imgs) {
		this.imgs = imgs;
	}
}
